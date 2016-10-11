package com.mordrum.mfarm.common;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GeneListener {
	@SubscribeEvent
	public void onEntityDamaged(LivingHurtEvent event) {
		Entity entity = event.getEntity();
		if (entity.getEntityData().hasKey("explosive")) {
			if (entity.getEntityData().getBoolean("explosive")) {
				BlockPos position = entity.getPosition();
				entity.getEntityWorld().createExplosion(entity, position.getX(), position.getY(), position.getZ(), 1.0f, true);
			}
		}
	}
}
