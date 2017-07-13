package com.mordrum.mfish.common

/*FIXME advancements
import net.minecraft.init.Items;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import java.util.List;

import static com.mordrum.mfish.MFish.MOD_ID;


public class Achievements {
	// related to the weight of a caught fish
	public static final Achievement LITTLE_GUPPY = new Achievement(MOD_ID + ".littleGuppy", MOD_ID + ".littleGuppy", 0, 2, Items.FISH, null)
			.initIndependentStat()
			.registerStat();
	public static final Achievement BIG_CATCH = new Achievement(MOD_ID + ".bigCatch", MOD_ID + ".bigCatch", 0, 0, Items.FISH, LITTLE_GUPPY)
			.initIndependentStat()
			.registerStat();
	public static final Achievement THE_BIG_ONE = new Achievement(MOD_ID + ".theBigOne", MOD_ID + ".theBigOne", 0, 4, Items.FISH, BIG_CATCH)
			.setSpecial()
			.initIndependentStat()
			.registerStat();

	// related to the combined weight of all caught fish
	public static final Achievement TO_FEED_A_FAMILY = new Achievement(MOD_ID + ".toFeedAFamily", MOD_ID + ".toFeedAFamily", 2, 0, Items.FISH, null)
			.initIndependentStat()
			.registerStat();
	public static final Achievement TO_FEED_A_VILLAGE = new Achievement(MOD_ID + ".toFeedAVillage", MOD_ID + ".toFeedAVillage", 2, 2, Items.FISH, TO_FEED_A_FAMILY)
			.initIndependentStat()
			.registerStat();
	public static final Achievement TO_FEED_AN_ARMY = new Achievement(MOD_ID + ".toFeedAnArmy", MOD_ID + ".toFeedAnArmy", 2, 4, Items.FISH, TO_FEED_A_VILLAGE)
			.setSpecial()
			.initIndependentStat()
			.registerStat();

	// Achievements related to the number of unique types fish caught
	public static final Achievement FISHING_101 = new Achievement(MOD_ID + ".fishing101", MOD_ID + ".fishing101", 4, 0, Items.FISH, null)
			.initIndependentStat()
			.registerStat();
	public static final Achievement ANGLER = new Achievement(MOD_ID + ".angler", MOD_ID + ".angler", 4, 2, Items.FISH, FISHING_101)
			.initIndependentStat()
			.registerStat();
	public static final Achievement FISHOLOGIST = new Achievement(MOD_ID + ".fishologist", MOD_ID + ".fishologist", 4, 4, Items.FISH, ANGLER)
			.setSpecial()
			.initIndependentStat()
			.registerStat();


	static void registerAchievements() {
		AchievementPage page = AchievementPage.getAchievementPage("Fishing");
		if (page == null) {
			page = new AchievementPage("Fishing");
			AchievementPage.registerAchievementPage(page);
		}
		List<Achievement> achievements = page.getAchievements();
		achievements.add(BIG_CATCH);
		achievements.add(LITTLE_GUPPY);
		achievements.add(THE_BIG_ONE);
		achievements.add(TO_FEED_A_FAMILY);
		achievements.add(TO_FEED_A_VILLAGE);
		achievements.add(TO_FEED_AN_ARMY);
		achievements.add(FISHING_101);
		achievements.add(ANGLER);
		achievements.add(FISHOLOGIST);
	}
}
*/