package com.mordrum.mcore.common.stats

import net.minecraft.entity.player.EntityPlayer

fun EntityPlayer.getStat(name: String): Stat {
    return StatManager.getStatForPlayer(name, this)
}