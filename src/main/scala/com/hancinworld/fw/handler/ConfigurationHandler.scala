//Copyright (c) 2015-2017, David Larochelle-Pratte
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
package com.hancinworld.fw.handler

import com.hancinworld.fw.FullscreenWindowed
import com.hancinworld.fw.reference.Reference
import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.common.config.ConfigCategory
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.common.config.Property
import java.io.File

object ConfigurationHandler {
  val CATEGORY_ADVANCED = "advanced"

  private var _configuration:Configuration = null
  private var _enableFullscreenWindowed: Property = null
  private var _fullscreenMonitor: Property = null
  private var _enableAdvancedFeatures: Property = null
  private var _customFullscreenDimensions: Property = null
  private var _customFullscreenDimensionsX: Property = null
  private var _customFullscreenDimensionsY: Property = null
  private var _customFullscreenDimensionsW: Property = null
  private var _customFullscreenDimensionsH: Property = null
  private val _onlyRemoveDecorations: Property = null
  private var _enableMaximumCompatibility: Property = null
  var commitImmediately = true
  private var _isInitializing = true

  def configurationCategory: ConfigCategory = _configuration.getCategory(Configuration.CATEGORY_GENERAL)

  def init(suggestedConfigurationFile: File) {
    if (_configuration == null) {
      _configuration = new Configuration(suggestedConfigurationFile)
      _isInitializing = true
      load()
      _isInitializing = false
    }
  }

  @SubscribeEvent
  def onConfigurationChangedEvent(event: ConfigChangedEvent.OnConfigChangedEvent) {
    try {
      if (event.getModID.equalsIgnoreCase(FullscreenWindowed.ID)) {
        load()
        if (!_isInitializing) FullscreenWindowed.proxy.registerKeyBindings
      }
    }catch {
      case e: NoSuchMethodError => {
        // In earlier versions of Forge (1.8), event.getModID() does not exist.
        // While this means any config change in any 1.8 mod configuration will force a reload of our configuration,
        // this is probably better than crashing...
        load()
        if (!_isInitializing) FullscreenWindowed.proxy.registerKeyBindings
      }
    }
  }



  def fullscreenWindowedEnabled: Boolean = {
    if (_enableFullscreenWindowed == null) return true
    _enableFullscreenWindowed.getBoolean(true)
  }

  def fullscreenWindowedEnabled_= (value:Int):Unit = {
    _enableFullscreenWindowed.set(value)
    if (commitImmediately && _configuration.hasChanged) _configuration.save()
  }

  def fullscreenMonitor: Int = {
    if (_fullscreenMonitor == null) return Reference.AUTOMATIC_MONITOR_SELECTION
    _fullscreenMonitor.getInt(Reference.AUTOMATIC_MONITOR_SELECTION)
  }

  def fullscreenMonitor_= (value: Int):Unit = {
    _fullscreenMonitor.set(value)
    if (commitImmediately && _configuration.hasChanged) _configuration.save()
  }

  def advancedFeaturesEnabled: Boolean = {
    if (_enableAdvancedFeatures == null) return Reference.ADVANCED_FEATURES_ENABLED
    _enableAdvancedFeatures.getBoolean(Reference.ADVANCED_FEATURES_ENABLED)
  }

  def advancedFeaturesEnabled_= (value: Boolean):Unit = {
    _enableAdvancedFeatures.set(value)
    if (commitImmediately && _configuration.hasChanged) _configuration.save()
  }

  def onlyRemoveDecorations: Boolean = {
    if (_onlyRemoveDecorations == null) return Reference.ONLY_REMOVE_DECORATIONS
    _onlyRemoveDecorations.getBoolean(Reference.ONLY_REMOVE_DECORATIONS)
  }

  def onlyRemoveDecorations_= (value: Boolean):Unit = {
    _onlyRemoveDecorations.set(value)
    if (commitImmediately && _configuration.hasChanged) _configuration.save()
  }

  def customFullscreenDimensions: Boolean = {
    if (_customFullscreenDimensions == null) return Reference.CUSTOM_FULLSCREEN_DIMENSIONS
    _customFullscreenDimensions.getBoolean(Reference.CUSTOM_FULLSCREEN_DIMENSIONS)
  }

  def customFullscreenDimensions_= (value: Boolean):Unit = {
    _customFullscreenDimensions.set(value)
    if (commitImmediately && _configuration.hasChanged) _configuration.save()
  }

  def maximumCompatibilityEnabled: Boolean = {
    if (_enableMaximumCompatibility == null) return Reference.ENABLE_MAXIMUM_COMPATIBILITY
    _enableMaximumCompatibility.getBoolean(Reference.ENABLE_MAXIMUM_COMPATIBILITY)
  }

  def maximumCompatibilityEnabled_= (value: Boolean):Unit = {
    _enableMaximumCompatibility.set(value)
    if (commitImmediately && _configuration.hasChanged) _configuration.save()
  }

  def customFullscreenDimensionsX: Int = {
    if (_customFullscreenDimensionsX == null) return Reference.CUSTOM_FULLSCREEN_X
    _customFullscreenDimensionsX.getInt(Reference.CUSTOM_FULLSCREEN_X)
  }

  def customFullscreenDimensionsX_= (value: Int):Unit = {
    _customFullscreenDimensionsX.set(value)
    if (commitImmediately && _configuration.hasChanged) _configuration.save()
  }

  def customFullscreenDimensionsY: Int = {
    if (_customFullscreenDimensionsY == null) return Reference.CUSTOM_FULLSCREEN_Y
    _customFullscreenDimensionsY.getInt(Reference.CUSTOM_FULLSCREEN_Y)
  }

  def customFullscreenDimensionsY_= (value: Int):Unit = {
    _customFullscreenDimensionsY.set(value)
    if (commitImmediately && _configuration.hasChanged) _configuration.save()
  }

  def customFullscreenDimensionsW: Int = {
    if (_customFullscreenDimensionsW == null) return Reference.CUSTOM_FULLSCREEN_W
    _customFullscreenDimensionsW.getInt(Reference.CUSTOM_FULLSCREEN_W)
  }

  def customFullscreenDimensionsW_= (value: Int):Unit = {
    _customFullscreenDimensionsW.set(value)
    if (commitImmediately && _configuration.hasChanged) _configuration.save()
  }

  def customFullscreenDimensionsH: Int = {
    if (_customFullscreenDimensionsH == null) return Reference.CUSTOM_FULLSCREEN_H
    _customFullscreenDimensionsH.getInt(Reference.CUSTOM_FULLSCREEN_H)
  }

  def customFullscreenDimensionsH_= (value: Int):Unit = {
    _customFullscreenDimensionsH.set(value)
    if (commitImmediately && _configuration.hasChanged) _configuration.save()
  }

  private def load() {
    _enableFullscreenWindowed = _configuration.get(Configuration.CATEGORY_GENERAL, "enableFullscreenWindowed", true, I18n.format("comment.fullscreenwindowed.enableFullscreenWindowed"))
    _fullscreenMonitor = _configuration.get(Configuration.CATEGORY_GENERAL, "fullscreenMonitor", Reference.AUTOMATIC_MONITOR_SELECTION, I18n.format("comment.fullscreenwindowed.fullscreenmonitor"))
    _enableAdvancedFeatures = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "enableAdvancedFeatures", Reference.ADVANCED_FEATURES_ENABLED, I18n.format("comment.fullscreenwindowed.enableAdvancedFeatures"))
    _enableMaximumCompatibility = _configuration.get(Configuration.CATEGORY_GENERAL, "enableMaximumCompatibility", Reference.ENABLE_MAXIMUM_COMPATIBILITY, I18n.format("comment.fullscreenwindowed.enableMaximumCompatibility"))
    _customFullscreenDimensions = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "customFullscreenDimensions", false, I18n.format("comment.fullscreenwindowed.customFullscreenDimensions"))
    _customFullscreenDimensionsX = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "customFullscreenDimensionsX", 0, I18n.format("comment.fullscreenwindowed.customFullscreenDimensionsX"))
    _customFullscreenDimensionsY = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "customFullscreenDimensionsY", 0, I18n.format("comment.fullscreenwindowed.customFullscreenDimensionsY"))
    _customFullscreenDimensionsW = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "customFullscreenDimensionsW", 0, I18n.format("comment.fullscreenwindowed.customFullscreenDimensionsW"))
    _customFullscreenDimensionsH = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "customFullscreenDimensionsH", 0, I18n.format("comment.fullscreenwindowed.customFullscreenDimensionsH"))
    //TODO: due to how LWJGL draws windows, it's not a good idea to have this... disabled until I can fix the bugs with X,Y being off due to decoration shadows.
    //_onlyRemoveDecorations = _configuration.get(ConfigurationHandler.CATEGORY_ADVANCED, "onlyRemoveDecorations", Reference.ONLY_REMOVE_DECORATIONS, I18n.format("comment.fullscreenwindowed.onlyRemoveDecorations"));
    if (_configuration.hasChanged) _configuration.save()
  }

  override def toString: String = _configuration.toString
}
