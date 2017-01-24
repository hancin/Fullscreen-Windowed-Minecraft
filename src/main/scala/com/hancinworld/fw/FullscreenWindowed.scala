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
package com.hancinworld.fw

import com.hancinworld.fw.proxy.IProxy
import com.hancinworld.fw.reference.Reference
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import net.minecraftforge.fml.common.event._
import org.apache.logging.log4j.{LogManager, Logger}

@Mod(modid = FullscreenWindowed.ID, name = FullscreenWindowed.Name,
  clientSideOnly = true, version = FullscreenWindowed.Version,
  guiFactory = Reference.GUI_FACTORY_CLASS, acceptedMinecraftVersions = Reference.MC_VERSIONS,
  modLanguage = "scala")
object FullscreenWindowed {
  final val ID = "fw"
  final val Name = "FullscreenWindowed"
  final val Version = "@VERSION@"

  def log = logger.getOrElse(LogManager.getLogger(Name))

  var logger: Option[Logger] = None

  @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS)
  var proxy: IProxy = null

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    logger = Option(event.getModLog)
    proxy.subscribeEvents(event.getSuggestedConfigurationFile)
  }

  @EventHandler
  def init(event: FMLInitializationEvent) = FullscreenWindowed.proxy.registerKeyBindings

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) = FullscreenWindowed.proxy.performStartupChecks

}
