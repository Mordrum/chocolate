package com.mordrum.mciv.common

import com.google.common.collect.Maps
import com.google.common.eventbus.EventBus
import com.mordrum.mciv.MCiv
import com.mordrum.mciv.client.ClientAPIHelper
import com.mordrum.mciv.client.networking.InviteRequestHandler
import com.mordrum.mciv.client.networking.InvitePlayerResultHandler
import com.mordrum.mciv.common.models.Civilization
import com.mordrum.mciv.server.networking.ClaimChunkMessageHandler
import com.mordrum.mciv.server.networking.CreateCivMessageHandler
import com.mordrum.mciv.common.networking.messages.*
import com.mordrum.mciv.server.networking.InviteHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side
import java.util.*
import java.util.function.BiConsumer
import kotlin.reflect.KClass

open class CommonProxy {
    open fun onPreInit(event: FMLPreInitializationEvent) {
        registerMessageHandler(CreateCivMessageHandler::class, CreateCivMessage.Request::class, Side.SERVER)
        registerMessageHandler(ClaimChunkMessageHandler::class, ChunkClaimMessage.Request::class, Side.SERVER)
        registerMessageHandler(ChunkSyncMessage.Handler::class, ChunkSyncMessage::class, Side.CLIENT)
        registerMessageHandler(SyncCivMessage.Handler::class, SyncCivMessage::class, Side.CLIENT)

        // Packet sent from client to server when client wants to invite player X
        // Packet sent from server to client letting them know if the invite was sent or not
        // Packet sent from server to client to tell them that they have been invited
        // Packet sent from client to server to tell them if they have accepted the invite or not

        registerMessageHandler(InviteHandler.InvitePlayer::class, InvitePlayerMessage.Request::class, Side.SERVER)
        registerMessageHandler(InvitePlayerResultHandler::class, InvitePlayerMessage.Response::class, Side.CLIENT)

        registerMessageHandler(InviteRequestHandler::class, InviteRequestMessage.Request::class, Side.CLIENT)
        registerMessageHandler(InviteHandler.ResponseHandler::class, InviteRequestMessage.Response::class, Side.SERVER)
    }

    open fun onInit(event: FMLInitializationEvent) {
        populateCaches()
    }

    fun onServerStarting(event: FMLServerStartingEvent) {}

    fun <REQ : IMessage, REPLY : IMessage> registerMessageHandler(handler: KClass<out IMessageHandler<REQ, REPLY>>, message: KClass<REQ>, side: Side) {
        DISCRIMINATOR++
        NETWORK_WRAPPER.registerMessage(handler.java, message.java, DISCRIMINATOR, side)
    }

    companion object {
        // Networking stuff
        val NETWORK_WRAPPER: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MCiv.MOD_ID)
        private var DISCRIMINATOR = 0
        val bus: EventBus = EventBus("mciv_bus")

        val chunkCache: MutableMap<Int, MutableMap<Int, Long>> = HashMap()
        val civilizationMap: MutableMap<Long, Civilization> = HashMap()

        fun populateCaches() {
            civilizationMap.clear()
            chunkCache.clear()
            ClientAPIHelper.getCivilizations(Maps.newHashMapWithExpectedSize<String, Any>(0), listOf("chunks"), BiConsumer { err, civilizations ->
                if (err == null) {
                    for (civilization in civilizations) {
                        civilizationMap.put(civilization.id, civilization)
                        syncCivilizationChunks(civilization)
                    }
                } else {
                    //TODO more graceful error handling, should probably crash here
                }
            })
        }

        fun syncCivilization(civilizationId: Long, syncChunks: Boolean) {
            val with = listOf<String>()
            if (syncChunks) with + "chunks"

            ClientAPIHelper.getCivilizations(mapOf(Pair("id", civilizationId)), with, BiConsumer { err, civilizations ->
                val civilization = civilizations[0]
                civilizationMap.put(civilization.id, civilization)
                if (syncChunks) syncCivilizationChunks(civilization)
            })
        }

        private fun syncCivilizationChunks(civilization: Civilization) {
            for (chunk in civilization.chunks) {
                if (!chunkCache.containsKey(chunk.position.x)) chunkCache.put(chunk.position.x, HashMap<Int, Long>())
                chunkCache[chunk.position.x]!!.put(chunk.position.z, civilization.id)
            }
        }
    }
}
