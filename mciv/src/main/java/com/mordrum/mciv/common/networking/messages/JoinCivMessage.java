package com.mordrum.mciv.common.networking.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class JoinCivMessage implements IMessage {
	private String civName;
	private UUID playerId;

	public JoinCivMessage() {
	}

	public JoinCivMessage(String bannerId, String civName, UUID playerId) {
		this.civName = civName;
		this.playerId = playerId;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		civName = ByteBufUtils.readUTF8String(buf);
		playerId = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, civName);
		ByteBufUtils.writeUTF8String(buf, playerId.toString());
	}
}
