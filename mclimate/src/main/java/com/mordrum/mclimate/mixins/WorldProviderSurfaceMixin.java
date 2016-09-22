package com.mordrum.mclimate.mixins;

import com.mordrum.mclimate.common.Season;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WorldProviderSurface.class)
public abstract class WorldProviderSurfaceMixin extends WorldProvider {

    @Override
    public float calculateCelestialAngle(long time, float partial) {
        Season currentSeason = Season.getCurrentSeason();
        int dayDuration = 12000;
        int nightDuration = 12000;
        if (currentSeason == Season.SUMMER) {
            dayDuration = 18000;
            nightDuration = 6000;
        } else if (currentSeason == Season.WINTER) {
            dayDuration = 6000;
            nightDuration = 18000;
        }

        int absoluteTime = (int) (Math.max(1, time) % (dayDuration + nightDuration));
        boolean day = absoluteTime >= 0 && absoluteTime < dayDuration;
        int cycleTime = day ? (absoluteTime % dayDuration) : (absoluteTime % nightDuration);
        float value = 0.5F * ((float) cycleTime + partial) / (day ? (float) (dayDuration) : (float) (nightDuration));

        if (day) {
            value += 0.75F;
        } else {
            value += 0.25F;
        }

        return value;
    }
}
