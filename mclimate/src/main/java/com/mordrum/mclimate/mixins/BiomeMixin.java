package com.mordrum.mclimate.mixins;

import com.mordrum.mclimate.common.Season;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeMixin {
    @Shadow
    @Final
    private float temperature;

    @Inject(method = "getTemperature", at = @At("HEAD"), cancellable = true)
    public void getTemperature(CallbackInfoReturnable<Float> returnable) {
        returnable.setReturnValue(Season.getCurrentSeason().getModifiedTemperature(temperature));
    }

    @Inject(method = "isSnowyBiome", at = @At("HEAD"), cancellable = true)
    public void isSnowyBiome(CallbackInfoReturnable<Boolean> returnable) {
        if (canSnow()) returnable.setReturnValue(true);
    }

    @Shadow
    public abstract Biome.TempCategory getTempCategory();

    @Inject(method = "canRain", at = @At("HEAD"), cancellable = true)
    public void canRain(CallbackInfoReturnable<Boolean> returnable) {
        returnable.setReturnValue(!this.canSnow());
    }

    @Inject(method = "getRainfall", at = @At("HEAD"), cancellable = true)
    public void getRainfall(CallbackInfoReturnable<Float> returnable) {
        returnable.setReturnValue(30.0f);
    }

    // Add this method so canRain can call it
    private boolean canSnow() {
        return (Season.getCurrentSeason() == Season.WINTER && this.temperature <= 0.8f);
    }
}

