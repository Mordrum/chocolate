package com.mordrum.mmetallurgy.items

import com.mordrum.mmetallurgy.MMetallurgy
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemHoe
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemSpade
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry

object Tools {
    private val recipePatterns = arrayOf(arrayOf("XXX", " # ", " # "), arrayOf("X", "#", "#"), arrayOf("XX", "X#", " #"), arrayOf("XX", " #", " #"), arrayOf(" X", " X", " #"))

    class Axe(metal: String, material: Item.ToolMaterial) : ItemAxe(material, material.damageVsEntity, material.efficiencyOnProperMaterial) {
        init {
            initTool(this, metal, "axe")
            GameRegistry.addShapedRecipe(ResourceLocation(MMetallurgy.MOD_ID, metal + "_axe"), null, ItemStack(this),
                    *recipePatterns[2],
                    'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metal + "_ingot"),
                    '#', Items.STICK)
        }
    }

    class Hoe(metal: String, material: Item.ToolMaterial) : ItemHoe(material) {
        init {
            initTool(this, metal, "hoe")
            GameRegistry.addShapedRecipe(ResourceLocation(MMetallurgy.MOD_ID, metal + "_hoe"), null, ItemStack(this),
                    *recipePatterns[3],
                    'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metal + "_ingot"),
                    '#', Items.STICK)
        }
    }

    class Pick(metal: String, material: Item.ToolMaterial) : ItemPickaxe(material) {
        init {
            initTool(this, metal, "pick")
            GameRegistry.addShapedRecipe(ResourceLocation(MMetallurgy.MOD_ID, metal + "_pick"), null, ItemStack(this),
                    *recipePatterns[0],
                    'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metal + "_ingot"),
                    '#', Items.STICK)
        }
    }

    class Shovel(metal: String, material: Item.ToolMaterial) : ItemSpade(material) {
        init {
            initTool(this, metal, "shovel")
            GameRegistry.addShapedRecipe(ResourceLocation(MMetallurgy.MOD_ID, metal + "_shovel"), null, ItemStack(this),
                    *recipePatterns[1],
                    'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metal + "_ingot"),
                    '#', Items.STICK)
        }
    }

    class Sword(metal: String, material: Item.ToolMaterial) : ItemSword(material) {
        init {
            initTool(this, metal, "sword")
            GameRegistry.addShapedRecipe(ResourceLocation(MMetallurgy.MOD_ID, metal + "_sword"), null, ItemStack(this),
                    *recipePatterns[4],
                    'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metal + "_ingot"),
                    '#', Items.STICK)
        }
    }

    private fun initTool(tool: Item, metalName: String, type: String) {
        tool.setRegistryName(metalName + "_" + type)
        tool.unlocalizedName = MMetallurgy.MOD_ID + "." + metalName + "." + type
    }
}
