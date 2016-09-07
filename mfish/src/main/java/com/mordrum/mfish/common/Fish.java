package com.mordrum.mfish.common;

import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;

import java.util.*;

public class Fish {
    private final static Map<Integer, Fish> fishes = new HashMap<>();
    private final static Map<EnvironmentType, Set<Fish>> environmentMap = new HashMap<>();

    private final String name;
    private final EnvironmentType environmentType;
    private final int rarity;
    private final int metadata;

    public Fish(String name, EnvironmentType environmentType, int rarity, int metadata) {
        this.name = name;
        this.environmentType = environmentType;
        this.rarity = rarity;
        this.metadata = metadata;
        fishes.put(metadata, this);
        if (!environmentMap.containsKey(environmentType)) environmentMap.put(environmentType, new HashSet<>());
        environmentMap.get(environmentType).add(this);
    }

    public static Collection<Fish> getFishes() {
        return fishes.values();
    }

    public String getName() {
        return name;
    }

    public EnvironmentType getEnvironmentType() {
        return environmentType;
    }

    public int getRarity() {
        return rarity;
    }

    public int getMetadata() {
        return metadata;
    }

    public static Fish byItemStack(ItemStack stack) {
        return fishes.get(stack.getMetadata());
    }

    public static Set<Fish> getFishForBiome(Biome biome) {
        Set<Fish> biomeFish = null;
        float temperature = biome.getTemperature();

        if (biome.getBiomeName().toLowerCase().contains("ocean")) biomeFish = environmentMap.get(EnvironmentType.SALT_WATER);
        else if (temperature <= 0.1) biomeFish = environmentMap.get(EnvironmentType.FRIGID);
        else if (temperature <= 0.5) biomeFish = environmentMap.get(EnvironmentType.COLD);
        else if (temperature <= 0.9) biomeFish = environmentMap.get(EnvironmentType.WARM);
        else if (temperature <= 1.2) biomeFish = environmentMap.get(EnvironmentType.TROPICAL);
        else if (temperature <= 3.0) biomeFish = environmentMap.get(EnvironmentType.ARID);

        if (biomeFish == null) biomeFish = new HashSet<>();

        return biomeFish;
    }

    public enum EnvironmentType {
        FRIGID, COLD, WARM, ARID, TROPICAL, SALT_WATER
    }
}
