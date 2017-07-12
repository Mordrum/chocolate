package com.mordrum.mciv.server.networking;

import com.mordrum.mciv.common.CommonProxy;
import com.mordrum.mciv.common.networking.messages.CreateCivMessage;
import com.mordrum.mciv.common.networking.messages.SyncCivMessage;
import com.mordrum.mciv.server.ServerAPIHelper;
import com.mordrum.mcore.common.util.ServerUtilities;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CreateCivMessageHandler implements IMessageHandler<CreateCivMessage.Request, CreateCivMessage.Response> {

	@Override
	public CreateCivMessage.Response onMessage(CreateCivMessage.Request message, MessageContext ctx) {
		EntityPlayerMP playerEntity = ctx.getServerHandler().player;

		if (!playerHasEnoughGold(playerEntity, 3)) {
			playerEntity.sendMessage(new TextComponentString("You must have 3 gold blocks to create a new civilization"));
			return null;
		}

		ServerAPIHelper.INSTANCE.createCivilization(message.getCivName(), message.getBannerId(), playerEntity, (response) -> {
			playerEntity.inventory.clearMatchingItems(Item.getItemFromBlock(Blocks.GOLD_BLOCK), -1, 3, null);
			ServerUtilities.broadcastMessageToPlayers(new TextComponentString(playerEntity.getName() + " has founded the civilization of " + message.getCivName()));
			CommonProxy.Companion.getNETWORK_WRAPPER().sendToAll(new SyncCivMessage(response.getId(), true));
			CommonProxy.Companion.syncCivilization(response.getId(), true);
		}, (error) -> {
			playerEntity.sendMessage(new TextComponentString("Failed to create civilization: " + error.getMessage()));

		});

		return null;
	}

	private boolean playerHasEnoughGold(EntityPlayerMP player, int requiredAmount) {
		int foundSoFar = 0;

		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			ItemStack itemStack = player.inventory.mainInventory.get(i);
			if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.GOLD_BLOCK))) {
				foundSoFar += itemStack.getCount();
			}
		}
		return foundSoFar >= requiredAmount;
	}
}
