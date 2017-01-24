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
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
  * We need to register for this event because we want fullscreen to be global.
  */
class DrawScreenEventHandler {
  private var _lastState = false
  private var _initialFullscreen = true
  private var _cooldown = Reference.DRAW_SCREEN_EVENT_COOLDOWN - 5

  @SubscribeEvent
  def handleDrawScreenEvent(event: GuiScreenEvent.DrawScreenEvent) {
    val newState = FullscreenWindowed.proxy.isCorrectKeyPressed
    if (_initialFullscreen && _cooldown >= Reference.DRAW_SCREEN_EVENT_COOLDOWN) {
      _cooldown = 0
      _initialFullscreen = false
      FullscreenWindowed.proxy.handleInitialFullscreen
    }

    if (!_initialFullscreen && _cooldown >= Reference.DRAW_SCREEN_EVENT_COOLDOWN && (_lastState != newState) && newState) {
      _cooldown = 0
      FullscreenWindowed.proxy.toggleFullScreen
    }

    _lastState = newState
    if (_cooldown < Reference.DRAW_SCREEN_EVENT_COOLDOWN) {
      _cooldown += 1
    }
  }
}
