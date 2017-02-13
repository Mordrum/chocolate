package com.mordrum.mrpg.common.items

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation

class WeaponSword(material: String, damageOffset: Double) : AbstractWeapon(material, "sword", 1.0, 0.0, 0.0, 1.0 + damageOffset, 2.0 + damageOffset, 0.0) {
    override fun register(): AbstractWeapon {
        this.registryName = ResourceLocation("minecraft", "${material}_sword")
        this.unlocalizedName = "sword${material.capitalize()}"

        val id = when(material) {
            "wood" -> 268
            "stone" -> 272
            "iron" -> 267
            "gold" -> 283
            "diamond" -> 276
            else -> 0
        }
        Item.REGISTRY.register(id, this.registryName, this)
        return this
    }
}