package com.mordrum.mclimate.common;

import java.awt.*;
import java.util.GregorianCalendar;

//TODO hook into BlockColors and inject Biomes O' Plenty leaves
public enum Season {
    SPRING(0x000000, 0.0, 0x000000, 0.0), SUMMER(0x00FF00, 0.3, 0x00FF00, 0.3), AUTUMN(0x000000, 0.0, 0xFF6A00, 0.7), WINTER(0x999988, 0.9, 0x999988, 1.0);

    private final Color foliageColor;
    private final double foliageMultiplier;
    private final Color grassColor;
    private final double grassMultiplier;

    Season(int foliageColor, double foliageMultiplier, int grassColor, double grassMultiplier) {
        this.foliageColor = new Color(foliageColor);
        this.foliageMultiplier = foliageMultiplier;
        this.grassColor = new Color(grassColor);
        this.grassMultiplier = grassMultiplier;
    }

    public static Season getCurrentSeason() {
        return getSeasonForTimestamp(System.currentTimeMillis());
    }

    public static Season getSeasonForCalendar(GregorianCalendar calendar) {
        return getSeasonForTimestamp(calendar.getTimeInMillis());
    }

    private static Season getSeasonForTimestamp(long timestamp) {
        // Convert to seconds
        timestamp /= 1000;
        // -4 hour offset
        timestamp -= 4 * 60 * 60;

        int floor = (int) Math.floor(timestamp / 86400);
        int season = floor % 4;
        return Season.values()[season];
    }

    public int getFoliageColor(int originalColor) {
        return mixColors(new Color(originalColor), this.foliageColor, this.foliageMultiplier);
    }

    public int getGrassColor(int originalColor) {
        return mixColors(new Color(originalColor), this.grassColor, this.grassMultiplier);
    }

    private int mixColors(Color baseColor, Color colorToMix, double multiplier) {
        int red = (int) (baseColor.getRed() * (1F - multiplier) + colorToMix.getRed() * multiplier);
        int green = (int) (baseColor.getGreen() * (1F - multiplier) + colorToMix.getGreen() * multiplier);
        int blue = (int) (baseColor.getBlue() * (1F - multiplier) + colorToMix.getBlue() * multiplier);

        return new Color(red, green, blue).getRGB();
    }

    public float getModifiedTemperature(float temperature) {
        if (this == SUMMER) return temperature * 2;
        else if (this == WINTER) {
            if (temperature <= 0.8f) return 0.1f;
            else return temperature / 2;
        } else return temperature;
    }
}
