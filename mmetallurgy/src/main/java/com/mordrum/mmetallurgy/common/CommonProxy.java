package com.mordrum.mmetallurgy.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mordrum.mmetallurgy.MMetallurgy;
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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonProxy {
	private final List<String> metalCategories = Arrays.asList("base.json", "utility.json", "magic.json");
	protected final List<Armor> armors;
	protected final List<Item> items;
	private final Map<Ingot, JsonArray> alloyMap;

	public CommonProxy() {
		armors = new ArrayList<>();
		items = new ArrayList<>();
		alloyMap = new HashMap<>();
	}

	public void preInit(FMLPreInitializationEvent event) {
		CraftingManager cm = CraftingManager.getInstance();

		loadMetals();
		loadAlloys();

		// Register volatile TNT and add its crafting recipe
		VolatileTNT volatileTNT = new VolatileTNT();
		volatileTNT.register();
		ItemStack tntItemBlock = new ItemStack(Blocks.TNT);
		cm.addShapelessRecipe(new ItemStack(volatileTNT), tntItemBlock, tntItemBlock, tntItemBlock, tntItemBlock);

		// Special one-off gunpowder recipe
		cm.addShapelessRecipe(new ItemStack(Items.GUNPOWDER, 2), Item.getByNameOrId(MMetallurgy.MOD_ID + ":sulfur_ingot"), Item.getByNameOrId(MMetallurgy.MOD_ID + ":saltpeter_ingot"));

		// World generators
		GameRegistry.registerWorldGenerator(new VeinGenerator(MMetallurgy.config.getConfigList("veins")), 1000);
		GameRegistry.registerWorldGenerator(OreGenerator.getInstance(), 1500);
		MinecraftForge.EVENT_BUS.register(new AchievementEventHandler());
	}

	public void init(FMLInitializationEvent event) {
		Achievements.registerAchievements();
	}

	private void loadMetals() {
		CraftingManager craftingManager = CraftingManager.getInstance();
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
							OreBlock oreBlock = new OreBlock(name, metalConfiguration.get("blockLevel").getAsInt(), metalConfiguration.get("isToxic").getAsBoolean());
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
									}, metalConfiguration.get("enchantability").getAsInt(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, metalConfiguration.get("armorToughness").getAsFloat());

							armors.add(new Armor(name, armorMaterial, 1, EntityEquipmentSlot.FEET));
							armors.add(new Armor(name, armorMaterial, 2, EntityEquipmentSlot.LEGS));
							armors.add(new Armor(name, armorMaterial, 1, EntityEquipmentSlot.CHEST));
							armors.add(new Armor(name, armorMaterial, 1, EntityEquipmentSlot.HEAD));
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
							items.add(new Tools.Axe(name, toolMaterial));
							items.add(new Tools.Hoe(name, toolMaterial));
							items.add(new Tools.Pick(name, toolMaterial));
							items.add(new Tools.Shovel(name, toolMaterial));
							items.add(new Tools.Sword(name, toolMaterial));
						}

						if (metalConfiguration.get("hasBlocks").getAsBoolean()) {
							// Register blocks here
							SolidBlock solidBlock = new SolidBlock(name);
							solidBlock.register();
							craftingManager.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(solidBlock)), ingotStack, ingotStack, ingotStack, ingotStack, ingotStack, ingotStack, ingotStack, ingotStack, ingotStack);
							craftingManager.addShapelessRecipe(new ItemStack(ingot, 9), Item.getItemFromBlock(solidBlock));
							BigBrickBlock bigBrickBlock = new BigBrickBlock(name);
							bigBrickBlock.register();
							craftingManager.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(bigBrickBlock), 4), ingotStack, ingotStack, ingotStack, ingotStack);
							SmallBrickBlock smallBrickBlock = new SmallBrickBlock(name);
							smallBrickBlock.register();
							craftingManager.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(smallBrickBlock), 3), ingotStack, ingotStack, ingotStack);
						}
					}
				});
	}

	private void loadAlloys() {
		for (Ingot ingot : alloyMap.keySet()) {
			JsonArray jsonElements = alloyMap.get(ingot);
			List<ItemStack> components = new ArrayList<>();
			//TODO support specifying the amount of each component required
			for (JsonElement jsonElement : jsonElements) {
				Item item = Item.getByNameOrId(jsonElement.getAsString());
				components.add(new ItemStack(item));
			}
			ShapelessRecipes recipe = new ShapelessRecipes(new ItemStack(ingot, components.size()), components);
			CraftingManager.getInstance().addRecipe(recipe);
		}
	}
}
