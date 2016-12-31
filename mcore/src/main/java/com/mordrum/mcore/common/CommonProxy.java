package com.mordrum.mcore.common;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy {
    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CreatureExplosionHealListener());
    }

    public void onLoad() {
        new MultiMineServer();
    }


    @SubscribeEvent
    public void onServerStart(WorldEvent.Load event) {
//        event.getWorld().setSeaLevel(128);
    }
}
