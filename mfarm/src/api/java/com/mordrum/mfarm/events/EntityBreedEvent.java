package com.mordrum.mfarm.events;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.living.LivingEvent;

public class EntityBreedEvent extends LivingEvent {
	private final EntityLiving mateOne;
	private final EntityLiving mateTwo;

	public EntityBreedEvent(EntityLiving spawnedEntity, EntityLiving mateOne, EntityLiving mateTwo) {
		super(spawnedEntity);
		this.mateOne = mateOne;
		this.mateTwo = mateTwo;
	}

	public EntityLiving getMateOne() {
		return mateOne;
	}

	public EntityLiving getMateTwo() {
		return mateTwo;
	}
}
