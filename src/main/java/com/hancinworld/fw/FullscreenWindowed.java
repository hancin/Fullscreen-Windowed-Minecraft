package com.hancinworld.fw;

import com.hancinworld.fw.handler.ConfigurationHandler;
import com.hancinworld.fw.proxy.IProxy;
import com.hancinworld.fw.reference.Reference;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name=Reference.MOD_NAME,version=Reference.VERSION,guiFactory = Reference.GUI_FACTORY_CLASS)
public class FullscreenWindowed {

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.Instance(Reference.MOD_ID)
    public static FullscreenWindowed instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Items and blocks
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
        proxy.toggleFullScreen(true);
    }

}
