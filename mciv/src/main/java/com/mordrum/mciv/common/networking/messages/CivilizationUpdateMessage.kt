package com.mordrum.mciv.common.networking.messages

import com.google.gson.Gson
import com.mordrum.mciv.common.models.Civilization
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

class CivilizationUpdateMessage() : IMessage {
    lateinit var civilization: Civilization

    constructor(civilization: Civilization): this() {
            this.civilization = civilization
    }

    override fun fromBytes(buf: ByteBuf?) {
        val civAsJson = ByteBufUtils.readUTF8String(buf)
        this.civilization = Gson().fromJson(civAsJson, Civilization::class.java)

    }

    override fun toBytes(buf: ByteBuf?) {
        ByteBufUtils.writeUTF8String(buf, Gson().toJson(this.civilization))
    }
}