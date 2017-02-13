package com.mordrum.mcore.common.network

import com.mordrum.mcore.common.stats.StatManager
import com.mordrum.mcore.common.util.getPlayer
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class MessageUpdateStat() : IMessage, IMessageHandler<MessageUpdateStat, IMessage> {
    lateinit var identifier: String
    lateinit var nbt: NBTTagCompound

    constructor(identifier: String, nbt: NBTTagCompound) : this() {
        this.identifier = identifier
        this.nbt = nbt
    }

    override fun fromBytes(buf: ByteBuf?) {
        this.identifier = ByteBufUtils.readUTF8String(buf)
        this.nbt = ByteBufUtils.readTag(buf)!!
    }

    override fun toBytes(buf: ByteBuf?) {
        ByteBufUtils.writeUTF8String(buf, this.identifier)
        ByteBufUtils.writeTag(buf, this.nbt)
    }

    override fun onMessage(message: MessageUpdateStat, ctx: MessageContext): IMessage? {
        val player = ctx.getPlayer()

        if (player != null) {
            val stat = StatManager.getStatForPlayer(message.identifier, player)
            stat.deserializeFromNBT(message.nbt)
        }

        return null
    }
}