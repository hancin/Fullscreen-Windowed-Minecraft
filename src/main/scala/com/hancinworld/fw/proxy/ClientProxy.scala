//Copyright (c) 2015-2017, David Larochelle-Pratte
//All rights reserved.
//
//        Redistribution and use in source and binary forms, with or without
//        modification, are permitted provided that the following conditions are met:
//
//        1. Redistributions of source code must retain the above copyright notice, this
//        list of conditions and the following disclaimer.
//        2. Redistributions in binary form must reproduce the above copyright notice,
//        this list of conditions and the following disclaimer in the documentation
//        and/or other materials provided with the distribution.
//
//        THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//        ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//        WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//        DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
//        ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//        (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//        LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
//        ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//        (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//        SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
package com.hancinworld.fw.proxy

import java.awt._
import java.io.File

import com.hancinworld.fw.FullscreenWindowed
import com.hancinworld.fw.handler.{ConfigurationHandler, DrawScreenEventHandler, KeyInputEventHandler}
import com.hancinworld.fw.reference.Reference
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.SplashProgress
import org.lwjgl.LWJGLException
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.{Display, DisplayMode}

object ClientProxy {
  var currentState: Boolean = false
  var fullscreenKeyBinding: KeyBinding = null

  /** This keybind replaces the default MC fullscreen keybind in their logic handler. Without it, the game crashes.
    * If this is set to any valid key, problems may occur. */
  val ignoreKeyBinding = new KeyBinding("key.fullscreenwindowed.unused", Keyboard.KEY_NONE, "key.categories.misc")
}

class ClientProxy extends IProxy {
  private val startupRequestedSetting = Minecraft.getMinecraft().gameSettings.fullScreen || Display.isFullscreen
  Minecraft.getMinecraft().gameSettings.fullScreen = false
  private var savedWindowedBounds: Rectangle = null

  def isCorrectKeyPressed: Boolean = ClientProxy.fullscreenKeyBinding != null && (ClientProxy.fullscreenKeyBinding.isPressed || Keyboard.isKeyDown(ClientProxy.fullscreenKeyBinding.getKeyCode))

  def registerKeyBindings = {
    val mc = Minecraft.getMinecraft
    if (ClientProxy.fullscreenKeyBinding == null && ConfigurationHandler.fullscreenWindowedEnabled) {
      ClientProxy.fullscreenKeyBinding = mc.gameSettings.keyBindFullscreen
      mc.gameSettings.keyBindFullscreen = ClientProxy.ignoreKeyBinding
    }
    else if (ClientProxy.fullscreenKeyBinding != null && !ConfigurationHandler.fullscreenWindowedEnabled) {
      mc.gameSettings.keyBindFullscreen = ClientProxy.fullscreenKeyBinding
      ClientProxy.fullscreenKeyBinding = null

      if (ClientProxy.currentState)
        mc.toggleFullscreen()
    }
  }

  def subscribeEvents(configurationFile: File) = {
    ConfigurationHandler.init(configurationFile)
    MinecraftForge.EVENT_BUS.register(ConfigurationHandler)
    MinecraftForge.EVENT_BUS.register(new KeyInputEventHandler())
    MinecraftForge.EVENT_BUS.register(new DrawScreenEventHandler())
  }

  /**
    * Find which monitor contains a specific (x,y) point
    *
    * @param x X coordinate
    * @param y Y coordinate
    * @return The monitor containing the point, or the primary monitor if none is found.
    */
  private def findCurrentScreenDimensionsAndPosition(x: Int, y: Int): Rectangle = {
    val screens = GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices
    for (dev <- screens) {
      val bounds = dev.getDefaultConfiguration.getBounds
      if (bounds.contains(x, y)) return bounds
    }
    //if Java isn't able to find a matching screen then use the old LWJGL calcs.
    return new Rectangle(0, 0, Display.getDesktopDisplayMode.getWidth, Display.getDesktopDisplayMode.getHeight)
  }

  /**
    * Get monitor dimensions and position for monitor with the specified id
    *
    * @param monitorID the monitor ID (1 is the primary monitor)
    * @return The screen dimensions if available, or null if none is found
    */
  private def findScreenDimensionsByID(monitorID: Int): Rectangle = {
    if (monitorID < 1) return null
    val env = GraphicsEnvironment.getLocalGraphicsEnvironment
    val screens = env.getScreenDevices
    if (screens == null || screens.length == 0 || screens.length < monitorID) return null
    screens(monitorID - 1).getDefaultConfiguration.getBounds
  }

  /**
    * Use the current window size and location, and configuration, to figure out what fullscreen dimensions should be
    *
    * @param currentCoordinates The current window size & location
    * @param desiredMonitor     The desired monitor for fullscreen
    * @return The preferred fullscreen dimensions & location
    */
  private def getAppropriateScreenBounds(currentCoordinates: Rectangle, desiredMonitor: Int): Rectangle = {
    val centerCoordinates = new Point((currentCoordinates.getMinX + currentCoordinates.getWidth / 2).toInt, (currentCoordinates.getMinY + currentCoordinates.getHeight / 2).toInt)

    ConfigurationHandler match {
      //First feature mode: Only remove decorations. No need to calculate screen positions, we're not changing size or location.
      case _ if ConfigurationHandler.advancedFeaturesEnabled && ConfigurationHandler.onlyRemoveDecorations => currentCoordinates
      //Custom dimensions enabled: follow requested settings if we can work with them.
      case _ if ConfigurationHandler.advancedFeaturesEnabled && ConfigurationHandler.customFullscreenDimensions && ConfigurationHandler.customFullscreenDimensionsH > 256 && ConfigurationHandler.customFullscreenDimensionsW > 256 => {
        val rect = new Rectangle(ConfigurationHandler.customFullscreenDimensionsX, ConfigurationHandler.customFullscreenDimensionsY, ConfigurationHandler.customFullscreenDimensionsW, ConfigurationHandler.customFullscreenDimensionsH)
        if (desiredMonitor > 0) {
          val actualScreenBounds = findScreenDimensionsByID(desiredMonitor)
          if (actualScreenBounds != null) {
            rect.setLocation(actualScreenBounds.x + rect.x, actualScreenBounds.y + rect.y)
          }
        }
        rect
      }
      // No specified monitor for fullscreen -> find the one the window is on right now
      case _ if desiredMonitor < 0 || desiredMonitor == Reference.AUTOMATIC_MONITOR_SELECTION => findCurrentScreenDimensionsAndPosition(centerCoordinates.getX.toInt, centerCoordinates.getY.toInt)
      // specified monitor for fullscreen -> get dimensions.
      case _ => Option(findScreenDimensionsByID(desiredMonitor)).getOrElse(findCurrentScreenDimensionsAndPosition(centerCoordinates.getX.toInt, centerCoordinates.getY.toInt))
    }

  }

  def toggleFullScreen = toggleFullScreen(!ClientProxy.currentState, ConfigurationHandler.fullscreenMonitor)
  /**
    * Toggle fullscreen to the desired state on the desired monitor
    *
    * @param goFullScreen   true to go full screen, false to go windowed
    * @param desiredMonitor desired monitor or 0 for the current/default option.
    */
  def toggleFullScreen(goFullScreen: Boolean, desiredMonitor: Int): Unit = {
    //If we're in actual fullscreen right now, then we need to fix that.
    if (Display.isFullscreen) {
      ClientProxy.currentState = true
      FullscreenWindowed.log.warn("Display is actual fullscreen! Is Minecraft starting with the option set?")
    }

    // if we have nothing to do (we appear to be in the correct mode) and we're not in actual fullscreen ( that's
    // never an acceptable state in this mod ), just quit now.
    if (ClientProxy.currentState == goFullScreen && !Display.isFullscreen)
      return

    //Save our current display parameters
    val currentCoordinates = new Rectangle(Display.getX, Display.getY, Display.getWidth, Display.getHeight)

    if (goFullScreen)
      savedWindowedBounds = currentCoordinates

    //Changing this property and causing a Display update will cause LWJGL to add/remove decorations (borderless).
    System.setProperty("org.lwjgl.opengl.Window.undecorated", if (goFullScreen) "true" else "false")
    //Get the fullscreen dimensions for the appropriate screen.
    val screenBounds = getAppropriateScreenBounds(currentCoordinates, desiredMonitor)
    //This is the new bounds we have to apply.
    var newBounds = if (goFullScreen) screenBounds else savedWindowedBounds
    if (newBounds == null)
      newBounds = screenBounds
    try {
      Display.setDisplayMode(new DisplayMode(newBounds.getWidth.toInt, newBounds.getHeight.toInt))
      Display.setResizable(!goFullScreen)
      Display.setFullscreen(false)
      Display.update()
      Display.setLocation(newBounds.x, newBounds.y)
      Minecraft.getMinecraft.resize(newBounds.getWidth.toInt, newBounds.getHeight.toInt)
    }
    catch {
      case e: LWJGLException => {
        e.printStackTrace()
      }
    }

    ClientProxy.currentState = goFullScreen
  }

  /**
    * Perform startup checks and setup the game so it goes fullscreen if required
    */
  def performStartupChecks = {
    //If the mod is disabled by configuration, just put back the initial value.
    if (!ConfigurationHandler.fullscreenWindowedEnabled) {
      Minecraft.getMinecraft.gameSettings.fullScreen = startupRequestedSetting
    }

    if (ConfigurationHandler.fullscreenWindowedEnabled && !ConfigurationHandler.maximumCompatibilityEnabled) try {
      //FIXME: Living dangerously here... Is there a better way of doing this?
      SplashProgress.pause()
      toggleFullScreen(startupRequestedSetting, ConfigurationHandler.fullscreenMonitor)
      SplashProgress.resume()

    } catch {
      case e: NoClassDefFoundError => {
        FullscreenWindowed.log.warn("Error while doing startup checks, are you using an old version of Forge ? " + e)
        toggleFullScreen(startupRequestedSetting, ConfigurationHandler.fullscreenMonitor)
      }
    }
  }

  /**
    * Toggle fullscreen from the @DrawScreenEventHandler
    */
  def handleInitialFullscreen = {
    if (ConfigurationHandler.maximumCompatibilityEnabled)
      toggleFullScreen(startupRequestedSetting, ConfigurationHandler.fullscreenMonitor)
  }

}