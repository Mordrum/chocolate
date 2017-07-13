package com.mordrum.mclimate.common

import com.mordrum.mclimate.common.biome.BiomeFrozenLake
import com.mordrum.mclimate.common.biome.BiomeLake
import net.minecraft.world.biome.Biome
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

open class CommonProxy {
    open fun onPreInit(event: FMLPreInitializationEvent) {

    }

    open fun onInit(event: FMLInitializationEvent) {}

    companion object {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        fun registerBiomes(event: RegistryEvent.Register<Biome>) {
            val biomeLake = BiomeLake()
            val biomeFrozenLake = BiomeFrozenLake()
            event.registry.registerAll(biomeLake, biomeFrozenLake)
        }
    }
}
