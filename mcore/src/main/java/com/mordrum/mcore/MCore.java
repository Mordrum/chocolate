package com.mordrum.mcore;

import com.mordrum.mcore.common.CommonProxy;
import com.mordrum.mcore.common.network.NetworkHelper;
import com.mordrum.mcore.common.network.PartialBlockPacket;
import com.mordrum.mcore.common.network.PartialBlockRemovalPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * FML superclass causing all of the things to happen. Registers everything, causes the Mod parts
 * to load, keeps the common config file.
 */
@Mod(modid = MCore.MODID, name = MCore.NAME, version = MCore.VERSION)
public class MCore {
	public static final String MODID = "mCore";
	public static final String NAME = "mCore";
	public static final String VERSION = "1.1";

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

	public static MCore instance() {
		return instance;
	}

	public NetworkHelper getNetworkHelper() {
		return networkHelper;
	}
}
