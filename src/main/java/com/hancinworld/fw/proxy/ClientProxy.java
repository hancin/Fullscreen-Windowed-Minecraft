//Copyright (c) 2015, David Larochelle-Pratte
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
package com.hancinworld.fw.proxy;

import com.hancinworld.fw.handler.ConfigurationHandler;
import com.hancinworld.fw.handler.DrawScreenEventHandler;
import com.hancinworld.fw.handler.KeyInputEventHandler;
import com.hancinworld.fw.reference.Reference;
import com.hancinworld.fw.utility.LogHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.SplashProgress;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;
import java.io.File;

public class ClientProxy extends CommonProxy {

    private final Minecraft client = Minecraft.getMinecraft();
    private Rectangle _savedWindowedBounds;
    public static boolean fullscreen;
    public static KeyBinding fullscreenKeyBinding;
    public DrawScreenEventHandler dsHandler;

    /** This keybind replaces the default MC fullscreen keybind in their logic handler. Without it, the game crashes.
     *  If this is set to any valid key, problems may occur. */
    public static KeyBinding ignoreKeyBinding = new KeyBinding("key.fullscreenwindowed.unused", Keyboard.KEY_NONE, "key.categories.misc");


    public ClientProxy()
    {
    }

    @Override
    public void registerKeyBindings()
    {
        /* FIXME: Overrides the minecraft hotkey for fullscreen, as there are no hooks */
        if(fullscreenKeyBinding == null && ConfigurationHandler.instance().isFullscreenWindowedEnabled())
        {
            fullscreenKeyBinding = client.gameSettings.keyBindFullscreen;
            client.gameSettings.keyBindFullscreen = ignoreKeyBinding;

        }
        else if(fullscreenKeyBinding != null && !ConfigurationHandler.instance().isFullscreenWindowedEnabled())
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.gameSettings.keyBindFullscreen = fullscreenKeyBinding;
            fullscreenKeyBinding = null;

            if(fullscreen){
                mc.fullscreen = false;
                mc.toggleFullscreen();
            }
        }
    }

    @Override
    public void subscribeEvents(File configurationFile) {

        ConfigurationHandler.instance().init(configurationFile);
        FMLCommonHandler.instance().bus().register(ConfigurationHandler.instance());
        FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());
        dsHandler = new DrawScreenEventHandler();
        MinecraftForge.EVENT_BUS.register(dsHandler);
    }

    private Rectangle findCurrentScreenDimensionsAndPosition(int x, int y)
    {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = env.getScreenDevices();

        for(GraphicsDevice dev : screens)
        {
            GraphicsConfiguration displayMode = dev.getDefaultConfiguration();
            Rectangle bounds = displayMode.getBounds();

            if(bounds.contains(x, y))
                return bounds;
        }

        //if Java isn't able to find a matching screen then use the old LWJGL calcs.
        return new Rectangle(0, 0, Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight());
}
    private Rectangle findScreenDimensionsByID(int monitorID)
    {
        if(monitorID < 1)
            return null;

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = env.getScreenDevices();

        if(screens == null || screens.length == 0 || screens.length < monitorID){
            return null;
        }

        return screens[monitorID - 1].getDefaultConfiguration().getBounds();
    }

    private Rectangle getAppropriateScreenBounds(Rectangle currentCoordinates, int desiredMonitor)
    {
        Rectangle screenBounds;
        Point centerCoordinates = new Point((int) (currentCoordinates.getMinX() + currentCoordinates.getWidth() / 2), (int) (currentCoordinates.getMinY() + currentCoordinates.getHeight() / 2));

        ConfigurationHandler configuration = ConfigurationHandler.instance();
        //First feature mode: Only remove decorations. No need to calculate screen positions, we're not changing size or location.
        if(configuration.areAdvancedFeaturesEnabled() && configuration.isOnlyRemoveDecorations()){
            screenBounds = currentCoordinates;
        }
        //Custom dimensions enabled: follow requested settings if we can work with them.
        else if(configuration.areAdvancedFeaturesEnabled() && configuration.isCustomFullscreenDimensions() && (configuration.getCustomFullscreenDimensionsH() > 256 && configuration.getCustomFullscreenDimensionsW() > 256))
        {
            screenBounds = new Rectangle(configuration.getCustomFullscreenDimensionsX(),configuration.getCustomFullscreenDimensionsY(), configuration.getCustomFullscreenDimensionsW(),configuration.getCustomFullscreenDimensionsH());

            //If you've selected a monitor, then X & Y are offsets - easier to do math.
            if(desiredMonitor > 0) {
                Rectangle actualScreenBounds = findScreenDimensionsByID(desiredMonitor);
                if(actualScreenBounds != null){
                    screenBounds.setLocation(actualScreenBounds.x + screenBounds.x, actualScreenBounds.y + screenBounds.y);
                }
            }
        }
        // No specified monitor for fullscreen -> find the one the window is on right now
        else if(desiredMonitor < 0 || desiredMonitor == Reference.AUTOMATIC_MONITOR_SELECTION) {
            //find which monitor we should be using based on the center of the MC window
            screenBounds = findCurrentScreenDimensionsAndPosition((int) centerCoordinates.getX(), (int) centerCoordinates.getY());
        // specified monitor for fullscreen -> get dimensions.
        }else{
            screenBounds = findScreenDimensionsByID(desiredMonitor);
            // you've specified a monitor but it doesn't look connected. Revert to automatic mode.
            if(screenBounds == null){
                screenBounds = findCurrentScreenDimensionsAndPosition((int) centerCoordinates.getX(), (int) centerCoordinates.getY());
            }
        }

        return screenBounds;
    }
    @Override
    public void toggleFullScreen(boolean goFullScreen) {
        toggleFullScreen(goFullScreen, ConfigurationHandler.instance().getFullscreenMonitor());
    }

    @Override
    public void toggleFullScreen(boolean goFullScreen, int desiredMonitor) {

        //Set value if it isn't set already.
        if(System.getProperty("org.lwjgl.opengl.Window.undecorated") == null){
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
        }

        //If we're in actual fullscreen right now, then we need to fix that.
        if(Display.isFullscreen()) {
            fullscreen = true;
        }

        String expectedState = goFullScreen ? "true":"false";
        // If all state is valid, there is nothing to do and we just exit.
        if(fullscreen == goFullScreen
                && !Display.isFullscreen()//Display in fullscreen mode: Change required
                && System.getProperty("org.lwjgl.opengl.Window.undecorated") == expectedState // Window not in expected state
        )
            return;

        //Save our current display parameters
        Rectangle currentCoordinates = new Rectangle(Display.getX(), Display.getY(), Display.getWidth(), Display.getHeight());
        if(goFullScreen && !Display.isFullscreen())
            _savedWindowedBounds = currentCoordinates;

        //Changing this property and causing a Display update will cause LWJGL to add/remove decorations (borderless).
        System.setProperty("org.lwjgl.opengl.Window.undecorated",expectedState);

        //Get the fullscreen dimensions for the appropriate screen.
        Rectangle screenBounds = getAppropriateScreenBounds(currentCoordinates, desiredMonitor);

        //This is the new bounds we have to apply.
        Rectangle newBounds = goFullScreen ? screenBounds : _savedWindowedBounds;
        if(newBounds == null)
            newBounds = screenBounds;

        if(goFullScreen == false && ClientProxy.fullscreen == false) {
            newBounds = currentCoordinates;
            _savedWindowedBounds = currentCoordinates;
        }

        try {
            fullscreen = goFullScreen;
            client.fullscreen = fullscreen;
            if( client.gameSettings.fullScreen != fullscreen) {
                client.gameSettings.fullScreen = fullscreen;
                client.gameSettings.saveOptions();
            }
            Display.setFullscreen(false);
            Display.setDisplayMode(new DisplayMode((int) newBounds.getWidth(), (int) newBounds.getHeight()));
            Display.setLocation(newBounds.x, newBounds.y);

            client.resize((int) newBounds.getWidth(), (int) newBounds.getHeight());
            // Related to the Forge fix for MC-68754
            if (!goFullScreen) {
            	Display.setResizable(false);
            }
            Display.setResizable(!goFullScreen);
            Display.setVSyncEnabled(client.gameSettings.enableVsync);
            client.updateDisplay();
            
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

    }

    @Override
    @SuppressWarnings("deprecated")
    public void performStartupChecks()
    {
        //If the mod is disabled by configuration, just put back the initial value.
        if(!ConfigurationHandler.instance().isFullscreenWindowedEnabled()) {
            return;
        }

        if(ConfigurationHandler.instance().isMaximumCompatibilityEnabled()){
            dsHandler.setInitialFullscreen(client.gameSettings.fullScreen,  ConfigurationHandler.instance().getFullscreenMonitor());
        // This is the correct way to set fullscreen at launch, but LWJGL limitations means we might crash the game if
        // another mod tries to do a similar Display changing operation. Doesn't help the API says "don't use this"
        }else{
            try {
                //FIXME: Living dangerously here... Is there a better way of doing this?
                SplashProgress.pause();
                toggleFullScreen(client.gameSettings.fullScreen, ConfigurationHandler.instance().getFullscreenMonitor());
                SplashProgress.resume();
            }catch(NoClassDefFoundError e) {
                LogHelper.warn("Error while doing startup checks, are you using an old version of Forge ? " + e);
                toggleFullScreen(client.gameSettings.fullScreen, ConfigurationHandler.instance().getFullscreenMonitor());
            }
        }
    }
}
