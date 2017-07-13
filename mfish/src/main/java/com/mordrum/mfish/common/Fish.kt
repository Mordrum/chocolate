package com.mordrum.mfish.common

import net.minecraft.item.ItemStack
import net.minecraft.world.biome.Biome

import java.util.*

class Fish(val name: String, environmentType: EnvironmentType, val rarity: Int, val metadata: Int) {

    init {
        fishes.put(metadata, this)
        if (!environmentMap.containsKey(environmentType)) environmentMap.put(environmentType, HashSet<Fish>())
        environmentMap[environmentType]!!.add(this)
    }

    enum class EnvironmentType {
        FRIGID, COLD, WARM, ARID, TROPICAL, SALT_WATER
    }

    companion object {
        private val fishes = HashMap<Int, Fish>()
        private val environmentMap = HashMap<EnvironmentType, HashSet<Fish>>()

        fun getFishes(): Collection<Fish> {
            return fishes.values
        }

        fun byItemStack(stack: ItemStack): Fish {
            return fishes[stack.metadata]!!
        }

        fun getFishForBiome(biome: Biome): Set<Fish> {
            var biomeFish: Set<Fish>? = null
            val temperature = biome.temperature

            if (biome.biomeName.toLowerCase().contains("ocean"))
                biomeFish = environmentMap[EnvironmentType.SALT_WATER]
            else if (temperature <= 0.1)
                biomeFish = environmentMap[EnvironmentType.FRIGID]
            else if (temperature <= 0.5)
                biomeFish = environmentMap[EnvironmentType.COLD]
            else if (temperature <= 0.9)
                biomeFish = environmentMap[EnvironmentType.WARM]
            else if (temperature <= 1.2)
                biomeFish = environmentMap[EnvironmentType.TROPICAL]
            else if (temperature <= 3.0) biomeFish = environmentMap[EnvironmentType.ARID]

            if (biomeFish == null) biomeFish = HashSet<Fish>()

            return biomeFish
        }
    }
}
