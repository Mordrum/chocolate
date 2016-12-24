package com.mordrum.mmetallurgy.common.blocks

import com.mordrum.mmetallurgy.MMetallurgy
import net.minecraft.block.BlockTNT
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side

class VolatileTNT : BlockTNT() {
    init {
        defaultState = this.blockState.baseState
        setRegistryName(MMetallurgy.MOD_ID, "volatile_tnt")
    }

    override fun onBlockDestroyedByExplosion(worldIn: World, pos: BlockPos, explosionIn: Explosion) {
        if (FMLCommonHandler.instance().effectiveSide == Side.CLIENT) return

        worldIn.createExplosion(explosionIn.explosivePlacedBy, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 12.0f, true)
    }

    override fun explode(worldIn: World, pos: BlockPos, state: IBlockState, igniter: EntityLivingBase?) {
        if (state.getValue(EXPLODE)) {
            if (FMLCommonHandler.instance().effectiveSide == Side.CLIENT) return

            worldIn.createExplosion(igniter, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 12.0f, true)
        }
    }
}