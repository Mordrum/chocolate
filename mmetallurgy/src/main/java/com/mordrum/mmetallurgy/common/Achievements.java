package com.mordrum.mmetallurgy.common;

import com.mordrum.mmetallurgy.MMetallurgy;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import java.util.List;

import static com.mordrum.mmetallurgy.MMetallurgy.MOD_ID;

public class Achievements {
	public static final Achievement STRONGER_TOGETHER = new Achievement(MOD_ID + ".strongerTogether", MOD_ID + ".strongerTogether", 0, 0, Item
			.getByNameOrId(MOD_ID + ":bronze_ingot"), null).registerStat();
	public static final Achievement METAL_SMITH = new Achievement(MOD_ID + ".metalSmith", MOD_ID + ".metalSmith", 1, 2, Item
			.getByNameOrId(MOD_ID + ":hepatizon_ingot"), STRONGER_TOGETHER).registerStat();
	public static final Achievement LOST_TO_THE_AGES = new Achievement(MOD_ID + ".lostToTheAges", MOD_ID + ".lostToTheAges", -1, 2, Item
			.getByNameOrId(MOD_ID + ":damascus_steel_ingot"), STRONGER_TOGETHER).setSpecial().registerStat();

	public static final Achievement ADAM = new Achievement(MOD_ID + ".adam", MOD_ID + ".adam", -2, 4, Item
			.getByNameOrId(MOD_ID + ":grifonium_ingot"), LOST_TO_THE_AGES).registerStat();
	public static final Achievement EVE = new Achievement(MOD_ID + ".eve", MOD_ID + ".eve", 0, 4, Item
			.getByNameOrId(MOD_ID + ":calitonium_ingot"), LOST_TO_THE_AGES).registerStat();

	public static final Achievement NEW_STYLE = new Achievement(MOD_ID + ".newStyle", MOD_ID + ".newStyle", 2, 0, Item
			.getByNameOrId(MOD_ID + ":copper_chest"), null).registerStat();

	public static final Achievement VOLATILE_RECIPE = new Achievement(MOD_ID + ".volatileRecipe", MOD_ID + ".volatileRecipe", 4, 0, Items.GUNPOWDER, null).registerStat();

	static void registerAchievements() {
		AchievementPage page = AchievementPage.getAchievementPage("Mining");
		if (page == null) {
			page = new AchievementPage("Mining");
			AchievementPage.registerAchievementPage(page);
		}
		List<Achievement> achievements = page.getAchievements();
		achievements.add(STRONGER_TOGETHER);
		achievements.add(METAL_SMITH);
		achievements.add(STRONGER_TOGETHER);
		achievements.add(LOST_TO_THE_AGES);
		achievements.add(ADAM);
		achievements.add(EVE);
		achievements.add(NEW_STYLE);
		achievements.add(VOLATILE_RECIPE);
	}
}
