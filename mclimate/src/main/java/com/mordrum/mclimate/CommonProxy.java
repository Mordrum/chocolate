package com.mordrum.mclimate;

import com.mordrum.mclimate.common.biome.BiomeFrozenLake;
import com.mordrum.mclimate.common.biome.BiomeLake;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = MClimate.MOD_ID)
public class CommonProxy {
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    public void onInit(FMLInitializationEvent event) {
    }


    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        BiomeLake biomeLake = new BiomeLake();
        BiomeFrozenLake biomeFrozenLake = new BiomeFrozenLake();
        event.getRegistry().registerAll(biomeLake, biomeFrozenLake);
    }
}
