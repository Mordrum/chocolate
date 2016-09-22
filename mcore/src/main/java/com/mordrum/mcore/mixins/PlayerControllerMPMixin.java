package com.mordrum.mcore.mixins;

import com.mordrum.mcore.client.MultiMineClient;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class PlayerControllerMPMixin {
    @Shadow
    private float curBlockDamageMP;

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"))
    public void onPlayerDamageBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable callbackInfo) {
        this.curBlockDamageMP = MultiMineClient.instance().eventPlayerDamageBlock(loc, curBlockDamageMP);
    }
}
