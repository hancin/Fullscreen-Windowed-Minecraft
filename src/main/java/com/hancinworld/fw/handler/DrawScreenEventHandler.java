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
import com.hancinworld.fw.proxy.ClientProxy;
import com.hancinworld.fw.reference.Reference;
import com.hancinworld.fw.utility.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.lwjgl.input.Keyboard;

/**
 * We need to register for this event because we want fullscreen to be global.
 */
public class DrawScreenEventHandler {

    private final Minecraft client = Minecraft.getMinecraft();
    private boolean _lastState = false;
    private boolean _initialFullscreen = false;
    private boolean _initialGoFullScreen = false;
    private int _initialDesiredMonitor = 0;
    private int _cooldown = Reference.DRAW_SCREEN_EVENT_COOLDOWN;
    private boolean _isProcessing = false;
    private static boolean isCorrectKeyBinding()
    {
        return ClientProxy.fullscreenKeyBinding != null && Keyboard.isKeyDown(ClientProxy.fullscreenKeyBinding.getKeyCode());
    }

    public void setInitialFullscreen(boolean goFullScreen, int desiredMonitor)
    {
        // This is truly a hack to prevent a GL context problem during startup... we just delay fullscreen initialization by 0.25 seconds after game load.
        _cooldown = 35;
        _initialFullscreen = true;
        _initialGoFullScreen = goFullScreen;
        _initialDesiredMonitor = desiredMonitor;
    }

    @SubscribeEvent
    public void handleDrawScreenEvent(GuiScreenEvent.DrawScreenEvent event) {
        if(!ConfigurationHandler.instance().isFullscreenWindowedEnabled())
            return;


        boolean newState = isCorrectKeyBinding();
        if(_initialFullscreen && _cooldown >= Reference.DRAW_SCREEN_EVENT_COOLDOWN) {
            _cooldown = 0;
            _initialFullscreen = false;
            FullscreenWindowed.proxy.toggleFullScreen(_initialGoFullScreen, _initialDesiredMonitor);
        }
        else if(_cooldown >= Reference.DRAW_SCREEN_EVENT_COOLDOWN && (_lastState != newState) && newState)
        {
            _cooldown = 0;
            _lastState = newState;
            FullscreenWindowed.proxy.toggleFullScreen(!ClientProxy.fullscreen, ConfigurationHandler.instance().getFullscreenMonitor());
        }

        _lastState = newState;

        if(_cooldown < Reference.DRAW_SCREEN_EVENT_COOLDOWN)
            _cooldown++;

        if(client.getVersion().startsWith("forge-11")){
            //Do it the old way in 1.8 since event.getGui does not exist.
            if(client.currentScreen instanceof GuiVideoSettings && client.fullscreen != ClientProxy.fullscreen) {
                FullscreenWindowed.proxy.toggleFullScreen(client.fullscreen);
            }
        }else{
            if (event.getGui() instanceof GuiVideoSettings && client.fullscreen != ClientProxy.fullscreen) {
                FullscreenWindowed.proxy.toggleFullScreen(client.fullscreen);
            }
        }
    }
}
