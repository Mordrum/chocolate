package com.mordrum.mdeco;

import com.mordrum.mdeco.client.ClientProxy;
import com.mordrum.mdeco.common.CommonProxy;
import com.mordrum.mdeco.server.ServerProxy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MDeco.MOD_ID, name = MDeco.MOD_NAME, version = MDeco.VERSION, dependencies = "required-after:malisiscore;required-after:biomesoplenty;required-after:mmetallurgy")
public class MDeco {
	public static final String MOD_ID = "mdeco";
	static final String MOD_NAME = "mDeco";
	static final String VERSION = "$VERSION";

	private final CommonProxy proxy;

	public MDeco() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			proxy = new ClientProxy();
		} else {
			proxy = new ServerProxy();
		}
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
}
