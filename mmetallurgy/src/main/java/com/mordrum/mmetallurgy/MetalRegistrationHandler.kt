package com.mordrum.mmetallurgy

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

object MetalRegistrationHandler {
    val blocksToRegister = ArrayList<Block>()
    val itemsToRegister = ArrayList<Item>()
    val recipesToRegister = ArrayList<IRecipe>()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        blocksToRegister.forEach { event.registry.register(it) }
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        itemsToRegister.forEach { event.registry.register(it) }
    }

    @SubscribeEvent
    fun registerRecipes(event: RegistryEvent.Register<IRecipe>) {
        recipesToRegister.forEach { event.registry.register(it) }
    }
}