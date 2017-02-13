package com.mordrum.mrpg.common.items

class WeaponKnife(material: String, damageOffset: Double) : AbstractWeapon(material, "knife", 1.5, 5.0, 0.0, 2.0 + damageOffset, 1.0 + damageOffset, 0.0)