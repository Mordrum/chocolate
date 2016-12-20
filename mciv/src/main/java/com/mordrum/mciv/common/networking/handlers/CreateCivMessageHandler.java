package com.mordrum.mciv.common.networking.handlers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.RequestBodyEntity;
import com.mordrum.mciv.MCiv;
import com.mordrum.mciv.common.CommonProxy;
import com.mordrum.mciv.common.networking.messages.CreateCivMessage;
import com.mordrum.mciv.common.networking.messages.SyncCivMessage;
import com.mordrum.mcore.MCore;
import com.mordrum.mcore.common.util.ServerUtilities;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.json.JSONObject;

import java.util.logging.Logger;

public class CreateCivMessageHandler implements IMessageHandler<CreateCivMessage.Request, CreateCivMessage.Response> {

	@Override
	public CreateCivMessage.Response onMessage(CreateCivMessage.Request message, MessageContext ctx) {
		EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

		if (!playerHasEnoughGold(playerEntity, 3)) {
			playerEntity.sendMessage(new TextComponentString("You must have 3 gold blocks to create a new civilization"));
			return null;
		}

//		if (playerIsNotFarEnoughAway) {
//			playerEntity.addChatComponentMessage(new TextComponentString("You must be at least 60 blocks from the nearest civilization"));
//			return null;
//		}

		JSONObject body = new JSONObject()
				.put("player", playerEntity.getUniqueID())
				.put("name", message.getCivName())
				.put("banner", message.getBannerId())
				.put("home_x", playerEntity.getPosition().getX() >> 4)
				.put("home_z", playerEntity.getPosition().getZ() >> 4);
		RequestBodyEntity entity = Unirest.post(MCore.API_URL + "/civilizations")
				.header("accept", "application/json")
				.header("Content-Type", "application/json")
				.body(body);
		entity.asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> response) {
				JSONObject body = response.getBody().getObject();
				if (response.getStatus() == 200) {
					playerEntity.inventory.clearMatchingItems(Item.getItemFromBlock(Blocks.GOLD_BLOCK), -1, 3, null);
					ServerUtilities.broadcastMessageToPlayers(new TextComponentString(playerEntity.getName() + " has founded the civilization of " + message.getCivName()));
					long civilizationId = response.getBody().getObject().getLong("id");
					CommonProxy.NETWORK_WRAPPER.sendToAll(new SyncCivMessage(civilizationId, true));
					CommonProxy.syncCivilization(civilizationId, true);
				} else {
					playerEntity.sendMessage(new TextComponentString("Failed to create civilization: " + body.getString("message")));
				}
			}

			@Override
			public void failed(UnirestException e) {
				playerEntity.sendMessage(new TextComponentString("An error occurred while trying to create your civilization"));
				Logger.getLogger(MCiv.MOD_ID).severe(e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void cancelled() {
				playerEntity.sendMessage(new TextComponentString("An error occurred while trying to create your civilization"));
				Logger.getLogger(MCiv.MOD_ID).severe("Civilization creation cancelled");
			}
		});

		return null;
	}

	private boolean playerHasEnoughGold(EntityPlayerMP player, int requiredAmount) {
		int foundSoFar = 0;

		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			ItemStack itemStack = player.inventory.mainInventory.get(i);
			if (itemStack != null && itemStack.getItem().equals(Item.getItemFromBlock(Blocks.GOLD_BLOCK))) {
				foundSoFar += itemStack.getCount();
			}
		}
		return foundSoFar >= requiredAmount;
	}
}
