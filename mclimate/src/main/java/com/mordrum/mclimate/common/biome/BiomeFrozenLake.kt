package com.mordrum.mclimate.common.biome

import com.mordrum.mclimate.MClimate
import net.minecraft.util.ResourceLocation
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeRiver

class BiomeFrozenLake : BiomeRiver(Biome.BiomeProperties("FrozenLake").setBaseHeight(-0.5f).setHeightVariation(0.0f).setTemperature(0.0f).setRainfall(0.5f).setSnowEnabled()) {
    init {
        this.spawnableCreatureList.clear()
        this.registryName = ResourceLocation(MClimate.MOD_ID, "frozen_lake")
    }
}
