package com.mordrum.mrpg.common.mixins;

import com.mordrum.mrpg.common.MixinEntityPlayerDelegate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {
	public MixinEntityPlayer(World worldIn) {
		super(worldIn);
	}

	@Overwrite
	public void attackTargetEntityWithCurrentItem(Entity entity) {
		EntityPlayer _this = (EntityPlayer) (Object) this;
		MixinEntityPlayerDelegate.INSTANCE.attackTargetEntityWithCurrentItem(entity, _this);
	}
}
