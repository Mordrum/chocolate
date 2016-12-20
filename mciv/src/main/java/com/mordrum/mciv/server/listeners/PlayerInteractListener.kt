package com.mordrum.mciv.server.listeners

import com.mordrum.mciv.common.CommonProxy
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class PlayerInteractListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun playerInteract(event: PlayerInteractEvent) {
        val player = event.entityPlayer
        val pos = event.pos
        val chunkX = pos.x shr 4
        val chunkZ = pos.z shr 4

        if (CommonProxy.getChunkCache().containsKey(chunkX) && CommonProxy.getChunkCache()[chunkX]!!.containsKey(chunkZ)) {
            val civId = CommonProxy.getChunkCache()[chunkX]!![chunkZ]!!
            if (!CommonProxy.getCivilizationMap()[civId]!!.players.contains(player.uniqueID)) {
                event.isCanceled = true
                event.result = Event.Result.DENY
                player.sendMessage(TextComponentString("This land is owned by the civilization of ${CommonProxy.getCivilizationMap()[civId]!!.name}"))
            }
        }
    }
}