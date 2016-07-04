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
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigurationHandler {

    private Configuration _configuration;
    private static ConfigurationHandler _instance;

    private Property _enableFullscreenWindowed = null;
    private Property _fullscreenMonitor = null;
    private boolean _commitImmediately = true;

    private boolean _isInitializing = true;

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
        if(event.getModID().equalsIgnoreCase(Reference.MOD_ID)) {
            load();
            if(!_isInitializing){
                FullscreenWindowed.proxy.registerKeyBindings();
            }
        }
    }

    public boolean isFullscreenWindowedEnabled()
    {
        if(_enableFullscreenWindowed == null)
            return true;

        return _enableFullscreenWindowed.getBoolean(true);
    }
    public void setFullscreenWindowedEnabled(boolean value)
    {
        _enableFullscreenWindowed.set(value);

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
        _enableFullscreenWindowed = _configuration.get(Configuration.CATEGORY_GENERAL, "enableFullscreenWindowed", true, I18n.format("comment.fullscreenwindowed.enableFullscreenWindowed"));
        _fullscreenMonitor = _configuration.get(Configuration.CATEGORY_GENERAL, "fullscreenMonitor", 0, I18n.format("comment.fullscreenwindowed.fullscreenmonitor"));

        if (_configuration.hasChanged()) {
            _configuration.save();
        }


    }


    @Override
    public String toString(){
        return _configuration.toString();
    }
}
