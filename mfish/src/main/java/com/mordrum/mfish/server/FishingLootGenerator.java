package com.mordrum.mfish.server;

import com.mordrum.mfish.CommonProxy;
import com.mordrum.mfish.common.Fish;
import com.mordrum.mfish.common.events.FishCaughtEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.server.FMLServerHandler;
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
        int rollMax = fishForBiome.stream().mapToInt(Fish::getRarity).sum();

        Random random = new Random();
        int roll = random.nextInt(rollMax);

        FDistribution fDistribution = new FDistribution(0.89, 100);
        int i = 0;
        for (Fish fish : fishForBiome) {
            i += fish.getRarity();
            if (roll <= i) {
                double weight = fDistribution.sample() * 10;
                String prefix = null;
                if (weight < 1) {
                    weight = 1 + Math.random();
                    prefix = "Tiny";
                } else if (weight > 30) {
                    prefix = "Large";
                } else if (weight > 75) {
                    prefix = "Huge";
                } else if (weight > 150) {
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
