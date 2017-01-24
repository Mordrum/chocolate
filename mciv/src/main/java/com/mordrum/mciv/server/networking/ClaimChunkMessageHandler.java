package com.mordrum.mciv.server.networking;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mordrum.mciv.common.CommonProxy;
import com.mordrum.mciv.common.networking.messages.ChunkSyncMessage;
import com.mordrum.mciv.common.networking.messages.ChunkClaimMessage;
import com.mordrum.mcore.MCore;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.json.JSONObject;

import java.util.logging.Logger;

public class ClaimChunkMessageHandler implements IMessageHandler<ChunkClaimMessage.Request, IMessage> {
	private static final Logger logger;

	static {
		Unirest.setDefaultHeader("accept", "application/json");
		Unirest.setDefaultHeader("content-type", "application/json");
		logger = Logger.getLogger("mciv.apihelper");
	}

	@Override
	public IMessage onMessage(ChunkClaimMessage.Request message, MessageContext ctx) {
		EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

		int chunkX = playerEntity.getPosition().getX() >> 4;
		int chunkZ = playerEntity.getPosition().getZ() >> 4;
		JSONObject body = new JSONObject()
				.put("x", chunkX)
				.put("z", chunkZ)
				.put("uuid", playerEntity.getUniqueID().toString());

		Unirest.post(MCore.API_URL + "/chunks")
				.body(body)
				.asJsonAsync(new Callback<JsonNode>() {
					@Override
					public void completed(HttpResponse<JsonNode> response) {
						if (response.getStatus() == 200) {
							playerEntity.sendMessage(new TextComponentString("Chunk claimed successfully"));
							int civilizationId = response.getBody().getObject().getJSONObject("civilization").getInt("id");
							CommonProxy.Companion.getNETWORK_WRAPPER().sendToAll(new ChunkSyncMessage(chunkX, chunkZ, civilizationId));
						} else {
							logError("POST", "/chunks", response);
						}
					}

					@Override
					public void failed(UnirestException e) {

					}

					@Override
					public void cancelled() {

					}
				});

		return null;
	}


	private static void logError(String method, String route, HttpResponse<JsonNode> response) {
		JSONObject object = response.getBody().getObject();
		logger.severe(String.format("Failed API call to '%s %s': %s", "GET", "/civilizations", object.getString("message")));
		logger.severe(object.getString("description"));
	}
}
