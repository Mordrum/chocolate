package com.mordrum.mrpg.common.items

import com.google.common.collect.Multimap
import com.mordrum.mrpg.MRPG
import com.mordrum.mrpg.common.RPGAttributes
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.*

open class ItemWeapon(metal: String, type: String, material: Item.ToolMaterial, val attackDamage: Double, val attackSpeed: Double, val criticalChance: Double) : Item() {
    val CRITICAL_CHANCE_MODIFIER: UUID = UUID.fromString("243637f3-8036-4499-9a01-ae5d3c010069")
    val SWEEP_RANGE_MODIFIER: UUID = UUID.fromString("eaee5d0c-8d96-4ef7-9be7-0aa07f7e9034")
    var sweepRange: Double

    init {
        this.maxDamage = material.maxUses
        this.creativeTab = CreativeTabs.COMBAT
        this.maxStackSize = 1
        this.unlocalizedName = "${MRPG.MOD_ID}.$metal.$type"
        this.setRegistryName(MRPG.MOD_ID, "$type.$metal")
        this.sweepRange = 0.0
    }

    override fun getAttributeModifiers(slot: EntityEquipmentSlot?, stack: ItemStack?): Multimap<String, AttributeModifier> {
        val attributeModifiers = super.getAttributeModifiers(slot, stack)

        if (slot == EntityEquipmentSlot.MAINHAND) {
            attributeModifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.name, AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, 0))
            attributeModifiers.put(SharedMonsterAttributes.ATTACK_SPEED.name, AttributeModifier(Item.ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, 0))
            attributeModifiers.put(RPGAttributes.CRITICAL_CHANCE.name, AttributeModifier(CRITICAL_CHANCE_MODIFIER, "Weapon modifier", this.criticalChance, 0))
            if (sweepRange > 0.0) {
                attributeModifiers.put(RPGAttributes.SWEEP_RANGE.name, AttributeModifier(SWEEP_RANGE_MODIFIER, "Weapon modifier", this.sweepRange, 0))
            }
        }

        return attributeModifiers
    }

    override fun shouldRotateAroundWhenRendering(): Boolean {
        return super.shouldRotateAroundWhenRendering()
    }
}