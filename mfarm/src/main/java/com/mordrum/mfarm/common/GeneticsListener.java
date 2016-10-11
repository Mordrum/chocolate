package com.mordrum.mfarm.common;

import com.mordrum.mfarm.events.EntityBreedEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.ThreadLocalRandom;

public class GeneticsListener {
	@SubscribeEvent
	public void onEntityBreed(EntityBreedEvent entityBreedEvent) {
		// 2 gene types
		// Discrete, AKA cow produces chocolate milk
		// Continuous, AKA max health is 18
		// a child inheriting a continuous gene will get a value ranging from X - Z to Y + Z, where X is the father's value, Y is the mother's, and Z is the average between the two

		// Ensure both mates have genetic values
		NBTTagCompound parent1NBT = getOrPopulateNBT(entityBreedEvent.getMateOne());
		NBTTagCompound parent2NBT = getOrPopulateNBT(entityBreedEvent.getMateTwo());

		NBTTagCompound childData = entityBreedEvent.getEntity().getEntityData();
		for (String key : parent1NBT.getKeySet()) {
			NBTBase parent1Tag = parent1NBT.getTag(key);
			NBTBase parent2Tag = parent2NBT.getTag(key);

			if (parent1Tag instanceof NBTTagByte) {
				childData.setBoolean(key, determineInheritedBoolean( ((NBTTagByte) parent1Tag).getByte() != 0, ((NBTTagByte) parent2Tag).getByte() != 0));
			} else if (parent1Tag instanceof NBTTagDouble) {
				childData.setDouble(key, determineInheritedDouble(((NBTTagDouble) parent1Tag).getDouble(), ((NBTTagDouble) parent2Tag).getDouble()));
			}
			//TODO else statement here for string switching
		}
		System.out.println(childData.toString());
	}

	private double determineInheritedDouble(double parent1Double, double parent2Double) {
		double lower;
		double upper;
		double average;

		if (parent1Double > parent2Double) {
			upper = parent1Double;
			lower = parent2Double;
		} else {
			upper = parent2Double;
			lower = parent1Double;
		}
		average = (parent1Double + parent2Double) / 2;

		return ThreadLocalRandom.current().nextDouble(lower - average / 2, upper + average / 2);
	}

	private boolean determineInheritedBoolean(boolean parent1Boolean, boolean parent2Boolean) {
		if (parent1Boolean == parent2Boolean) {
			if (Math.random() > 0.9) return !parent1Boolean;
			else return parent1Boolean;
		}

		return Math.random() > 0.5;
	}

	private NBTTagCompound getOrPopulateNBT(EntityLiving entity) {
		NBTTagCompound entityData = entity.getEntityData();
		if (!entityData.hasKey("maxHealth")) entityData.setDouble("maxHealth", 10.0);
		if (!entityData.hasKey("moveSpeed")) entityData.setDouble("moveSpeed", 1.0);
		if (!entityData.hasKey("explosive")) entityData.setBoolean("explosive", false);
		if (!entityData.hasKey("damageResistance")) entityData.setBoolean("damageResistance", false);
		if (!entityData.hasKey("fireResistance")) entityData.setBoolean("fireResistance", false);

		if (entity instanceof EntityCow) {
			if (!entityData.hasKey("produces")) entityData.setString("produces", Items.MILK_BUCKET.getRegistryName().toString());
		}

		return entityData;
	}
}
