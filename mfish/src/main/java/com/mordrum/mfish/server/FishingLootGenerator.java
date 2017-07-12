package com.mordrum.mfish.server;

import com.mordrum.mfish.common.CommonProxy;
import com.mordrum.mfish.common.Fish;
import com.mordrum.mfish.common.events.FishCaughtEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.math3.distribution.FDistribution;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public class FishingLootGenerator {
    private static FDistribution fDistribution = new FDistribution(1, 9.8);

    public static ItemStack getFishingLoot(EntityPlayer player, Entity fishHook) {
        Biome biome = fishHook.getEntityWorld().getBiome(fishHook.getPosition());

        Set<Fish> fishForBiome = Fish.getFishForBiome(biome);

		if (fishForBiome.isEmpty()) {
			return new ItemStack(Blocks.COBBLESTONE);
		}

        int rollMax = fishForBiome.stream().mapToInt(Fish::getRarity).sum();

        Random random = new Random();
        int roll = random.nextInt(rollMax);

        FDistribution fDistribution = new FDistribution(0.89, 100);
        int i = 0;
        for (Fish fish : fishForBiome) {
            i += fish.getRarity();
            if (roll <= i) {
                double originalWeight = fDistribution.sample() * 10;
                double weight = originalWeight;
                String prefix = null;
                if (originalWeight < 1) {
                    weight = 1 + Math.random();
                    prefix = "Tiny";
                } else if (originalWeight > 30) {
                    prefix = "Large";
                } else if (originalWeight > 75) {
                    prefix = "Huge";
                } else if (originalWeight > 150) {
                    prefix = "Massive";
                }
                weight = new BigDecimal(weight).round(new MathContext(3)).doubleValue();
                ItemStack itemStack = new ItemStack(CommonProxy.rawItem, 1, fish.getMetadata());
                if (!itemStack.hasTagCompound()) itemStack.setTagCompound(new NBTTagCompound());
                itemStack.getTagCompound().setDouble("Weight", weight);
                if (prefix != null) itemStack.getTagCompound().setString("Prefix", prefix);

                //TODO decide if this is the best place to call these
                MinecraftForge.EVENT_BUS.post(new FishCaughtEvent(player, (EntityFishHook) fishHook, fish, weight));
                HighscoreManager.checkHighscore(player, fish, weight);


                /*FIXME advancements
	            StatisticsManagerServer statFile = ((EntityPlayerMP) player).getStatFile();
	            if (!statFile.hasAchievementUnlocked(Achievements.FISHOLOGIST)) {
		            JsonSerializableSet jsonserializableset = statFile.getProgress(Achievements.FISHOLOGIST);
					if (jsonserializableset == null) {
						jsonserializableset = statFile.setProgress(Achievements.FISHOLOGIST, new JsonSerializableSet());
					}
					jsonserializableset.add(fish.getName());
					if (jsonserializableset.size() >= 30) {
						player.addStat(Achievements.FISHOLOGIST);
					} else if (jsonserializableset.size() >= 10) {
						player.addStat(Achievements.ANGLER);
					} else if (jsonserializableset.size() >= 3) {
						player.addStat(Achievements.FISHING_101);
					}
	            }
	            */

	            /*FIXME advancements
	            // Big catch achievement
	            if (weight >= 100.0) {
					player.addStat(Achievements.THE_BIG_ONE);
	            } else if (weight >= 30.0) {
	            	player.addStat(Achievements.BIG_CATCH);
	            } else if (originalWeight <= 1.0) {
		            player.addStat(Achievements.LITTLE_GUPPY);
	            }
	            */

	            // Keep track of the number of pounds this player has caught so far
	            NBTTagCompound entityData = player.getEntityData();
	            double totalWeightCaughtSoFar = 0.0;
	            if (entityData.hasKey("totalweight")) totalWeightCaughtSoFar = entityData.getDouble("totalweight");
				totalWeightCaughtSoFar += weight;
				entityData.setDouble("totalweight", totalWeightCaughtSoFar);

				/*FIXME advancements
                if (totalWeightCaughtSoFar >= 100) {
                    player.addStat(Achievements.TO_FEED_A_FAMILY, 1);
                } else if (totalWeightCaughtSoFar >= 1000) {
	                player.addStat(Achievements.TO_FEED_A_VILLAGE, 1);
                } else if (totalWeightCaughtSoFar >= 10000) {
	                player.addStat(Achievements.TO_FEED_AN_ARMY, 1);
                }
                */

	            return itemStack;
            }
        }

        return new ItemStack(Blocks.COBBLESTONE, 1);
    }

    public static void main(String[] args) {
        fDistribution = new FDistribution(0.89, 100);
        calculateForSample(fDistribution.sample(100000));
    }

    private static void calculateForSample(double[] sample) {
        System.out.println("NDGF: " + fDistribution.getNumeratorDegreesOfFreedom());

        double avg = 0;
        for (int i = 0; i < sample.length; i++) {
            double v = sample[i] * 10;
            if (v < 1) v = 1 + Math.random();
            if (v > 250) v = 250;
            sample[i] = v;

            avg += v;
        }

        Arrays.sort(sample);
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("Random: " + df.format(sample[new Random().nextInt(sample.length)]));
        System.out.println("Smallest " + df.format(sample[0]));
        System.out.println("Largest " + df.format(sample[sample.length - 1]));
        System.out.println("Median: " + df.format(sample[sample.length / 2]));
        System.out.println("Mean: " + df.format((avg / sample.length)));
    }
}
