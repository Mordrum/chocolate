package com.mordrum.mfarm.mixins;

import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBush.class)
public abstract class BlockBushMixin {
    @Inject(method = "checkAndDropBlock", at = @At("HEAD"), cancellable = true)
    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state, CallbackInfo callbackInfo) {
        callbackInfo.cancel();
    }
}
