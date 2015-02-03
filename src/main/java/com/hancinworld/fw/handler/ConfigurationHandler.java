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

        overrideF11Behavior = configuration.getBoolean("overrideF11Behavior", Configuration.CATEGORY_GENERAL, true, "Make this mod override the default fullscreen F11 behavior to use fullscreen windowed mode.");
        fullscreenWindowedStartup = configuration.getBoolean("fullscreenWindowedStartup", Configuration.CATEGORY_GENERAL, true, "Forces Minecraft to start in fullscreen windowed mode");

        if (configuration.hasChanged()) {
            configuration.save();
        }


    }
}
