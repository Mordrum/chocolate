package com.mordrum.mmetallurgy.common;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OreGenEventHandler {
	public OreGenEventHandler() {
		MinecraftForge.ORE_GEN_BUS.register(this);
	}

	@SubscribeEvent(receiveCanceled = true)
	public void oreGenEvent(OreGenEvent.GenerateMinable event) {
		switch (event.getType()) {
			case COAL:
			case DIAMOND:
			case GOLD:
			case IRON:
				event.setResult(Event.Result.DENY);
				break;
		}
	}
}
