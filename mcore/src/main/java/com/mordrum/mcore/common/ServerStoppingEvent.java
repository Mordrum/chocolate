package com.mordrum.mcore.common;

import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ServerStoppingEvent extends Event {
	private final FMLServerStoppingEvent event;

	public ServerStoppingEvent(FMLServerStoppingEvent event) {
		this.event = event;
	}
}
