package com.mordrum.mclimate.common.biome;

import com.mordrum.mclimate.MClimate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRiver;

public class BiomeFrozenLake extends BiomeRiver {
	public BiomeFrozenLake() {
		super(new Biome.BiomeProperties("FrozenLake").setBaseHeight(-0.5F).setHeightVariation(0.0F).setTemperature(0.0F).setRainfall(0.5F).setSnowEnabled());
		this.spawnableCreatureList.clear();
		this.setRegistryName(new ResourceLocation(MClimate.MOD_ID, "frozen_lake"));
	}
}
