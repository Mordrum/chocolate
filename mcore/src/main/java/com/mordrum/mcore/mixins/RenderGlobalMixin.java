package com.mordrum.mcore.mixins;

import com.mordrum.mcore.client.MultiMineClient;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin implements MultiMineClient.RenderGlobalExtension {
    @Shadow
    @Final
    private Map<Integer, DestroyBlockProgress> damagedBlocks;

    public Map<Integer, DestroyBlockProgress> getDamagedBlocks() {
        return damagedBlocks;
    }
}


