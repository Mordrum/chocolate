package com.mordrum.mcore.common.stats

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.gameevent.TickEvent

abstract class Stat(val player: EntityPlayer) {
    var clientNeedsUpdate: Boolean = false
    private set
    get() {
        val stored = field
        if (field) field = false
        return stored
    }

    fun clientNeedsUpdate() {
        this.clientNeedsUpdate = true
    }

    abstract fun serializeToNBT(): NBTTagCompound
    abstract fun deserializeFromNBT(nbt: NBTTagCompound)
    abstract fun update(phase: TickEvent.Phase)
}