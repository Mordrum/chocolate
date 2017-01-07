package com.mordrum.mdeco.common.networking

import com.mordrum.mdeco.common.tileentities.CaskState
import com.mordrum.mdeco.common.tileentities.CaskTileEntity
import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

class CaskStateChangedMessage : IMessage {
    lateinit var tileEntity: CaskTileEntity
    lateinit var caskState: CaskState

    constructor()

    constructor(tileEntity: CaskTileEntity, caskState: CaskState) {
        this.tileEntity = tileEntity
        this.caskState = caskState
    }

    override fun fromBytes(buf: ByteBuf?) {
        val x = ByteBufUtils.readVarInt(buf, 5)
        val y = ByteBufUtils.readVarInt(buf, 5)
        val z = ByteBufUtils.readVarInt(buf, 5)
        val dimension = ByteBufUtils.readVarInt(buf, 5)

        val te = DimensionManager.getWorld(dimension).getTileEntity(BlockPos(x, y, z))
        if (te is CaskTileEntity) this.tileEntity = te

        this.caskState = CaskState.valueOf(ByteBufUtils.readUTF8String(buf))
    }

    override fun toBytes(buf: ByteBuf?) {
        ByteBufUtils.writeVarInt(buf, this.tileEntity.pos.x, 5)
        ByteBufUtils.writeVarInt(buf, this.tileEntity.pos.y, 5)
        ByteBufUtils.writeVarInt(buf, this.tileEntity.pos.z, 5)
        ByteBufUtils.writeVarInt(buf, this.tileEntity.world.provider.dimension, 5)
        ByteBufUtils.writeUTF8String(buf, this.caskState.toString())
    }

}
