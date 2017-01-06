package com.mordrum.mcore;

import com.mordrum.mcore.common.CommonProxy;
import com.mordrum.mcore.common.ServerStoppingEvent;
import com.mordrum.mcore.common.network.NetworkHelper;
import com.mordrum.mcore.common.network.PartialBlockPacket;
import com.mordrum.mcore.common.network.PartialBlockRemovalPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

/**
 * FML superclass causing all of the things to happen. Registers everything, causes the Mod parts
 * to load, keeps the common config file.
 */
@Mod(modid = MCore.MODID, name = MCore.NAME, version = MCore.VERSION, dependencies = "required-after:malisiscore")
public class MCore {
	public static final String NAME = "mCore";
	public static final String MODID = "mcore";
	public static final String VERSION = "$VERSION";
	public static final String API_URL;

	static {
		String environment = System.getProperty("environment");
		if ((environment != null && environment.equalsIgnoreCase("development"))) {
			API_URL = "http://localhost:8080";
		} else {
			API_URL = "http://api.mordrum.com";
		}
	}

	@Instance(MCore.MODID)
	private static MCore instance;

	private NetworkHelper networkHelper;

	@SidedProxy(clientSide = "com.mordrum.mcore.client.ClientProxy", serverSide = "com.mordrum.mcore.common.CommonProxy")
	public static CommonProxy proxy;

	static {
		FluidRegistry.enableUniversalBucket();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		proxy.onPreInit();
		networkHelper = new NetworkHelper("AS_MM", PartialBlockPacket.class, PartialBlockRemovalPacket.class);
	}

	@EventHandler
	public void load(FMLInitializationEvent evt) {
		proxy.onLoad();
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		MinecraftForge.EVENT_BUS.post(new ServerStoppingEvent(event));
	}

	public static MCore instance() {
		return instance;
	}

	public NetworkHelper getNetworkHelper() {
		return networkHelper;
	}
}
