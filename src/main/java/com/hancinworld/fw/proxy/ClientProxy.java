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

import java.lang.reflect.Method;

public class ClientProxy extends CommonProxy {

    private int _lastRegisteredWidth = 0;
    private int _lastRegisteredHeight = 0;
    public static boolean currentState;
    public static KeyBinding fullscreenKeyBinding;

    public static KeyBinding ignoreKeyBinding = new KeyBinding("key.fullscreenwindowed.unused", Keyboard.KEY_NONE, "key.categories.misc");

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

    @Override
    public void toggleFullScreen(boolean state) {

        if(state)
        {
            _lastRegisteredWidth = Display.getWidth();
            _lastRegisteredHeight = Display.getHeight();
        }

        System.setProperty("org.lwjgl.opengl.Window.undecorated", state?"true":"false");
        try {
            Display.setResizable(!state);
            Display.setFullscreen(false);
            int w,h;
            if(state){
                w = Display.getDesktopDisplayMode().getWidth();
                h = Display.getDesktopDisplayMode().getHeight();
                Display.setDisplayMode(new DisplayMode( w ,  h));

            }else{
                w =  Math.max(_lastRegisteredWidth, 800);
                h = Math.max(_lastRegisteredHeight, 473);
                Display.setDisplayMode(new DisplayMode( w ,  h));
            }

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

        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        currentState = state;
    }
}
