package com.mordrum.mrpg.common

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.mordrum.mrpg.common.items.AbstractWeapon
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item
import java.util.*

object MixinItemSwordDelegate {
    fun getItemAttributeModifiers(item: Item, slot: EntityEquipmentSlot, material: Item.ToolMaterial): Multimap<String, AttributeModifier> {
        val attributeModifiers = HashMultimap.create<String, AttributeModifier>()

        val damageOffset: Double
        when (material) {
            Item.ToolMaterial.WOOD -> damageOffset = 0.0
            Item.ToolMaterial.STONE -> damageOffset = 1.0
            Item.ToolMaterial.IRON -> damageOffset = 2.0
            Item.ToolMaterial.DIAMOND -> damageOffset = 3.0
            Item.ToolMaterial.GOLD -> damageOffset = 0.0
            else -> damageOffset = 0.0
        }

        if (slot == EntityEquipmentSlot.MAINHAND) {
            attributeModifiers.put(SharedMonsterAttributes.ATTACK_SPEED.name, AttributeModifier(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), "Weapon modifier", 1.0, 0))
            attributeModifiers.put(RPGAttributes.CRITICAL_CHANCE.name, AttributeModifier(AbstractWeapon.CRITICAL_CHANCE_MODIFIER, "Weapon modifier", 0.0, 0))
            attributeModifiers.put(RPGAttributes.DAMAGE_BLUNT.name, AttributeModifier(AbstractWeapon.DAMAGE_BLUNT_MODIFIER, "Weapon modifier", 0.0, 0))
            attributeModifiers.put(RPGAttributes.DAMAGE_PIERCE.name, AttributeModifier(AbstractWeapon.DAMAGE_PIERCE_MODIFIDER, "Weapon modifier", 1.0 + damageOffset, 0))
            attributeModifiers.put(RPGAttributes.DAMAGE_SLASH.name, AttributeModifier(AbstractWeapon.DAMAGE_SLASH_MODIFIER, "Weapon modifier", 2.0 + damageOffset, 0))
            attributeModifiers.put(RPGAttributes.SWEEP_RANGE.name, AttributeModifier(AbstractWeapon.SWEEP_RANGE_MODIFIER, "Weapon modifier", 0.0, 0))
        }

        return attributeModifiers
    }
}
