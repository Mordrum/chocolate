package com.mordrum.mmetallurgy.items;

import com.mordrum.mmetallurgy.MMetallurgy;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Tools {
	private static final String[][] recipePatterns = new String[][] {{"XXX", " # ", " # "}, {"X", "#", "#"}, {"XX", "X#", " #"}, {"XX", " #", " #"}, {" X", " X", " #"}};

	public static class Axe extends ItemAxe {
		public Axe(String metal, ToolMaterial material) {
			super(material, material.getDamageVsEntity(), material.getEfficiencyOnProperMaterial());
			initTool(this, metal, "axe");
			CraftingManager.getInstance().addRecipe(new ItemStack(this), recipePatterns[2], 'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metal + "_ingot"), '#', Items.STICK);
		}
	}

	public static class Hoe extends ItemHoe {
		public Hoe(String metal, ToolMaterial material) {
			super(material);
			initTool(this, metal, "hoe");
			CraftingManager.getInstance().addRecipe(new ItemStack(this), recipePatterns[3], 'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metal + "_ingot"), '#', Items.STICK);
		}
	}

	public static class Pick extends ItemPickaxe {
		public Pick(String metal, ToolMaterial material) {
			super(material);
			initTool(this, metal, "pick");
			CraftingManager.getInstance().addRecipe(new ItemStack(this), recipePatterns[0], 'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metal + "_ingot"), '#', Items.STICK);
		}
	}

	public static class Shovel extends ItemSpade {
		public Shovel(String metal, ToolMaterial material) {
			super(material);
			initTool(this, metal, "shovel");
			CraftingManager.getInstance().addRecipe(new ItemStack(this), recipePatterns[1], 'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metal + "_ingot"), '#', Items.STICK);
		}
	}

	public static class Sword extends ItemSword {
		public Sword(String metal, ToolMaterial material) {
			super(material);
			initTool(this, metal, "sword");
			CraftingManager.getInstance().addRecipe(new ItemStack(this), recipePatterns[4], 'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metal + "_ingot"), '#', Items.STICK);
		}
	}

	private static void initTool(Item tool, String metalName, String type) {
		tool.setRegistryName(metalName + "_" + type);
		tool.setUnlocalizedName(MMetallurgy.MOD_ID + "." + metalName + "." + type);
		GameRegistry.register(tool);
	}
}
