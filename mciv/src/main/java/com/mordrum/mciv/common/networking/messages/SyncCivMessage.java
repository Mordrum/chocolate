package com.mordrum.mciv.common.networking.messages;

import com.google.common.collect.Lists;
import com.mordrum.mciv.common.Civilization;
import com.mordrum.mciv.common.CommonProxy;
import com.mordrum.mciv.common.networking.ClientAPIHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncCivMessage implements IMessage {
	private long civilizationId;
	private boolean syncChunks;

	public SyncCivMessage() {
	}

	public SyncCivMessage(long civilizationId) {
		this.civilizationId = civilizationId;
		this.syncChunks = false;
	}

	public SyncCivMessage(long civilizationId, boolean syncChunks) {
		this.civilizationId = civilizationId;
		this.syncChunks = syncChunks;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.civilizationId = buf.readLong();
		this.syncChunks = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.civilizationId);
		buf.writeBoolean(this.syncChunks);
	}

	public static class Handler implements IMessageHandler<SyncCivMessage, IMessage> {
		@Override
		public IMessage onMessage(SyncCivMessage message, MessageContext ctx) {
			ClientAPIHelper.findCivilizations(Lists.asList("id", new String[]{Long.toString(message.civilizationId)}), (civilizations -> {
				for (Civilization civilization : civilizations) {
					Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Sync'd civilization #" + civilization.getId()));
					CommonProxy.getCivilizationMap().put(civilization.getId(), civilization);
					if (message.syncChunks) {
						CommonProxy.syncCivilizationChunks(civilization.getId());
					}
				}
			}));
			return null;
		}
	}
}
