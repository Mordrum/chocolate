package com.mordrum.mfarm.common.networking

import com.mordrum.mfarm.common.tileentities.CaskTileEntity
import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

class BeginFermentationMessage : IMessage {
    lateinit var tileEntity: CaskTileEntity

    constructor()

    constructor(tileEntity: CaskTileEntity) {
        this.tileEntity = tileEntity
    }

    override fun fromBytes(buf: ByteBuf?) {
        val x = ByteBufUtils.readVarInt(buf, 5)
        val y = ByteBufUtils.readVarInt(buf, 5)
        val z = ByteBufUtils.readVarInt(buf, 5)
        val dimension = ByteBufUtils.readVarInt(buf, 5)

        val te = DimensionManager.getWorld(dimension).getTileEntity(BlockPos(x, y, z))
        if (te is CaskTileEntity) this.tileEntity = te
    }

    override fun toBytes(buf: ByteBuf?) {
        ByteBufUtils.writeVarInt(buf, this.tileEntity.pos.x, 5)
        ByteBufUtils.writeVarInt(buf, this.tileEntity.pos.y, 5)
        ByteBufUtils.writeVarInt(buf, this.tileEntity.pos.z, 5)
        ByteBufUtils.writeVarInt(buf, this.tileEntity.world.provider.dimension, 5)
    }

}