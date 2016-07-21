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
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigurationHandler {

    public static final String CATEGORY_ADVANCED = "advanced";
    private Configuration _configuration;
    private static ConfigurationHandler _instance;

    private Property _enableFullscreenWindowed = null;
    private Property _fullscreenMonitor = null;
    private Property _enableAdvancedFeatures = null;
    private Property _customFullscreenDimensions = null;
    private Property _customFullscreenDimensionsX = null;
    private Property _customFullscreenDimensionsY = null;
    private Property _customFullscreenDimensionsW = null;
    private Property _customFullscreenDimensionsH = null;
    private Property _onlyRemoveDecorations = null;
    private Property _enableMaximumCompatibility = null;

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
        if(event.modID.equalsIgnoreCase(Reference.MOD_ID)) {
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


    public boolean areAdvancedFeaturesEnabled()
    {
        if(_enableAdvancedFeatures == null)
            return Reference.ADVANCED_FEATURES_ENABLED;

        return _enableAdvancedFeatures.getBoolean(Reference.ADVANCED_FEATURES_ENABLED);
    }

    public void setAdvancedFeaturesEnabled(boolean value)
    {
        _enableAdvancedFeatures.set(value);

        if(_commitImmediately && _configuration.hasChanged())
            _configuration.save();
    }


    public boolean isOnlyRemoveDecorations()
    {
        if(_onlyRemoveDecorations == null)
            return Reference.ONLY_REMOVE_DECORATIONS;

        return _onlyRemoveDecorations.getBoolean(Reference.ONLY_REMOVE_DECORATIONS);
    }

    public void setOnlyRemoveDecorations(boolean value)
    {
        _onlyRemoveDecorations.set(value);

        if(_commitImmediately && _configuration.hasChanged())
            _configuration.save();
    }

    public boolean isCustomFullscreenDimensions()
    {
        if(_customFullscreenDimensions == null)
            return Reference.CUSTOM_FULLSCREEN_DIMENSIONS;

        return _customFullscreenDimensions.getBoolean(Reference.CUSTOM_FULLSCREEN_DIMENSIONS);
    }

    public void setCustomFullscreenDimensions(boolean value)
    {
        _customFullscreenDimensions.set(value);

        if(_commitImmediately && _configuration.hasChanged())
            _configuration.save();
    }


    public boolean isMaximumCompatibilityEnabled()
    {
        if(_enableMaximumCompatibility == null)
            return Reference.ENABLE_MAXIMUM_COMPATIBILITY;

        return _enableMaximumCompatibility.getBoolean(Reference.ENABLE_MAXIMUM_COMPATIBILITY);
    }

    public void setEnableMaximumCompatibility(boolean value)
    {
        _enableMaximumCompatibility.set(value);

        if(_commitImmediately && _configuration.hasChanged())
            _configuration.save();
    }


    public int getCustomFullscreenDimensionsX()
    {
        if(_customFullscreenDimensionsX == null)
            return Reference.CUSTOM_FULLSCREEN_X;

        return _customFullscreenDimensionsX.getInt(Reference.CUSTOM_FULLSCREEN_X);
    }

    public void setCustomFullscreenDimensionsX(int value)
    {
        _customFullscreenDimensionsX.set(value);

        if(_commitImmediately && _configuration.hasChanged())
            _configuration.save();
    }

    public int getCustomFullscreenDimensionsY()
    {
        if(_customFullscreenDimensionsY == null)
            return Reference.CUSTOM_FULLSCREEN_Y;

        return _customFullscreenDimensionsY.getInt(Reference.CUSTOM_FULLSCREEN_Y);
    }

    public void setCustomFullscreenDimensionsY(int value)
    {
        _customFullscreenDimensionsY.set(value);

        if(_commitImmediately && _configuration.hasChanged())
            _configuration.save();
    }
    public int getCustomFullscreenDimensionsW()
    {
        if(_customFullscreenDimensionsW == null)
            return Reference.CUSTOM_FULLSCREEN_W;

        return _customFullscreenDimensionsW.getInt(Reference.CUSTOM_FULLSCREEN_W);
    }

    public void setCustomFullscreenDimensionsW(int value)
    {
        _customFullscreenDimensionsW.set(value);

        if(_commitImmediately && _configuration.hasChanged())
            _configuration.save();
    }
    public int getCustomFullscreenDimensionsH()
    {
        if(_customFullscreenDimensionsH == null)
            return Reference.CUSTOM_FULLSCREEN_H;

        return _customFullscreenDimensionsH.getInt(Reference.CUSTOM_FULLSCREEN_H);
    }

    public void setCustomFullscreenDimensionsH(int value)
    {
        _customFullscreenDimensionsH.set(value);

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
        _fullscreenMonitor = _configuration.get(Configuration.CATEGORY_GENERAL, "fullscreenMonitor", Reference.AUTOMATIC_MONITOR_SELECTION, I18n.format("comment.fullscreenwindowed.fullscreenmonitor"));
        _enableAdvancedFeatures = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "enableAdvancedFeatures", Reference.ADVANCED_FEATURES_ENABLED, I18n.format("comment.fullscreenwindowed.enableAdvancedFeatures"));
        _enableMaximumCompatibility = _configuration.get(Configuration.CATEGORY_GENERAL, "enableMaximumCompatibility", Reference.ENABLE_MAXIMUM_COMPATIBILITY, I18n.format("comment.fullscreenwindowed.enableMaximumCompatibility"));

        _customFullscreenDimensions = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "customFullscreenDimensions", false, I18n.format("comment.fullscreenwindowed.customFullscreenDimensions"));
        _customFullscreenDimensionsX = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "customFullscreenDimensionsX", 0, I18n.format("comment.fullscreenwindowed.customFullscreenDimensionsX"));
        _customFullscreenDimensionsY = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "customFullscreenDimensionsY", 0, I18n.format("comment.fullscreenwindowed.customFullscreenDimensionsY"));
        _customFullscreenDimensionsW = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "customFullscreenDimensionsW", 0, I18n.format("comment.fullscreenwindowed.customFullscreenDimensionsW"));
        _customFullscreenDimensionsH = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "customFullscreenDimensionsH", 0, I18n.format("comment.fullscreenwindowed.customFullscreenDimensionsH"));

        //TODO: due to how LWJGL draws windows, it's not a good idea to have this... disabled until I can fix the bugs with X,Y being off due to decoration shadows.
        //_onlyRemoveDecorations = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "onlyRemoveDecorations", Reference.ONLY_REMOVE_DECORATIONS, I18n.format("comment.fullscreenwindowed.onlyRemoveDecorations"));

        if (_configuration.hasChanged()) {
            _configuration.save();
        }


    }


    @Override
    public String toString(){
        return _configuration.toString();
    }
}
