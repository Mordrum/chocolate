package com.mordrum.mfarm.mixins;

import com.mordrum.mfarm.events.EntityBreedEvent;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.AchievementList;
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
	private EntityAnimal theAnimal;
	@Shadow
	World theWorld;
	@Shadow
	private EntityAnimal targetMate;

	@Overwrite
	private void spawnBaby() {
		EntityAgeable entityageable = this.theAnimal.createChild(this.targetMate);

		if (entityageable != null) {
			EntityPlayer entityplayer = this.theAnimal.getPlayerInLove();

			if (entityplayer == null && this.targetMate.getPlayerInLove() != null) {
				entityplayer = this.targetMate.getPlayerInLove();
			}

			if (entityplayer != null) {
				entityplayer.addStat(StatList.ANIMALS_BRED);

				if (this.theAnimal instanceof EntityCow) {
					entityplayer.addStat(AchievementList.BREED_COW);
				}
			}

			// Set the defaults before we fire the event so changes will persist
			entityageable.setGrowingAge(-24000);
			entityageable.setLocationAndAngles(this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, 0.0F, 0.0F);
			EntityBreedEvent breedEvent = new EntityBreedEvent(entityageable, this.theAnimal, this.targetMate);
			MinecraftForge.EVENT_BUS.post(breedEvent);
			if (breedEvent.isCanceled()) return;

			this.theAnimal.setGrowingAge(6000);
			this.targetMate.setGrowingAge(6000);
			this.theAnimal.resetInLove();
			this.targetMate.resetInLove();

			this.theWorld.spawnEntityInWorld(entityageable);
			Random random = this.theAnimal.getRNG();

			for (int i = 0; i < 7; ++i) {
				double d0 = random.nextGaussian()*0.02D;
				double d1 = random.nextGaussian()*0.02D;
				double d2 = random.nextGaussian()*0.02D;
				double d3 = random.nextDouble()*(double) this.theAnimal.width*2.0D - (double) this.theAnimal.width;
				double d4 = 0.5D + random.nextDouble()*(double) this.theAnimal.height;
				double d5 = random.nextDouble()*(double) this.theAnimal.width*2.0D - (double) this.theAnimal.width;
				this.theWorld.spawnParticle(EnumParticleTypes.HEART,
						this.theAnimal.posX + d3, this.theAnimal.posY + d4, this.theAnimal.posZ + d5, d0, d1, d2);
			}

			if (this.theWorld.getGameRules().getBoolean("doMobLoot")) {
				this.theWorld.spawnEntityInWorld(new EntityXPOrb(this.theWorld, this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ,
						random.nextInt(7) + 1));
			}
		}
	}
}
