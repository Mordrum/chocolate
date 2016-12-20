package com.mordrum.mmetallurgy;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mordrum.mmetallurgy.common.CommonProxy;
import com.mordrum.mmetallurgy.common.OreGenEventHandler;
import com.mordrum.mmetallurgy.common.generation.OreGenerator;
import com.mordrum.mmetallurgy.creativetabs.BricksTab;
import com.mordrum.mmetallurgy.creativetabs.MetalsTab;
import com.mordrum.mmetallurgy.creativetabs.OresTab;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Mod(modid = MMetallurgy.MOD_ID, name = MMetallurgy.MOD_NAME, version = MMetallurgy.VERSION, dependencies = "required-after:malisiscore")
public class MMetallurgy {
	public static final String MOD_ID = "mmetallurgy";
	static final String MOD_NAME = "MMetallurgy";
	static final String VERSION = "1.1.1";
	public static final Gson gson = new Gson();

	// Creative tabs
	public static final MetalsTab metalsTab = new MetalsTab();
	//TODO add tools to this tab
	// public static final ToolsTab toolsTab = new ToolsTab();
	public static final OresTab oresTab = new OresTab();
	public static final BricksTab bricksTab = new BricksTab();
	public static Config config;

	@Mod.Instance(MMetallurgy.MOD_ID)
	public static MMetallurgy instance;

	@SidedProxy(clientSide = "com.mordrum." + MOD_ID + ".client.ClientProxy", serverSide = "com.mordrum." + MOD_ID + ".common.CommonProxy")
	private static CommonProxy proxy;

	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		// Initialize the config
		if (!event.getSuggestedConfigurationFile().exists()) {
			try {
				InputStream inputStream = getClass().getResourceAsStream("/assets/mmetallurgy/default_config.conf");
				Files.copy(inputStream, event.getSuggestedConfigurationFile()
						.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = ConfigFactory.parseFile(event.getSuggestedConfigurationFile());

		MinecraftForge.EVENT_BUS.register(this);
		new OreGenEventHandler();

		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void stopping(FMLServerStoppedEvent event) {
		try {
			OreGenerator.getInstance().saveCachedOres();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JsonArray getResourceAsJSON(String path) {
		path = "data/" + path;
		InputStream resourceAsStream = MMetallurgy.class.getClassLoader()
				.getResourceAsStream("assets/" + MMetallurgy.MOD_ID + "/" + path);
		return new JsonParser().parse(new JsonReader(new InputStreamReader(resourceAsStream))).getAsJsonArray();
	}
}
