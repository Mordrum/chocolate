package com.mordrum.mcore.mixins;

import com.google.gson.Gson;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.gen.ChunkProviderSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkProviderSettings.Factory.class)
public class ChunkProviderSettingsFactoryMixin {
    @Shadow
    @Final
    static Gson JSON_ADAPTER;

    @Overwrite
    public static ChunkProviderSettings.Factory jsonToFactory(String json) {
        if (!json.isEmpty()) {
            try {
                System.out.print("Totes Custom JSON");
                System.out.println(json);
                return JsonUtils.gsonDeserialize(JSON_ADAPTER, json, ChunkProviderSettings.Factory.class);
            } catch (Exception ignored) {
            }
        }

        ChunkProviderSettings.Factory factory = new ChunkProviderSettings.Factory();
        factory.seaLevel = 127;
        return factory;
    }
}
