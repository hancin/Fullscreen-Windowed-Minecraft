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
package com.hancinworld.fw;

import com.hancinworld.fw.handler.ConfigurationHandler;
import com.hancinworld.fw.handler.KeyInputEventHandler;
import com.hancinworld.fw.proxy.IProxy;
import com.hancinworld.fw.reference.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;

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
