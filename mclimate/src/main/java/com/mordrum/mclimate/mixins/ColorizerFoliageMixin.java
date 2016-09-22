package com.mordrum.mclimate.mixins;

import com.mordrum.mclimate.common.Season;
import net.minecraft.world.ColorizerFoliage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

@Mixin(ColorizerFoliage.class)
public class ColorizerFoliageMixin {
    @Shadow
    private static int[] foliageBuffer;

    @Overwrite
    public static int getFoliageColor(double temperature, double humidity) {
        humidity = humidity * temperature;
        int i = (int) ((1.0D - temperature) * 255.0D);
        int j = (int) ((1.0D - humidity) * 255.0D);
        return Season.getCurrentSeason().getFoliageColor(foliageBuffer[j << 8 | i]);
    }

    @Overwrite
    public static int getFoliageColorPine() {
        return 6396257;
    }

    @Overwrite
    public static int getFoliageColorBirch() {
        if (Season.getCurrentSeason() == Season.AUTUMN) {
            return new Color(255, 250, 0).getRGB();
        } else {
            return Season.getCurrentSeason().getFoliageColor(8431445);
        }
    }

    @Overwrite
    public static int getFoliageColorBasic() {
        return Season.getCurrentSeason().getFoliageColor(4764952);
    }
}
