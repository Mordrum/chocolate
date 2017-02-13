package com.mordrum.mcore.common

import com.mordrum.mcore.MCore
import com.mordrum.mcore.common.network.MessageUpdateStat
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

open class CommonProxy {
    companion object {
        // Networking stuff
        val NETWORK_WRAPPER: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MCore.MODID)
        private var DISCRIMINATOR = 0
    }

    open fun onPreInit() {
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(CreatureExplosionHealListener())
        MinecraftForge.EVENT_BUS.register(BedSpawnListener())

        NETWORK_WRAPPER.registerMessage(MessageUpdateStat::class.java, MessageUpdateStat::class.java, DISCRIMINATOR++, Side.CLIENT)
    }

    open fun onLoad() {
        MultiMineServer()
    }

    @SubscribeEvent
    fun onServerStart(event: WorldEvent.Load) {
    }
}
