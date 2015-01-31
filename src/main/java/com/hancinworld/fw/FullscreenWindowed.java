package com.hancinworld.fw;

import com.hancinworld.fw.proxy.IProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "fw", name="Fullscreen Windowed",version="1.7.10-1.0.0")
public class FullscreenWindowed {

    @SidedProxy(clientSide = "com.hancinworld.fw.proxy.ClientProxy", serverSide = "com.hancinworld.fw.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.Instance("fw")
    public static FullscreenWindowed instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Items and blocks
        proxy.toggleFullScreen(true);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //GUI and recipes and stuff
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //After-mod initialization stuff
    }
}
