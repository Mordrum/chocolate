package com.mordrum.mclimate.mixins;

import com.mordrum.mclimate.common.Season;
import net.minecraft.world.ColorizerGrass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ColorizerGrass.class)
public class ColorizerGrassMixin {
    @Shadow
    private static int[] grassBuffer;

    @Overwrite
    public static int getGrassColor(double temperature, double humidity) {
        humidity = humidity * temperature;
        int i = (int) ((1.0D - temperature) * 255.0D);
        int j = (int) ((1.0D - humidity) * 255.0D);
        int k = j << 8 | i;

        return Season.getCurrentSeason().getGrassColor(k > grassBuffer.length ? -65281 : grassBuffer[k]);
    }
}
