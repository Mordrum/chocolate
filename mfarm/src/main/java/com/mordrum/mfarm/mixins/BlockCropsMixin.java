package com.mordrum.mfarm.mixins;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockCrops.class)
public abstract class BlockCropsMixin implements IPlantable {
    @Overwrite
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        IBlockState soil = worldIn.getBlockState(pos.down());
        return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
    }

    @Inject(method = "updateTick", at = @At("HEAD"), cancellable =  true)
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo callbackInfo) {
        if (!worldIn.canSeeSky(pos)) {
            callbackInfo.cancel();
        }
    }
}

