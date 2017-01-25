package com.mordrum.mcore.common.util

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

fun MessageContext.getPlayer(): EntityPlayer {
    if (this.side == Side.SERVER)
        return this.serverHandler.playerEntity
    else
        return Minecraft.getMinecraft().player
}

fun MessageContext.getWorld(): World {
    if (this.side == Side.SERVER)
        return this.serverHandler.playerEntity.world
    else
        return Minecraft.getMinecraft().world
}