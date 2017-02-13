package com.mordrum.mcore.common.stats

import com.mordrum.mcore.common.CommonProxy
import com.mordrum.mcore.common.network.MessageUpdateStat
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.primaryConstructor

object StatManager {
    private val stats: MutableMap<String, KClass<out Stat>> = HashMap()
    private val instances: MutableMap<String, MutableMap<UUID, Stat>> = HashMap()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun registerStat(name: String, stat: KClass<out Stat>) {
        if (stats.containsKey(name)) {
            throw Exception("A stat with the name $name has already been registered")
        } else {
            stats[name] = stat
            instances[name] = HashMap()
        }
    }

    fun getStat(name: String): KClass<out Stat>? {
        return stats[name]
    }

    fun getStatForPlayer(name: String, player: EntityPlayer): Stat {
        val stat = instances[name]!![player.uniqueID] ?: throw Exception("Stat with name $name is not registered")
        return stat
    }

    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        // Initialize the stats on both client and server
        for ((name, kClass) in stats) {
            val instance = kClass.primaryConstructor!!.call(event.player)
            instances[name]!![event.player.uniqueID] = instance

            // On the server only, deserialize the stat, then send it to the client
            if (!event.player.world.isRemote) {
                // If the player has this stat saved, deserialize it, otherwise just save the defaults
                if (event.player.entityData.hasKey(name)) {
                    instance.deserializeFromNBT(event.player.entityData.getCompoundTag(name))
                } else {
                    event.player.entityData.setTag(name, instance.serializeToNBT())
                }
                CommonProxy.NETWORK_WRAPPER.sendTo(MessageUpdateStat(name, instance.serializeToNBT()), event.player as EntityPlayerMP)
            }
        }
    }

    @SubscribeEvent
    fun onPlayerJoinClientside(event: EntityJoinWorldEvent) {
        if (event.entity !is EntityPlayer || !event.entity.world.isRemote) return

        val player = event.entity as EntityPlayer
        for ((name, kClass) in stats) {
            // Prevent stats from being initialized twice
            if (!instances[name]!!.contains(player.uniqueID)) {
                val instance = kClass.primaryConstructor!!.call(player)
                instances[name]!![player.uniqueID] = instance
            }
        }
    }

    @SubscribeEvent
    fun onPlayerLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        if (event.player.world.isRemote) return

        for ((name, mutableMap) in instances) {
            event.player.entityData.setTag(name, mutableMap[event.player.uniqueID]!!.serializeToNBT())
            mutableMap.remove(event.player.uniqueID)
        }
    }

    @SubscribeEvent
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        if (event.player.world.isRemote) return

        for ((name, mutableMap) in instances) {
            val instance = mutableMap[event.player.uniqueID]!!
            instance.update(event.phase)
            if (instance.clientNeedsUpdate) {
                CommonProxy.NETWORK_WRAPPER.sendTo(MessageUpdateStat(name, instance.serializeToNBT()), event.player as EntityPlayerMP)
            }
        }
    }
}