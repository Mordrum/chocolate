package com.mordrum.mciv;

import com.mordrum.mciv.client.ClientProxy;
import com.mordrum.mciv.common.CommonProxy;
import com.mordrum.mciv.server.ServerProxy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = MCiv.MOD_ID, name = MCiv.MOD_NAME, version = MCiv.MOD_VERSION, dependencies = "required-after:malisiscore;required-after:mcore")
public class MCiv {
	public static final String MOD_ID = "mciv";
	public static final String MOD_NAME = "mCiv";
	public static final String MOD_VERSION = "$VERSION";

	@Mod.Instance(MOD_ID)
	private static MCiv instance;

	@SidedProxy(clientSide = "com.mordrum.mciv.client.ClientProxy", serverSide = "com.mordrum.mciv.server.ServerProxy")
	private static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.onPreInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.onInit(event);
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		proxy.onServerStarting(event);
	}
}
