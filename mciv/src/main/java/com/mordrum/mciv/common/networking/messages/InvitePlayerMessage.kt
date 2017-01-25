package com.mordrum.mciv.common.networking.messages

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

class InvitePlayerMessage {
    class Request() : IMessage {
        lateinit var playerToInvite: String

        constructor(playerToInvite: String) : this() {
            this.playerToInvite = playerToInvite
        }

        override fun toBytes(buf: ByteBuf?) {
            ByteBufUtils.writeUTF8String(buf, this.playerToInvite)
        }

        override fun fromBytes(buf: ByteBuf?) {
            this.playerToInvite = ByteBufUtils.readUTF8String(buf)
        }
    }

    class Response() : IMessage {
        lateinit var message: String

        constructor(message: String): this() {
            this.message = message
        }

        override fun toBytes(buf: ByteBuf?) {
            ByteBufUtils.writeUTF8String(buf, this.message)
        }

        override fun fromBytes(buf: ByteBuf?) {
            this.message = ByteBufUtils.readUTF8String(buf)
        }
    }
}