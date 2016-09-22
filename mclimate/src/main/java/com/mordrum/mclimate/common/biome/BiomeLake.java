package com.mordrum.mclimate.common.biome;

import com.mordrum.mclimate.MClimate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRiver;

public class BiomeLake extends BiomeRiver {
	public BiomeLake() {
		super(new Biome.BiomeProperties("Lake").setBaseHeight(-0.5F).setHeightVariation(0.0F));
		this.spawnableCreatureList.clear();
		this.setRegistryName(new ResourceLocation(MClimate.MOD_ID, "lake"));
	}
}
