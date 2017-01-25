package com.mordrum.mciv.common.networking.messages;

import com.mordrum.mciv.common.CommonProxy;
import io.netty.buffer.ByteBuf;
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
			CommonProxy.Companion.syncCivilization(message.civilizationId, true);
			return null;
		}
	}
}
