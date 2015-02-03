package com.hancinworld.fw.client.gui;

import com.hancinworld.fw.handler.ConfigurationHandler;
import com.hancinworld.fw.reference.Reference;
import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;


/**
 * Created by David on 2015-02-02.
 */
public class ModGuiConfig extends GuiConfig {
    public ModGuiConfig (GuiScreen parentScreen)
    {
        super(parentScreen,
                new ConfigElement(ConfigurationHandler.configuration.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                Reference.MOD_ID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(ConfigurationHandler.configuration.toString()));
    }
}
