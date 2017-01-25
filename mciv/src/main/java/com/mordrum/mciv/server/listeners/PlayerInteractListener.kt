package com.mordrum.mciv.server.listeners

import com.mordrum.mciv.common.CommonProxy
import com.mordrum.mciv.common.models.Player
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

        if (CommonProxy.chunkCache.containsKey(chunkX) && CommonProxy.chunkCache[chunkX]!!.containsKey(chunkZ)) {
            val civId = CommonProxy.chunkCache[chunkX]!![chunkZ]!!
            val playerModel = Player()
            playerModel.uuid = player.uniqueID
            if (!CommonProxy.civilizationMap[civId]!!.players.contains(playerModel)) {
                event.isCanceled = true
                event.result = Event.Result.DENY
                player.sendMessage(TextComponentString("This land is owned by the civilization of ${CommonProxy.civilizationMap[civId]!!.name}"))
            }
        }
    }
}