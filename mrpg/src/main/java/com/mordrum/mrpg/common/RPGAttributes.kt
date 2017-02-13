package com.mordrum.mrpg.common

import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.entity.ai.attributes.RangedAttribute

object RPGAttributes {
    // Determines how fast the health of an entity regenerates
    val VITALITY: IAttribute = RangedAttribute(null, "mrpg.vitality", 0.0, 0.0, 1000.0)

    // Determines how strong an entity's attacks are
    val STRENGTH: IAttribute = RangedAttribute(null, "mrpg.strength", 0.0, 0.0, 1000.0)

    // Determines how resistant the entity is to physical damage
    val DEFENSE: IAttribute = RangedAttribute(null, "mrpg.defense", 0.0, 0.0, 1000.0)

    // Determines the move speed of the entity along with its resistance to fall damage
    val AGILITY: IAttribute = RangedAttribute(null, "mrpg.agility", 0.0, 0.0, 1000.0)

    // The odds of this entity scoring a critical hit (all entities have a 1% natural crit chance)
    val CRITICAL_CHANCE: IAttribute = RangedAttribute(null, "mrpg.critical_chance", 1.0, 0.0, 1000.0)

    val MAX_STAMINA: IAttribute = RangedAttribute(null, "mrpg.max_stamina", 0.0, 0.0, 1000.0)
    val MAX_SPIRIT: IAttribute = RangedAttribute(null, "mrpg.max_spirit", 0.0, 0.0, 1000.0)

    val SWEEP_RANGE: IAttribute = RangedAttribute(null, "mrpg.sweep_range", 0.0, 0.0, 1000.0)
    val DAMAGE_BLUNT: IAttribute = RangedAttribute(null, "mrpg.damage_blunt", 0.0, 0.0, 1000.0)
    val DAMAGE_PIERCE: IAttribute = RangedAttribute(null, "mrpg.damage_pierce", 0.0, 0.0, 1000.0)
    val DAMAGE_SLASH: IAttribute = RangedAttribute(null, "mrpg.damage_slash", 0.0, 0.0, 1000.0)

    val RESISTANCE_BLUNT: IAttribute = RangedAttribute(null, "mrpg.resistance_blunt", 0.0, 0.0, 1000.0)
    val RESISTANCE_PIERCE: IAttribute = RangedAttribute(null, "mrpg.resistance_pierce", 0.0, 0.0, 1000.0)
    val RESISTANCE_SLASH: IAttribute = RangedAttribute(null, "mrpg.resistance_slash", 0.0, 0.0, 1000.0)

}