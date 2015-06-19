package com.hancinworld.fw.proxy;

import com.hancinworld.fw.utility.LogHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.lang.reflect.Method;

/**
 * Created by David on 2015-01-31.
 */
public class ClientProxy extends CommonProxy {

    private int _lastRegisteredWidth = 0;
    private int _lastRegisteredHeight = 0;

    @Override
    public void toggleFullScreen(boolean state) {

        if(state)
        {
            _lastRegisteredWidth = Display.getDisplayMode().getWidth();
            _lastRegisteredHeight = Display.getDisplayMode().getHeight();
        }

        System.setProperty("org.lwjgl.opengl.Window.undecorated", state?"true":"false");
        try
        {
            Display.setResizable(!state);
            Display.setFullscreen(false);

            if (state)
            {
                int w = Display.getDesktopDisplayMode().getWidth();
                int h = Display.getDesktopDisplayMode().getHeight();
                Display.setDisplayMode(new DisplayMode(w, h));
                try
                {
                    Minecraft inst = Minecraft.getMinecraft();
                    Method resizeMethod = ReflectionHelper.findMethod(Minecraft.class, inst, new String[]{"func_71370_a", "resize"});
                    if (resizeMethod != null)
                    {
                        Display.update();
                        resizeMethod.invoke(inst, w, h);
                    }

                } catch (Exception e)
                {
                    LogHelper.warn("Resize method not found or problem found while calling it. Are you using the correct version of the mod for this version of Minecraft?" + e.toString());
                }
            } else
            {
                Display.setDisplayMode(new DisplayMode(Math.max(_lastRegisteredWidth, 800), Math.max(_lastRegisteredHeight, 473)));
            }

        } catch (LWJGLException e)
        {
            e.printStackTrace();
        } catch (IllegalStateException ignored) {}
    }
}
