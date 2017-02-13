package com.mordrum.mrpg.common.mixins;

import com.google.common.collect.Multimap;
import com.mordrum.mrpg.common.MixinItemSwordDelegate;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemSword.class)
public class MixinItemSword extends Item {
	@Shadow
	@Final
	private Item.ToolMaterial material;

	@Overwrite
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		return MixinItemSwordDelegate.INSTANCE.getItemAttributeModifiers(this, equipmentSlot, material);
	}
}
