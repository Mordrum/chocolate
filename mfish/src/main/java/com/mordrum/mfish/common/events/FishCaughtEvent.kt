package com.mordrum.mfish.common.events

import com.mordrum.mfish.common.Fish
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityFishHook
import net.minecraftforge.fml.common.eventhandler.Event

class FishCaughtEvent(val player: EntityPlayer, val fishHook: EntityFishHook, val fish: Fish, val weight: Double) : Event()
