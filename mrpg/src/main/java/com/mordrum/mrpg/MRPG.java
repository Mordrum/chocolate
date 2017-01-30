package com.mordrum.mrpg;

import com.mordrum.mrpg.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MRPG.MOD_ID, name = MRPG.MOD_NAME, version = MRPG.VERSION, dependencies = "required-after:malisiscore")
public class MRPG {
	public static final String MOD_ID = "mrpg";
	static final String MOD_NAME = "mRPG";
	static final String VERSION = "$VERSION";

	@SidedProxy(clientSide = "com.mordrum.mrpg.client.ClientProxy", serverSide = "com.mordrum.mrpg.server.ServerProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public  void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
}