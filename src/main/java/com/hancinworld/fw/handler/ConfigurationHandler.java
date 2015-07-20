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
package com.hancinworld.fw.handler;

import com.hancinworld.fw.reference.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.io.File;

/**
 * Created by David on 2015-02-02.
 */
public class ConfigurationHandler {

    public static Configuration configuration;

    public static boolean overrideF11Behavior = true;
    public static boolean fullscreenWindowedStartup = true;
    public static int fullScreenMonitor = -1;

    public static void init(File configFile)
    {
        if(configuration == null) {
            configuration = new Configuration(configFile);
            loadConfiguration();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.modID.equalsIgnoreCase(Reference.MOD_ID)) {
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {
        boolean overrideF11Behavior = true;
        boolean fullscreenWindowedStartup = true;
        int fullscreenMonitor = -1;

        overrideF11Behavior = configuration.getBoolean("overrideF11Behavior", Configuration.CATEGORY_GENERAL, true, "Make this mod override the default fullscreen F11 behavior to use fullscreen windowed mode.");
        fullscreenWindowedStartup = configuration.getBoolean("fullscreenWindowedStartup", Configuration.CATEGORY_GENERAL, true, "Forces Minecraft to start in fullscreen windowed mode");
        fullscreenMonitor = configuration.getInt("fullscreenMonitor", Configuration.CATEGORY_GENERAL, -1,-1, 5, "Indicates which monitor to use for fullscreen windowed mode. Use -1 for the default behavior of maximizing on the active monitor.");

        if (configuration.hasChanged()) {
            configuration.save();
        }


        ConfigurationHandler.overrideF11Behavior = overrideF11Behavior;
        ConfigurationHandler.fullScreenMonitor = fullscreenMonitor;
        ConfigurationHandler.fullscreenWindowedStartup = fullscreenWindowedStartup;
    }
}
