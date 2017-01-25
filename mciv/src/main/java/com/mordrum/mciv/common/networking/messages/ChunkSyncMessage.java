package com.mordrum.mciv.common.networking.messages;

import com.mordrum.mciv.common.CommonProxy;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class ChunkSyncMessage implements IMessage {
	private int x;
	private int z;
	private long civ;

	public ChunkSyncMessage() {
	}

	public ChunkSyncMessage(int x, int z, long civ) {
		this.x = x;
		this.z = z;
		this.civ = civ;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.z = buf.readInt();
		this.civ = buf.readLong();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(z);
		buf.writeLong(civ);
	}


	public static class Handler implements IMessageHandler<ChunkSyncMessage, IMessage> {
		@Override
		public IMessage onMessage(ChunkSyncMessage message, MessageContext ctx) {
			Map<Integer, Map<Integer, Long>> chunkCache = CommonProxy.Companion.getChunkCache();

			if (!chunkCache.containsKey(message.x)) {
				chunkCache.put(message.x, new HashMap<>());
			}

			chunkCache.get(message.x).put(message.z, message.civ);
			System.out.println("Synced chunk " + message.civ + "," + message.z + " to " + message.civ);

			return null;
		}
	}
}
