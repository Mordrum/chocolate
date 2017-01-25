package com.mordrum.mciv.common.networking.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class CreateCivMessage {
	public static class Request implements IMessage {
		private String bannerId = "";
		private String civName = "";

		public Request() {
		}

		public Request(String bannerId, String civName) {
			this.bannerId = bannerId;
			this.civName = civName;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			bannerId = ByteBufUtils.readUTF8String(buf);
			civName = ByteBufUtils.readUTF8String(buf);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			ByteBufUtils.writeUTF8String(buf, bannerId);
			ByteBufUtils.writeUTF8String(buf, civName);
		}

		public String getBannerId() {
			return bannerId;
		}

		public String getCivName() {
			return civName;
		}
	}

	public static class Response implements IMessage {
		private boolean success;
		private String text;

		public Response() {
		}

		public Response(boolean success, String text) {
			this.success = success;
			this.text = text;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			success = buf.readBoolean();
			text = ByteBufUtils.readUTF8String(buf);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(success);
			ByteBufUtils.writeUTF8String(buf, text);
		}

		public boolean isSuccess() {
			return success;
		}

		public String getText() {
			return text;
		}
	}
}
