package com.mordrum.mclimate.mixins;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends World implements IThreadListener {
    @Shadow
    @Final
    private PlayerChunkMap playerChunkMap;

    protected WorldServerMixin(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }

    @Overwrite
    public void updateBlocks() {
        this.playerCheckLight();

        int i = this.getGameRules().getInt("randomTickSpeed");
        boolean flag = this.isRaining();
        boolean flag1 = this.isThundering();
        this.theProfiler.startSection("pollingChunks");

        for (Iterator<Chunk> iterator = getPersistentChunkIterable(this.playerChunkMap.getChunkIterator()); iterator.hasNext(); this.theProfiler.endSection()) {
            this.theProfiler.startSection("getChunk");
            Chunk chunk = (Chunk) iterator.next();
            int j = chunk.xPosition * 16;
            int k = chunk.zPosition * 16;
            this.theProfiler.endStartSection("checkNextLight");
            chunk.enqueueRelightChecks();
            this.theProfiler.endStartSection("tickChunk");
            chunk.onTick(false);
            this.theProfiler.endStartSection("thunder");

            if (this.provider.canDoLightning(chunk) && flag && flag1 && this.rand.nextInt(100000) == 0) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int l = this.updateLCG >> 2;
                BlockPos blockpos = this.adjustPosToNearbyEntity(new BlockPos(j + (l & 15), 0, k + (l >> 8 & 15)));

                if (this.isRainingAt(blockpos)) {
                    DifficultyInstance difficultyinstance = this.getDifficultyForLocation(blockpos);

                    if (this.rand.nextDouble() < (double) difficultyinstance.getAdditionalDifficulty() * 0.05D) {
                        EntitySkeletonHorse skeletonHorse = new EntitySkeletonHorse(this);
                        skeletonHorse.setTrap(true);
                        skeletonHorse.setGrowingAge(0);
                        skeletonHorse.setPosition((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ());
                        this.spawnEntity(skeletonHorse);
                        this.addWeatherEffect(new EntityLightningBolt(this, (double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ(), true));
                    } else {
                        this.addWeatherEffect(new EntityLightningBolt(this, (double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ(), false));
                    }
                }
            }

            this.theProfiler.endStartSection("iceandsnow");

            if (this.provider.canDoRainSnowIce(chunk) && this.rand.nextInt(16) == 0) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int j2 = this.updateLCG >> 2;
                BlockPos blockpos1 = this.getPrecipitationHeight(new BlockPos(j + (j2 & 15), 0, k + (j2 >> 8 & 15)));
                checkBlockPosForChanges(blockpos1, flag);
            }

            this.theProfiler.endStartSection("tickBlocks");

            if (i > 0) {
                for (ExtendedBlockStorage extendedblockstorage : chunk.getBlockStorageArray()) {
                    if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE && extendedblockstorage.getNeedsRandomTick()) {
                        for (int i1 = 0; i1 < i; ++i1) {
                            this.updateLCG = this.updateLCG * 3 + 1013904223;
                            int j1 = this.updateLCG >> 2;
                            int k1 = j1 & 15;
                            int l1 = j1 >> 8 & 15;
                            int i2 = j1 >> 16 & 15;
                            IBlockState iblockstate = extendedblockstorage.get(k1, i2, l1);
                            Block block = iblockstate.getBlock();
                            this.theProfiler.startSection("randomTick");

                            if (block.getTickRandomly()) {
                                block.randomTick(this, new BlockPos(k1 + j, i2 + extendedblockstorage.getYLocation(), l1 + k), iblockstate, this.rand);
                            }

                            this.theProfiler.endSection();
                        }
                    }
                }
            }
        }

        this.theProfiler.endSection();
    }

    @Shadow
    public abstract void playerCheckLight();

    @Shadow
    protected abstract BlockPos adjustPosToNearbyEntity(BlockPos pos);

    private void checkBlockPosForChanges(BlockPos blockPos, boolean isRaining) {
        // If we can freeze the blockpos, replace it with ice
        if (this.canBlockFreezeNoWater(blockPos)) {
            this.setBlockState(blockPos, Blocks.ICE.getDefaultState());
        }

        // If it's raining and we can fill this spot in with water, fill it
        BlockPos belowPos = blockPos.down();
        Block belowBlock = this.getBlockState(belowPos).getBlock();
        if (isRaining && this.getBiome(belowPos).canRain()) {
            belowBlock.fillWithRain(this, belowPos);
        }

        // If it's raining and snow can occur at the blockpos, place some snow
        if (isRaining) {
           if (this.canSnowAt(blockPos, false)) {
               this.setBlockState(blockPos, Blocks.SNOW_LAYER.getDefaultState());
               // If the block below the one we're trying to precipitate onto is leaves, fall through
               if (belowBlock instanceof BlockLeaves) {
                   checkFallthrough(belowPos);
               }
           } else if (this.getBlockState(blockPos).getBlock() instanceof BlockBush || this.getBlockState(blockPos).getBlock() == Blocks.FARMLAND) {
               if (this.getBlockState(blockPos.down()).getBlock() == Blocks.FARMLAND) {
                   this.setBlockState(blockPos.down(), Blocks.DIRT.getDefaultState());
               }
               this.setBlockState(blockPos, Blocks.SNOW_LAYER.getDefaultState());
           }
        }
    }

    private void checkFallthrough(BlockPos blockPos) {
        Block block = this.getBlockState(blockPos).getBlock();
        if (block instanceof BlockLeaves || block instanceof BlockBush || block == Blocks.AIR || block == Blocks.LOG || block == Blocks.LOG2) {
            checkFallthrough(blockPos.down());
        } else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
            this.setBlockState(blockPos, Blocks.ICE.getDefaultState());
        } else if (block != Blocks.SNOW_LAYER && block != Blocks.ICE) {
            if (block == Blocks.FARMLAND) this.setBlockState(blockPos, Blocks.DIRT.getDefaultState());
            this.setBlockState(blockPos.up(), Blocks.SNOW_LAYER.getDefaultState());
        }
    }
}
