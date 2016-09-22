package com.mordrum.mclimate;

import com.mordrum.mclimate.common.biome.BiomeFrozenLake;
import com.mordrum.mclimate.common.biome.BiomeLake;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    public void onInit(FMLInitializationEvent event) {
        BiomeLake biomeLake = new BiomeLake();
        Biome.registerBiome(201, biomeLake.getRegistryName().toString(), biomeLake);

        BiomeFrozenLake biomeFrozenLake = new BiomeFrozenLake();
        Biome.registerBiome(202, biomeFrozenLake.getRegistryName().toString(), biomeFrozenLake);
    }
}
