package com.mordrum.mmetallurgy.items;

import com.mordrum.mmetallurgy.MMetallurgy;
import net.malisis.core.util.replacement.ShapedRecipesHandler;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Armor extends ItemArmor {
	private final String[][] recipePatterns = new String[][] {{"XXX", "X X"}, {"X X", "XXX", "XXX"}, {"XXX", "X X", "X X"}, {"X X", "X X"}};

	public Armor(String metalName, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
		String slotName = equipmentSlotIn.getName().toLowerCase();
		this.setRegistryName(metalName + "_" + slotName);
		this.setUnlocalizedName(MMetallurgy.MOD_ID + "." + metalName + "." + slotName);

		String[] pattern = new String[0];
		switch (equipmentSlotIn) {
			case FEET:
				pattern = recipePatterns[3];
				break;
			case LEGS:
				pattern = recipePatterns[2];
				break;
			case CHEST:
				pattern = recipePatterns[1];
				break;
			case HEAD:
				pattern = recipePatterns[0];
				break;
		}

		//FIXME FUCKING LEX BREAKING SHAPED RECIPES GOD FUCKING DAMMIT
//		CraftingManager.getInstance().addRecipe(new ItemStack(this), pattern, 'X', Item.getByNameOrId(MMetallurgy.MOD_ID + ":" + metalName + "_ingot"));
	}
}
