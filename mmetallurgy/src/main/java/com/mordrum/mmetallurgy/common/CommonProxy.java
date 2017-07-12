package com.mordrum.mmetallurgy.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mordrum.mmetallurgy.MMetallurgy;
import com.mordrum.mmetallurgy.MetalRegistrationHandler;
import com.mordrum.mmetallurgy.MetalType;
import com.mordrum.mmetallurgy.blocks.BigBrickBlock;
import com.mordrum.mmetallurgy.blocks.OreBlock;
import com.mordrum.mmetallurgy.blocks.SmallBrickBlock;
import com.mordrum.mmetallurgy.blocks.SolidBlock;
import com.mordrum.mmetallurgy.common.blocks.VolatileTNT;
import com.mordrum.mmetallurgy.common.generation.OreGenerator;
import com.mordrum.mmetallurgy.common.generation.VeinGenerator;
import com.mordrum.mmetallurgy.items.Armor;
import com.mordrum.mmetallurgy.items.Ingot;
import com.mordrum.mmetallurgy.items.Tools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonProxy {
	private final List<String> metalCategories = Arrays.asList("base.json", "utility.json", "magic.json");
	private final Map<Ingot, JsonArray> alloyMap;

	public CommonProxy() {
		alloyMap = new HashMap<>();
	}

	public void preInit(FMLPreInitializationEvent event) {
		loadMetals();
		loadAlloys();

		// Register volatile TNT and add its crafting recipe
		VolatileTNT volatileTNT = new VolatileTNT();
		volatileTNT.register();
		Ingredient tntItemBlock = Ingredient.fromStacks(new ItemStack(Blocks.TNT));
		GameRegistry.addShapelessRecipe(new ResourceLocation(MMetallurgy.MOD_ID, "volatile_tnt"), null, new ItemStack(volatileTNT), tntItemBlock,
				tntItemBlock,
				tntItemBlock,
				tntItemBlock);

		// Special one-off gunpowder recipe
		GameRegistry.addShapelessRecipe(new ResourceLocation(MMetallurgy.MOD_ID, "volatile_tnt"), null, new ItemStack(Items.GUNPOWDER, 2),
				Ingredient.fromItem(Item.getByNameOrId(MMetallurgy.MOD_ID + ":sulfur_ingot")),
				Ingredient.fromItem(Item.getByNameOrId(MMetallurgy.MOD_ID + ":saltpeter_ingot"))
		);

		// World generators
		GameRegistry.registerWorldGenerator(new VeinGenerator(MMetallurgy.config.getConfigList("veins")), 1000);
		GameRegistry.registerWorldGenerator(OreGenerator.getInstance(), 1500);
		MinecraftForge.EVENT_BUS.register(new AchievementEventHandler());
	}

	public void init(FMLInitializationEvent event) {
		//FIXME
//		Achievements.registerAchievements();
	}

	private void loadMetals() {
		FurnaceRecipes furnaceRecipes = FurnaceRecipes.instance();

		// Read our metal configuration files
		metalCategories.stream()
				.map(MMetallurgy::getResourceAsJSON)
				.forEach((metalCategory) -> {
					for (JsonElement metalElement : metalCategory) {
						JsonObject metalConfiguration = metalElement.getAsJsonObject();
						MetalType type = MetalType.valueOf(metalConfiguration.get("type")
								.getAsString()
								.toUpperCase());
						// Process the metal name and clean it up
						String name = metalConfiguration.get("name")
								.getAsString()
								.trim()
								.replaceAll(" ", "_")
								.toLowerCase();

						Ingot ingot = new Ingot(name);
						ItemStack ingotStack = new ItemStack(ingot);

						// Metal is mineable
						if (type != MetalType.ALLOY) {
							OreBlock oreBlock = new OreBlock(name, metalConfiguration.get("blockLevel").getAsInt(), metalConfiguration.get("isToxic")
									.getAsBoolean());
							oreBlock.register();
							if (type == MetalType.DROP) {
								oreBlock.setDrop(ingot);
								oreBlock.setMaxQuantityDropped(4);
							} else {
								furnaceRecipes.addSmeltingRecipeForBlock(oreBlock, ingotStack, 1.0f);
							}
						} else {
							ingot.setAlloy(true);
							alloyMap.put(ingot, metalConfiguration.get("components").getAsJsonArray());
						}

						if (metalConfiguration.has("armorStrength")) {
							// Make armor
							int armorDurability = metalConfiguration.get("armorDurability").getAsInt();
							int armorStrength = metalConfiguration.get("armorStrength").getAsInt();
							ItemArmor.ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial(name,
									MMetallurgy.MOD_ID + ":" + name, armorDurability, new int[]{
											armorStrength,
											armorStrength,
											armorStrength,
											armorStrength
									}, metalConfiguration.get("enchantability")
											.getAsInt(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, metalConfiguration.get("armorToughness").getAsFloat());

							MetalRegistrationHandler.INSTANCE.getItemsToRegister().add(new Armor(name, armorMaterial, 1, EntityEquipmentSlot.FEET));
							MetalRegistrationHandler.INSTANCE.getItemsToRegister().add(new Armor(name, armorMaterial, 2, EntityEquipmentSlot.LEGS));
							MetalRegistrationHandler.INSTANCE.getItemsToRegister().add(new Armor(name, armorMaterial, 1, EntityEquipmentSlot.CHEST));
							MetalRegistrationHandler.INSTANCE.getItemsToRegister().add(new Armor(name, armorMaterial, 1, EntityEquipmentSlot.HEAD));
						}

						if (metalConfiguration.has("toolStrength")) {
							// Make items
							Item.ToolMaterial toolMaterial = EnumHelper.addToolMaterial(
									name,
									metalConfiguration.get("toolStrength").getAsInt(),
									metalConfiguration.get("toolDurability").getAsInt(),
									metalConfiguration.get("toolSpeed").getAsFloat(),
									metalConfiguration.get("toolDamage").getAsFloat(),
									metalConfiguration.get("enchantability").getAsInt()
							);
							MetalRegistrationHandler.INSTANCE.getItemsToRegister().add(new Tools.Axe(name, toolMaterial));
							MetalRegistrationHandler.INSTANCE.getItemsToRegister().add(new Tools.Hoe(name, toolMaterial));
							MetalRegistrationHandler.INSTANCE.getItemsToRegister().add(new Tools.Pick(name, toolMaterial));
							MetalRegistrationHandler.INSTANCE.getItemsToRegister().add(new Tools.Shovel(name, toolMaterial));
							MetalRegistrationHandler.INSTANCE.getItemsToRegister().add(new Tools.Sword(name, toolMaterial));
						}

						if (metalConfiguration.get("hasBlocks").getAsBoolean()) {
							Ingredient ingotIngredient = Ingredient.fromStacks(ingotStack);

							// Register blocks here
							SolidBlock solidBlock = new SolidBlock(name);
							solidBlock.register();
							GameRegistry.addShapelessRecipe(new ResourceLocation(MMetallurgy.MOD_ID, solidBlock.getName()), null, new ItemStack(Item.getItemFromBlock
									(solidBlock)), ingotIngredient, ingotIngredient, ingotIngredient, ingotIngredient, ingotIngredient, ingotIngredient, ingotIngredient, ingotIngredient, ingotIngredient);
							GameRegistry.addShapelessRecipe(new ResourceLocation(MMetallurgy.MOD_ID, solidBlock.getName() + "_breakdown"), null, new ItemStack
											(ingot, 9),
									Ingredient.fromItem
											(Item.getItemFromBlock(solidBlock)));
							BigBrickBlock bigBrickBlock = new BigBrickBlock(name);
							bigBrickBlock.register();
							GameRegistry.addShapelessRecipe(new ResourceLocation(MMetallurgy.MOD_ID, bigBrickBlock.getName()), null, new ItemStack(Item
									.getItemFromBlock
									(bigBrickBlock), 4), ingotIngredient, ingotIngredient, ingotIngredient, ingotIngredient);
							SmallBrickBlock smallBrickBlock = new SmallBrickBlock(name);
							smallBrickBlock.register();
							GameRegistry.addShapelessRecipe(new ResourceLocation(MMetallurgy.MOD_ID, smallBrickBlock.getName()), null, new ItemStack(Item
									.getItemFromBlock
									(smallBrickBlock), 3), ingotIngredient, ingotIngredient, ingotIngredient);
						}
					}
				});
	}

	private void loadAlloys() {
		for (Ingot ingot : alloyMap.keySet()) {
			JsonArray jsonElements = alloyMap.get(ingot);

			List<Ingredient> components = new ArrayList<>();
			//TODO support specifying the amount of each component required
			int i = 0;
			for (JsonElement jsonElement : jsonElements) {
				Item item = Item.getByNameOrId(jsonElement.getAsString());
				components.add(Ingredient.fromItem(item));
			}

			GameRegistry.addShapelessRecipe(new ResourceLocation(MMetallurgy.MOD_ID, ingot.getName()), null, new ItemStack(ingot, components.size()), components
					.toArray(new Ingredient[jsonElements.size()]));
		}
	}
}
