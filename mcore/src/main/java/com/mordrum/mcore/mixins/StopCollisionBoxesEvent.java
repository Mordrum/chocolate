package com.mordrum.mcore.mixins;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(World.class)
public abstract class StopCollisionBoxesEvent implements IBlockAccess, net.minecraftforge.common.capabilities.ICapabilityProvider {
	@Overwrite
	private boolean func_191504_a(@Nullable Entity p_191504_1_, AxisAlignedBB p_191504_2_, boolean p_191504_3_, @Nullable List<AxisAlignedBB> p_191504_4_) {
		int i = MathHelper.floor(p_191504_2_.minX) - 1;
		int j = MathHelper.ceil(p_191504_2_.maxX) + 1;
		int k = MathHelper.floor(p_191504_2_.minY) - 1;
		int l = MathHelper.ceil(p_191504_2_.maxY) + 1;
		int i1 = MathHelper.floor(p_191504_2_.minZ) - 1;
		int j1 = MathHelper.ceil(p_191504_2_.maxZ) + 1;
		WorldBorder worldborder = this.getWorldBorder();
		boolean flag = p_191504_1_ != null && p_191504_1_.isOutsideBorder();
		boolean flag1 = p_191504_1_ != null && this.func_191503_g(p_191504_1_);
		IBlockState iblockstate = Blocks.STONE.getDefaultState();
		BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

		try {
			for (int k1 = i; k1 < j; ++k1) {
				for (int l1 = i1; l1 < j1; ++l1) {
					boolean flag2 = k1 == i || k1 == j - 1;
					boolean flag3 = l1 == i1 || l1 == j1 - 1;

					if ((!flag2 || !flag3) && this.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(k1, 64, l1))) {
						for (int i2 = k; i2 < l; ++i2) {
							if (!flag2 && !flag3 || i2 != l - 1) {
								if (p_191504_3_) {
									if (k1 < -30000000 || k1 >= 30000000 || l1 < -30000000 || l1 >= 30000000) {
										return true;
									}
								} else if (p_191504_1_ != null && flag == flag1) {
									p_191504_1_.setOutsideBorder(!flag1);
								}

								blockpos$pooledmutableblockpos.setPos(k1, i2, l1);
								IBlockState iblockstate1;

								if (!p_191504_3_ && !worldborder.contains(blockpos$pooledmutableblockpos) && flag1) {
									iblockstate1 = iblockstate;
								} else {
									iblockstate1 = this.getBlockState(blockpos$pooledmutableblockpos);
								}

								Object o = this;
								World w = (World) o;
								iblockstate1.addCollisionBoxToList(w, blockpos$pooledmutableblockpos, p_191504_2_, p_191504_4_, p_191504_1_, false);
//								net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.GetCollisionBoxesEvent(this, null, p_191504_2_, p_191504_4_));

								if (p_191504_3_ && !p_191504_4_.isEmpty()) {
									return true;
								}
							}
						}
					}
				}
			}
		} finally {
			blockpos$pooledmutableblockpos.release();
		}

		return !p_191504_4_.isEmpty();
	}

	@Overwrite
	public List<AxisAlignedBB> getCollisionBoxes(@Nullable Entity entityIn, AxisAlignedBB aabb) {
		List<AxisAlignedBB> list = Lists.newArrayList();
		this.func_191504_a(entityIn, aabb, false, list);

		if (entityIn != null) {
			List<Entity> list1 = this.getEntitiesWithinAABBExcludingEntity(entityIn, aabb.expandXyz(0.25D));

			for (Entity aList1 : list1) {
				if (!entityIn.isRidingSameEntity(aList1)) {
					AxisAlignedBB axisalignedbb = aList1.getCollisionBoundingBox();

					if (axisalignedbb != null && axisalignedbb.intersectsWith(aabb)) {
						list.add(axisalignedbb);
					}

					axisalignedbb = entityIn.getCollisionBox(aList1);

					if (axisalignedbb != null && axisalignedbb.intersectsWith(aabb)) {
						list.add(axisalignedbb);
					}
				}
			}
		}
//		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.GetCollisionBoxesEvent(this, entityIn, aabb, list));
		return list;
	}

	@Shadow
	protected abstract List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entityIn, AxisAlignedBB axisAlignedBB);

	@Shadow
	protected abstract boolean func_191503_g(Entity p_191504_1_);

	@Shadow
	public abstract boolean isBlockLoaded(BlockPos pos);

	@Shadow
	public abstract WorldBorder getWorldBorder();
}
