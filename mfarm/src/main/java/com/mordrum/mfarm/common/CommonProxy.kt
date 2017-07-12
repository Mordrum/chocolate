package com.mordrum.mfarm.common

import com.google.common.collect.ImmutableMap
import com.mordrum.mfarm.MFarm
import com.mordrum.mfarm.common.block.Cask
import com.mordrum.mfarm.common.networking.BeginFermentationMessage
import com.mordrum.mfarm.common.networking.BeginFermentationMessageHandler
import com.mordrum.mfarm.common.recipe.CaskRecipe
import com.mordrum.mfarm.common.tileentities.CaskTileEntity
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.oredict.OreDictionary

@Mod.EventBusSubscriber(modid = MFarm.MOD_ID)
open class CommonProxy {
    companion object {
        val NETWORK_WRAPPER: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MFarm.MOD_ID)
        var DISCRIMINATOR = 0
        var ringBlockItemToBlockMap: ImmutableMap<Item, Block> = ImmutableMap.of()
    }

    open fun preInit(event: FMLPreInitializationEvent) {
        NETWORK_WRAPPER.registerMessage(BeginFermentationMessageHandler::class.java, BeginFermentationMessage::class.java, DISCRIMINATOR++, Side.SERVER)

        MinecraftForge.EVENT_BUS.register(GeneticsListener())
        MinecraftForge.EVENT_BUS.register(GeneListener())
    }

    open fun init(event: FMLInitializationEvent) {
    }

    open fun registerRecipes(event: RegistryEvent.Register<IRecipe>) {
        ringBlockItemToBlockMap = ImmutableMap.Builder<Item, Block>()
                // Vanilla stuff
                .put(Items.IRON_INGOT, Blocks.IRON_BLOCK)
                .put(Items.GOLD_INGOT, Blocks.GOLD_BLOCK)
                .put(Items.DIAMOND, Blocks.DIAMOND_BLOCK)
                .put(Items.EMERALD, Blocks.EMERALD_BLOCK)
                .put(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK), Blocks.REDSTONE_BLOCK)
                .put(Item.getItemFromBlock(Blocks.LAPIS_BLOCK), Blocks.LAPIS_BLOCK)
                .put(Item.getItemFromBlock(Blocks.GLOWSTONE), Blocks.GLOWSTONE)
                .put(Item.getItemFromBlock(Blocks.NETHERRACK), Blocks.NETHERRACK)
                .put(Item.getItemFromBlock(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_BLOCK)
                // mMetallurgy
                .put(Item.getByNameOrId("mmetallurgy:copper_ingot"), Block.getBlockFromName("mmetallurgy:mmetallurgy.copper.block"))
                .put(Item.getByNameOrId("mmetallurgy:tin_ingot"), Block.getBlockFromName("mmetallurgy:mmetallurgy.tin.block"))
                .put(Item.getByNameOrId("mmetallurgy:bronze_ingot"), Block.getBlockFromName("mmetallurgy:mmetallurgy.bronze.block"))
                .put(Item.getByNameOrId("mmetallurgy:manganese_ingot"), Block.getBlockFromName("mmetallurgy:mmetallurgy.manganese.block"))
                .put(Item.getByNameOrId("mmetallurgy:hepatizon_ingot"), Block.getBlockFromName("mmetallurgy:mmetallurgy.hepatizon.block"))
                .put(Item.getByNameOrId("mmetallurgy:steel_ingot"), Block.getBlockFromName("mmetallurgy:mmetallurgy.steel.block"))
                .put(Item.getByNameOrId("mmetallurgy:damascus_steel_ingot"), Block.getBlockFromName("mmetallurgy:mmetallurgy.damascus_steel.block"))
                .put(Item.getByNameOrId("mmetallurgy:calitonium_ingot"), Block.getBlockFromName("mmetallurgy:mmetallurgy.calitonium.block"))
                .put(Item.getByNameOrId("mmetallurgy:grifonium_ingot"), Block.getBlockFromName("mmetallurgy:mmetallurgy.grifonium.block"))
                .build()

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

        planks.forEach { barrelPlank ->
            planks.forEach { feetPlank ->
                ringBlockItemToBlockMap.keys.forEach { ringItem ->
                    event.registry.register(CaskRecipe(cask, ItemStack(ringItem), barrelPlank, feetPlank))
                }
            }
        }
    }
}