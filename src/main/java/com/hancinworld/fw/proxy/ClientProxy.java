package com.hancinworld.fw.proxy;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * Created by David on 2015-01-31.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void toggleFullScreen(boolean state) {
        System.setProperty("org.lwjgl.opengl.Window.undecorated", state?"true":"false");
        try {
            Display.setResizable(!state);
            Display.setFullscreen(false);
            Display.setDisplayMode(new DisplayMode( Display.getDesktopDisplayMode().getWidth(),  Display.getDesktopDisplayMode().getHeight()));
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }
}
