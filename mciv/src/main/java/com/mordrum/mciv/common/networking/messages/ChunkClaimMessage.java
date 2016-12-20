package com.mordrum.mciv.common.networking.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class ChunkClaimMessage {
	public static class Request implements IMessage {
		public Request() {
		}

		@Override
		public void fromBytes(ByteBuf buf) {
		}

		@Override
		public void toBytes(ByteBuf buf) {
		}
	}

}
