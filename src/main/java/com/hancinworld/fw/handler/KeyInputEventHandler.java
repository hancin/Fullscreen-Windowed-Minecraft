package com.hancinworld.fw.handler;

import com.hancinworld.fw.FullscreenWindowed;
import com.hancinworld.fw.proxy.ClientProxy;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

/**
 * Created by David on 2015-07-04.
 */
public class KeyInputEventHandler {

    private static boolean isCorrectKeyBinding()
    {
        return ClientProxy.fullscreenKeyBinding.isPressed();
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event)
    {

        if(isCorrectKeyBinding())
        {

            FullscreenWindowed.proxy.toggleFullScreen(!ClientProxy.currentState);
        }
    }
}
