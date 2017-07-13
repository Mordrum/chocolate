package com.mordrum.mclimate.common.biome

import com.mordrum.mclimate.MClimate
import net.minecraft.util.ResourceLocation
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeRiver

class BiomeLake : BiomeRiver(Biome.BiomeProperties("Lake").setBaseHeight(-0.5f).setHeightVariation(0.0f)) {
    init {
        this.spawnableCreatureList.clear()
        this.registryName = ResourceLocation(MClimate.MOD_ID, "lake")
    }
}
