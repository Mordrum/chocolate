package com.mordrum.mdeco.common

import com.mordrum.mdeco.common.block.Cask
import com.mordrum.mdeco.common.recipe.CaskRecipe
import com.mordrum.mdeco.common.tileentities.CaskTileEntity
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary

open class CommonProxy {
    fun preInit(event: FMLPreInitializationEvent) {
        val cask = Cask()
        cask.register()
        GameRegistry.registerTileEntity(CaskTileEntity::class.java, "caskTileEntity")

        val planks = OreDictionary.getOres("plankWood")
        Block.REGISTRY.keys
                .filter {
                    it.resourcePath.toLowerCase().contains("_planks")
                }.forEach {
                    planks.add(ItemStack(Block.REGISTRY.getObject(it)))
                }
        val ringBlocks = arrayOf(
                Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK, Blocks.REDSTONE_BLOCK, Blocks.LAPIS_BLOCK,
                Blocks.COAL_BLOCK, Blocks.GLOWSTONE, Blocks.QUARTZ_BLOCK, Blocks.NETHERRACK,
                Block.getBlockFromName("mmetallurgy:mmetallurgy.copper.block"), Block.getBlockFromName("mmetallurgy:mmetallurgy.tin.block"),
                Block.getBlockFromName("mmetallurgy:mmetallurgy.bronze.block"), Block.getBlockFromName("mmetallurgy:mmetallurgy.manganese.block"),
                Block.getBlockFromName("mmetallurgy:mmetallurgy.hepatizon.block"), Block.getBlockFromName("mmetallurgy:mmetallurgy.steel.block"),
                Block.getBlockFromName("mmetallurgy:mmetallurgy.damascus_steel.block")
        ).map(::ItemStack)

        planks.forEach { barrelPlank ->
            planks.forEach { feetPlank ->
                ringBlocks.forEach { ringBlock ->
                    CraftingManager.getInstance().addRecipe(CaskRecipe(cask, ringBlock, barrelPlank, feetPlank))
                }
            }
        }
    }

    fun init(event: FMLInitializationEvent) {
    }
}