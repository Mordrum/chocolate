package com.mordrum.mciv.server;

import com.mordrum.mciv.common.CommonProxy;
import com.mordrum.mciv.common.listeners.PlayerMoveListener;
import com.mordrum.mciv.server.listeners.PlayerInteractListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {
	@Override
	public void onPreInit(FMLPreInitializationEvent event) {
		super.onPreInit(event);

		MinecraftForge.EVENT_BUS.register(new PlayerInteractListener());
	}

	@Override
	public void onInit(FMLInitializationEvent event) {
		super.onInit(event);
		populateCaches();
	}

}
