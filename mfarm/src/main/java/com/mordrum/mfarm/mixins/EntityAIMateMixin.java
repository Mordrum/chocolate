package com.mordrum.mfarm.mixins;

import com.mordrum.mfarm.events.EntityBreedEvent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(EntityAIMate.class)
public class EntityAIMateMixin {
	@Shadow
	@Final
	private EntityAnimal animal;
	@Shadow
	World world;
	@Shadow
	private EntityAnimal targetMate;

	@Overwrite
	private void spawnBaby() {
		EntityAgeable entityageable = this.animal.createChild(this.targetMate);

		if (entityageable != null) {
			EntityPlayerMP entityplayer = this.animal.getLoveCause();

			if (entityplayer == null && this.targetMate.getLoveCause() != null) {
				entityplayer = this.targetMate.getLoveCause();
			}

			if (entityplayer != null) {
				entityplayer.addStat(StatList.ANIMALS_BRED);

				if (this.animal instanceof EntityCow) {
					CriteriaTriggers.BRED_ANIMALS.trigger(entityplayer, this.animal, this.targetMate, entityageable);
				}
			}

			// Set the defaults before we fire the event so changes will persist
			entityageable.setGrowingAge(-24000);
			entityageable.setLocationAndAngles(this.animal.posX, this.animal.posY, this.animal.posZ, 0.0F, 0.0F);
			EntityBreedEvent breedEvent = new EntityBreedEvent(entityageable, this.animal, this.targetMate);
			MinecraftForge.EVENT_BUS.post(breedEvent);
			if (breedEvent.isCanceled()) return;

			this.animal.setGrowingAge(6000);
			this.targetMate.setGrowingAge(6000);
			this.animal.resetInLove();
			this.targetMate.resetInLove();

			this.world.spawnEntity(entityageable);
			Random random = this.animal.getRNG();

			for (int i = 0; i < 7; ++i) {
				double d0 = random.nextGaussian()*0.02D;
				double d1 = random.nextGaussian()*0.02D;
				double d2 = random.nextGaussian()*0.02D;
				double d3 = random.nextDouble()*(double) this.animal.width*2.0D - (double) this.animal.width;
				double d4 = 0.5D + random.nextDouble()*(double) this.animal.height;
				double d5 = random.nextDouble()*(double) this.animal.width*2.0D - (double) this.animal.width;
				this.world.spawnParticle(EnumParticleTypes.HEART,
						this.animal.posX + d3, this.animal.posY + d4, this.animal.posZ + d5, d0, d1, d2);
			}

			if (this.world.getGameRules().getBoolean("doMobLoot")) {
				this.world.spawnEntity(new EntityXPOrb(this.world, this.animal.posX, this.animal.posY, this.animal.posZ,
						random.nextInt(7) + 1));
			}
		}
	}
}
