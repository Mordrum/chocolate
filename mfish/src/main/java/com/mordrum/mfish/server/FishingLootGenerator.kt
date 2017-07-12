package com.mordrum.mfish.server

import com.mordrum.mfish.common.CommonProxy
import com.mordrum.mfish.common.Fish
import com.mordrum.mfish.common.events.FishCaughtEvent
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityFishHook
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.MinecraftForge
import org.apache.commons.math3.distribution.FDistribution
import java.math.BigDecimal
import java.math.MathContext
import java.text.DecimalFormat
import java.util.*
import java.util.function.Consumer

object FishingLootGenerator {
    private val sb = StringBuilder()
    private val decimalFormat = DecimalFormat("#,###.##")

    fun getFishingLoot(player: EntityPlayer, fishHook: Entity): ItemStack {
        // Get the fish available in the biome where the player is fishing
        val fishForBiome = Fish.getFishForBiome(fishHook.entityWorld.getBiome(fishHook.position))

        // Add up the rarity of all the fish, this will tell us the maximum our random number can be
        val rollMax = fishForBiome.map { it.rarity }.sum()
        // Create a random number generator, then roll a number to determine what fish will be selected
        val roll = Random().nextInt(rollMax)

        // Go through the list of fish and pull out the fish that was selected
        var i = 0
        var selectedFish: Fish? = null
        for (fish in fishForBiome) {
            i += fish.rarity
            if (roll <= i) {
                selectedFish = fish
                break
            }
        }
        // If no fish was selected (somehow) (usually the result of a bad config file), return cobblestone
        if (selectedFish == null) return ItemStack(Blocks.COBBLESTONE)

        // Get a random weight for the fish and determine its sized based prefix
        val weight: Double = rollFishWeight()
        val prefix: String
        if (weight > 150) prefix = "Massive"
        else if (weight > 75) prefix = "Huge"
        else if (weight > 30) prefix = "Large"
        else if (weight > 1) prefix = ""
        else prefix = "Baby"

        // Create an NBT tag compound so the fish itemstack will keep track of its data
        val tagCompound = NBTTagCompound()
        tagCompound.setDouble("weight", weight)
        tagCompound.setString("prefix", prefix)

        val itemStack = ItemStack(CommonProxy.rawItem, 1, selectedFish.metadata)
        itemStack.tagCompound = tagCompound

        // Post a FishCaughtEvent to the eventbus so things can interact with this method
        MinecraftForge.EVENT_BUS.post(FishCaughtEvent(player, fishHook as EntityFishHook, selectedFish, weight))

        //TODO migrate these methods out, perhaps into a listener class that listens for FishCaughtEvent
        APIHelper.checkHighscore(player, selectedFish, weight, Consumer { isHighscore ->
            if (isHighscore) {
                sb.setLength(0)
                sb.append(player.name)
                        .append(" just caught a record breaking ")
                        .append(decimalFormat.format(weight))
                        .append("lb ")
                        .append(selectedFish!!.name)
                        .append("!")
                val component = TextComponentString(sb.toString())
                for (entityPlayerMP in player.server!!.playerList.players) {
                    entityPlayerMP.sendMessage(component)
                }
            } else {
                player.sendMessage(TextComponentString("You caught a " + decimalFormat.format(weight) + "lb " + selectedFish!!.name))
            }
        }, Consumer { error ->
            player.sendMessage(TextComponentString("An error occurred while sending your fish to the API server: " + error.message))
        })
        //FIXME advancements
        //checkAchievements(player, selectedFish, weight)

        return itemStack
    }

    private fun rollFishWeight(): Double {
        val fDistribution = FDistribution(0.89, 100.0)
        var weight = fDistribution.sample() * 10

        // If the fish is really tiny, turn it into a baby fish that weighs between 0.5 and 1.0 lbs
        if (weight < 1) weight = Math.random() / 2 + 0.5
        // Trim off excess decimal places
        return BigDecimal(weight).round(MathContext(3)).toDouble()
    }

    /*FIXME advancements
    private fun checkAchievements(player: EntityPlayer, fish: Fish, weight: Double) {
        val statFile = (player as EntityPlayerMP).statFile
        if (!statFile.hasAchievementUnlocked(Achievements.FISHOLOGIST)) {
            var jsonserializableset: JsonSerializableSet? = statFile.getProgress<JsonSerializableSet>(Achievements.FISHOLOGIST)
            if (jsonserializableset == null) {
                jsonserializableset = statFile.setProgress(Achievements.FISHOLOGIST, JsonSerializableSet())
            }
            jsonserializableset!!.add(fish.name)
            if (jsonserializableset.size >= 30) {
                player.addStat(Achievements.FISHOLOGIST)
            } else if (jsonserializableset.size >= 10) {
                player.addStat(Achievements.ANGLER)
            } else if (jsonserializableset.size >= 3) {
                player.addStat(Achievements.FISHING_101)
            }
        }

        // Big catch achievement
        if (weight >= 100.0) {
            player.addStat(Achievements.THE_BIG_ONE)
        } else if (weight >= 30.0) {
            player.addStat(Achievements.BIG_CATCH)
        } else if (weight <= 1.0) {
            player.addStat(Achievements.LITTLE_GUPPY)
        }

        // Keep track of the number of pounds this player has caught so far
        val entityData = player.getEntityData()
        var totalWeightCaughtSoFar = 0.0
        if (entityData.hasKey("totalweight")) totalWeightCaughtSoFar = entityData.getDouble("totalweight")
        totalWeightCaughtSoFar += weight
        entityData.setDouble("totalweight", totalWeightCaughtSoFar)
        if (totalWeightCaughtSoFar >= 100) {
            player.addStat(Achievements.TO_FEED_A_FAMILY, 1)
        } else if (totalWeightCaughtSoFar >= 1000) {
            player.addStat(Achievements.TO_FEED_A_VILLAGE, 1)
        } else if (totalWeightCaughtSoFar >= 10000) {
            player.addStat(Achievements.TO_FEED_AN_ARMY, 1)
        }
    }
    */
}
