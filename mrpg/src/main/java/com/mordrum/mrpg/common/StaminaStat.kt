package com.mordrum.mrpg.common

import com.mordrum.mcore.common.stats.Stat
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.gameevent.TickEvent

class StaminaStat(player: EntityPlayer) : Stat(player) {
    var ticks: Long = 0
    var stamina: Float = 0.0f
        set(value) {
            if (value < 0f) field = value
            else if (value > maxStamina) field = maxStamina
            else field = value
        }
    var maxStamina: Float = 100.0f

    override fun serializeToNBT(): NBTTagCompound {
        val tag = NBTTagCompound()
        tag.setFloat("value", stamina)
        tag.setFloat("maxStamina", maxStamina)
        return tag
    }

    override fun deserializeFromNBT(nbt: NBTTagCompound) {
        stamina = nbt.getFloat("value")
        maxStamina = nbt.getFloat("maxStamina")
    }

    override fun update(phase: TickEvent.Phase) {
        if (phase == TickEvent.Phase.END) {
            ticks++
            if (this.player.isSprinting && !this.player.isCreative) {
                if (ticks % 20L == 0L) {
                    this.stamina -= 3
                    this.clientNeedsUpdate()
                }

                if (stamina <= 0) this.player.isSprinting = false
            } else {
                if (ticks % 20L == 0L) {
                    this.stamina++
                    this.clientNeedsUpdate()
                }
            }
        }
    }
}