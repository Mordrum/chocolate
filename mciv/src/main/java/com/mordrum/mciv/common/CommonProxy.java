package com.mordrum.mciv.common;

import com.google.common.collect.Lists;
import com.mordrum.mciv.MCiv;
import com.mordrum.mciv.common.listeners.PlayerMoveListener;
import com.mordrum.mciv.common.networking.ClientAPIHelper;
import com.mordrum.mciv.common.networking.handlers.ClaimChunkMessageHandler;
import com.mordrum.mciv.common.networking.handlers.CreateCivMessageHandler;
import com.mordrum.mciv.common.networking.messages.ChunkSyncMessage;
import com.mordrum.mciv.common.networking.messages.ChunkClaimMessage;
import com.mordrum.mciv.common.networking.messages.CreateCivMessage;
import com.mordrum.mciv.common.networking.messages.SyncCivMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonProxy {
	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MCiv.MOD_ID);
	private static final int DISCRIMINATOR = 0;

	private static Map<Integer, Map<Integer, Long>> chunkCache;
	private static Map<Long, Civilization> civilizationMap;

	public void onPreInit(FMLPreInitializationEvent event) {
		NETWORK_WRAPPER.registerMessage(CreateCivMessageHandler.class, CreateCivMessage.Request.class, DISCRIMINATOR + 1, Side.SERVER);
		NETWORK_WRAPPER.registerMessage(ClaimChunkMessageHandler.class, ChunkClaimMessage.Request.class, DISCRIMINATOR + 2, Side.SERVER);

		NETWORK_WRAPPER.registerMessage(ChunkSyncMessage.Handler.class, ChunkSyncMessage.class, DISCRIMINATOR + 3, Side.CLIENT);
		NETWORK_WRAPPER.registerMessage(SyncCivMessage.Handler.class, SyncCivMessage.class, DISCRIMINATOR + 4, Side.CLIENT);

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new PlayerMoveListener());
		}

		chunkCache = new HashMap<>();
		civilizationMap = new HashMap<>();
	}

	public void onInit(FMLInitializationEvent event) {

	}

	public void onServerStarting(FMLServerStartingEvent event) {
	}

	public static void populateCaches() {
		ClientAPIHelper.findCivilizations(Lists.newArrayList(), (civilizations -> {
			civilizations.forEach((civilization -> {
				civilizationMap.put(civilization.getId(), civilization);
				syncCivilizationChunks(civilization.getId());
			}));
		}));
	}

	public static Map<Integer, Map<Integer, Long>> getChunkCache() {
		return chunkCache;
	}

	public static Map<Long, Civilization> getCivilizationMap() {
		return civilizationMap;
	}

	public static void syncCivilizationChunks(long civilizationId) {
		ClientAPIHelper.getChunksForCivilization(civilizationId, (chunks) -> {
			for (Object chunk : chunks) {
				JSONObject chunkJson = (JSONObject) chunk;
				int x = chunkJson.getInt("x");
				int z = chunkJson.getInt("z");
				if (!chunkCache.containsKey(x)) chunkCache.put(x, new HashMap<>());
				chunkCache.get(x).put(z, civilizationId);
			}
		});
	}

	public static void syncCivilization(long civilizationId, boolean syncChunks) {
		ClientAPIHelper.findCivilizations(Lists.asList("id", new String[]{Long.toString(civilizationId)}), (civilizations -> {
			for (Civilization civilization : civilizations) {
				CommonProxy.getCivilizationMap().put(civilization.getId(), civilization);
				if (syncChunks) syncCivilizationChunks(civilizationId);
			}
		}));
	}
}
