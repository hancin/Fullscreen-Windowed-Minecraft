package com.hancinworld.fw;

import com.hancinworld.fw.handler.ConfigurationHandler;
import com.hancinworld.fw.handler.KeyInputEventHandler;
import com.hancinworld.fw.proxy.IProxy;
import com.hancinworld.fw.reference.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

@Mod(modid = Reference.MOD_ID, name=Reference.MOD_NAME,version=Reference.VERSION,guiFactory = Reference.GUI_FACTORY_CLASS)
public class FullscreenWindowed {

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.Instance(Reference.MOD_ID)
    public static FullscreenWindowed instance;

    private boolean runOnce = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Items and blocks
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
        FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerKeyBindings();

    }

    /*@Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        if(runOnce){
            runOnce = false;
            proxy.toggleFullScreen(ConfigurationHandler.fullscreenWindowedStartup);
        }
    }*/
}
