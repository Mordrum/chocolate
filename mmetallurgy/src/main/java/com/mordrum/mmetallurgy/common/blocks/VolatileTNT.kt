package com.mordrum.mmetallurgy.common.blocks

import com.mordrum.mmetallurgy.MMetallurgy
import net.malisis.core.MalisisCore
import net.malisis.core.block.MalisisBlock
import net.malisis.core.renderer.icon.provider.IIconProvider
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side

class VolatileTNT : MalisisBlock(Material.TNT) {
    init {
        setName("volatile_tnt")
        if (MalisisCore.isClient()) {
            val build = IIconProvider.create(MMetallurgy.MOD_ID + ":blocks/misc/volatile_tnt_", "side")
                    .withSide(EnumFacing.UP, "top")
                    .withSide(EnumFacing.DOWN, "bottom")
                    .build()
            this.addComponent(build)
        }
    }

    fun explode(world: World, pos: BlockPos, igniter: EntityLivingBase?) {
        if (FMLCommonHandler.instance().effectiveSide == Side.CLIENT) return

        world.setBlockState(pos, Blocks.AIR.defaultState, 11)
        world.createExplosion(igniter, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 8.0f, true)
    }

    // Other explosions detonate TNT
    override fun onBlockDestroyedByExplosion(worldIn: World, pos: BlockPos, explosionIn: Explosion) {
        this.explode(worldIn, pos, explosionIn.explosivePlacedBy)
    }

    // Flint and steel or fire charge detonates TNT
    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val itemstack = playerIn.getHeldItem(hand)

        if (!itemstack.isEmpty && (itemstack.item === Items.FLINT_AND_STEEL || itemstack.item === Items.FIRE_CHARGE)) {
            this.explode(worldIn, pos, playerIn)

            if (itemstack.item === Items.FLINT_AND_STEEL) {
                itemstack.damageItem(1, playerIn)
            } else if (!playerIn.capabilities.isCreativeMode) {
                itemstack.shrink(1)
            }

            return true
        } else {
            return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
        }
    }

    // Flaming arrow detonates TNT
    override fun onEntityCollidedWithBlock(worldIn: World, pos: BlockPos, state: IBlockState, entityIn: Entity) {
        if (!worldIn.isRemote && entityIn is EntityArrow) {
            if (entityIn.isBurning) {
                this.explode(worldIn, pos, if (entityIn.shootingEntity is EntityLivingBase) entityIn.shootingEntity as EntityLivingBase else null)
            }
        }
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)

        if (worldIn.isBlockPowered(pos)) {
            this.explode(worldIn, pos, null)
        }
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        if (worldIn.isBlockPowered(pos)) {
            this.explode(worldIn, pos, null)
        }
    }
}