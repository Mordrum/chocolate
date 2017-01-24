package com.mordrum.mciv.common.networking.messages

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

class InviteRequestMessage {
    // Sent to the player that is being invited
    class Request() : IMessage {
        lateinit var message: String

        constructor(message: String): this() {
            this.message = message
        }

        override fun fromBytes(buf: ByteBuf?) {
            this.message = ByteBufUtils.readUTF8String(buf)
        }

        override fun toBytes(buf: ByteBuf?) {
            ByteBufUtils.writeUTF8String(buf, this.message)
        }

    }

    // Sent by the player containing their response to the invite
    class Response() : IMessage {
        var accepted: Boolean = false

        constructor(accepted: Boolean): this() {
            this.accepted = accepted
        }

        override fun fromBytes(buf: ByteBuf?) {
            val readVarInt = ByteBufUtils.readVarShort(buf)
            if (readVarInt == 0) this.accepted = false
            else if (readVarInt == 1) this.accepted = true
        }

        override fun toBytes(buf: ByteBuf?) {
            val short = if (this.accepted) 1 else 0
            ByteBufUtils.writeVarShort(buf, short)
        }
    }
}