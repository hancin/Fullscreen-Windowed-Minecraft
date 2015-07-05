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

/**
 * Created by David on 2015-01-31.
 */
public class ClientProxy extends CommonProxy {

    private int _lastRegisteredWidth = 0;
    private int _lastRegisteredHeight = 0;
    public static boolean currentState;
    public static KeyBinding fullscreenKeyBinding;

    public static KeyBinding ignoreKeyBinding = new KeyBinding("key.fullscreenwindowed.unused", Keyboard.KEY_NONE, "key.categories.misc");

    @Override
    public void registerKeyBindings()
    {

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
