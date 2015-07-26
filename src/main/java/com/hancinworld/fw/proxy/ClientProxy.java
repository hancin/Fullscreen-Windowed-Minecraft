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
import com.hancinworld.fw.utility.LogHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;
import java.lang.reflect.Method;

public class ClientProxy extends CommonProxy {

    private Rectangle _savedWindowedBounds;
    public static boolean currentState;
    public static KeyBinding fullscreenKeyBinding;

    /** This keybind replaces the default MC fullscreen keybind in their logic handler. Without it, the game crashes.
     *  If this is set to any valid key, problems may occur. */
    public static KeyBinding ignoreKeyBinding = new KeyBinding("key.fullscreenwindowed.unused", Keyboard.KEY_NONE, "key.categories.misc");


    public ClientProxy()
    {
        Minecraft mc = Minecraft.getMinecraft();
        mc.gameSettings.fullScreen = false;
    }

    @Override
    public void registerKeyBindings()
    {

        /* FIXME: Overrides the minecraft hotkey for fullscreen, as there are no hooks */
        if(ConfigurationHandler.overrideF11Behavior)
        {
            Minecraft mc = Minecraft.getMinecraft();
            fullscreenKeyBinding = mc.gameSettings.field_152395_am;
            mc.gameSettings.field_152395_am = ignoreKeyBinding;
        }
    }

    private Rectangle findCurrentScreenDimensionsAndPosition(int x, int y)
    {
         //clamp value to the positive realm
         if(x < 0)
             x = 0;
         if(y < 0)
             y = 0;

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = env.getScreenDevices();

        //TODO: Actually test with multiple display adapters to see if issue occurs
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

    /** Calls the Minecraft resize() method so it updates its framebuffer. */
    private void callMinecraftResizeMethod(int w, int h)
    {
        try{
            Class[] args = new Class[2];
            args[0] = int.class;
            args[1] = int.class;
            Minecraft inst = Minecraft.getMinecraft();
            Method resizeMethod = ReflectionHelper.findMethod(Minecraft.class, inst, new String[]{"func_71370_a", "resize"}, args);
            if(resizeMethod != null)
            {
                Display.update();
                resizeMethod.invoke(inst, w, h);
            }

        }catch (Exception e){
            LogHelper.warn("Resize method not found or problem found while calling it. Are you using the correct version of the mod for this version of Minecraft?" + e.toString());
        }
    }

    private boolean isDesktopDisplayMode(Rectangle bounds)
    {
        if(bounds.getX() == 0 && bounds.getY() == 0)
        {
            DisplayMode displayMode = Display.getDesktopDisplayMode();

            return displayMode.getWidth() == bounds.getWidth() && displayMode.getHeight() == bounds.getHeight();
        }

        return false;
    }
    @Override
    public void toggleFullScreen(boolean goFullScreen) {

        if(Display.isFullscreen()) {
            currentState = true;
            LogHelper.warn("Display is actual fullscreen! Is Minecraft starting with the option set?");
        }

        if(currentState == goFullScreen)
            return;

        //Changing this property and causing a Display update will cause LWJGL to add/remove decorations (borderless).
        System.setProperty("org.lwjgl.opengl.Window.undecorated", goFullScreen?"true":"false");

        //Save our current display parameters
        Rectangle currentCoords = new Rectangle(Display.getX(), Display.getY(), Display.getWidth(), Display.getHeight());
        if(goFullScreen)
            _savedWindowedBounds = currentCoords;


        //find which monitor we should be using based on the center of the MC window
        Point centerCoordinates = new Point((int)(currentCoords.getMinX() + currentCoords.getWidth() / 2), (int)(currentCoords.getMinY() + currentCoords.getHeight() / 2));
        Rectangle screenBounds = findCurrentScreenDimensionsAndPosition((int)centerCoordinates.getX(), (int)centerCoordinates.getY());

        //This is the new bounds we have to apply.
        Rectangle newBounds = goFullScreen ? screenBounds : _savedWindowedBounds;
        if(newBounds == null)
            newBounds = screenBounds;



        try {
            Display.setDisplayMode(new DisplayMode((int) newBounds.getWidth(), (int) newBounds.getHeight()));
            Display.setResizable(!goFullScreen);
            Display.setFullscreen(false);
            //Vsync has no effect on borderless windows.
            Display.setVSyncEnabled(false);

            Display.update();

            Display.setLocation((int) newBounds.getMinX(), (int)newBounds.getMinY());


            callMinecraftResizeMethod((int)newBounds.getWidth(), (int)newBounds.getHeight());

        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        currentState = goFullScreen;
    }
}
