package com.mordrum.mrpg.common.mixins;

import com.mordrum.mrpg.common.RPGAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {
	@Overwrite
	private int getArmSwingAnimationEnd() {
		if (getHeldItemMainhand() == ItemStack.EMPTY) return 6;
		else {
			IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
			if (entityAttribute == null) return 6;
			else {
				for (AttributeModifier attributeModifier : entityAttribute.getModifiers()) {
//					System.out.println(attributeModifier.getName());

				}

				return (int) (20 / (entityAttribute.getAttributeValue() - 4));
			}
		}
	}

	@Shadow
	public abstract ItemStack getHeldItemMainhand();

	@Shadow
	public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);
}
