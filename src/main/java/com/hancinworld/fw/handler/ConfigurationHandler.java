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

import com.hancinworld.fw.FullscreenWindowed;
import com.hancinworld.fw.reference.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigurationHandler {

    private Configuration _configuration;
    private static ConfigurationHandler _instance;

    private Property _overrideF11Behavior = null;
    private Property _fullscreenWindowedStartup = null;
    private Property _fullscreenMonitor = null;
    private boolean _commitImmediately = true;

    private boolean _isInitializing = false;


    private ConfigurationHandler()
    {
    }

    public static ConfigurationHandler instance()
    {
        if(_instance == null)
            _instance = new ConfigurationHandler();

        return _instance;
    }

    public ConfigCategory getConfigurationCategory()
    {
        return _configuration.getCategory(Configuration.CATEGORY_GENERAL);
    }


    public void init(File suggestedConfigurationFile)
    {
        if(_configuration == null) {
            _configuration = new Configuration(suggestedConfigurationFile);
            _isInitializing = true;
            load();
            _isInitializing = false;
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.modID.equalsIgnoreCase(Reference.MOD_ID)) {
            load();
        }
    }

    public boolean getOverrideF11Behavior()
    {
        if(_overrideF11Behavior == null)
            return true;

        return _overrideF11Behavior.getBoolean(true);
    }
    public void setOverrideF11Behavior(boolean value)
    {
        _overrideF11Behavior.set(value);

        if(_commitImmediately && _configuration.hasChanged())
            _configuration.save();
    }

    public boolean getFullscreenWindowedStartup()
    {
        if(_fullscreenWindowedStartup == null)
            return false;

        return _fullscreenWindowedStartup.getBoolean(true);
    }
    public void setFullscreenWindowedStartup(boolean value)
    {
        _fullscreenWindowedStartup.set(value);

        if(_commitImmediately && _configuration.hasChanged())
            _configuration.save();
    }

    public int getFullscreenMonitor()
    {
        if(_fullscreenMonitor == null)
            return Reference.AUTOMATIC_MONITOR_SELECTION;

        return _fullscreenMonitor.getInt(Reference.AUTOMATIC_MONITOR_SELECTION);
    }

    public void setFullscreenMonitor(int value)
    {
        _fullscreenMonitor.set(value);

        if(_commitImmediately && _configuration.hasChanged())
            _configuration.save();
    }

    public boolean isCommitImmediately()
    {
        return _commitImmediately;
    }
    public void setCommitImmediately(boolean value)
    {
        _commitImmediately = value;
    }


    private void load()
    {
        _overrideF11Behavior = _configuration.get(Configuration.CATEGORY_GENERAL, "overrideF11Behavior", true, StatCollector.translateToLocal("comment.fullscreenwindowed.overridef11behavior"));
        _fullscreenWindowedStartup = _configuration.get(Configuration.CATEGORY_GENERAL, "fullscreenWindowedStartup", false, StatCollector.translateToLocal("comment.fullscreenwindowed.fullscreenwindowedstartup"));
        _fullscreenMonitor = _configuration.get(Configuration.CATEGORY_GENERAL, "fullscreenMonitor", 0, StatCollector.translateToLocal("comment.fullscreenwindowed.fullscreenmonitor"));

        if (_configuration.hasChanged()) {
            _configuration.save();
        }
    }


    @Override
    public String toString(){
        return _configuration.toString();
    }
}
