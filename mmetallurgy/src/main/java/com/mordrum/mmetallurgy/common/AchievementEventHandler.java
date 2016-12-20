package com.mordrum.mmetallurgy.common;

import com.mordrum.mmetallurgy.items.Armor;
import com.mordrum.mmetallurgy.items.Ingot;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.JsonSerializableSet;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class AchievementEventHandler {
	@SubscribeEvent
	public void craftingEvent(PlayerEvent.ItemCraftedEvent event) {
		if (event.player.world.isRemote) return;

		Item item = event.crafting.getItem();
		if (item instanceof Ingot) {
			 Ingot ingot = (Ingot) item;
			 if (ingot.isAlloy()) {
				 event.player.addStat(Achievements.STRONGER_TOGETHER);
				 if (ingot.getUnlocalizedName().equalsIgnoreCase("item.mmetallurgy.damascus_steel.ingot")) {
					 event.player.addStat(Achievements.LOST_TO_THE_AGES);
				 }

				 StatisticsManagerServer statFile = ((EntityPlayerMP) event.player).getStatFile();
				 if (!statFile.hasAchievementUnlocked(Achievements.METAL_SMITH)) {
					 JsonSerializableSet jsonserializableset = statFile.getProgress(Achievements.METAL_SMITH);
					 if (jsonserializableset == null) {
						 jsonserializableset = statFile.setProgress(Achievements.METAL_SMITH, new JsonSerializableSet());
					 }
					 jsonserializableset.add(ingot.getName());
					 if (jsonserializableset.size() >= 4) {
						 event.player.addStat(Achievements.METAL_SMITH);
					 }
				 }
			 }
		} else if (item instanceof Armor) {
			event.player.addStat(Achievements.NEW_STYLE);
		} else if (item.equals(Items.GUNPOWDER)) {
			event.player.addStat(Achievements.VOLATILE_RECIPE);
		}
	}

	@SubscribeEvent
	public void furnaceEvent(PlayerEvent.ItemSmeltedEvent event) {
		if (event.player.world.isRemote) return;

		if (event.smelting.getUnlocalizedName().equalsIgnoreCase("item.mmetallurgy.grifonium.ingot")) {
			event.player.addStat(Achievements.ADAM);
		} else if (event.smelting.getUnlocalizedName().equalsIgnoreCase("item.mmetallurgy.calitonium.ingot")) {
			event.player.addStat(Achievements.EVE);
		}
	}
}
