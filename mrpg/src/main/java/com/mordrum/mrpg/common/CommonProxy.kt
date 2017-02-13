package com.mordrum.mrpg.common

import com.mordrum.mcore.common.stats.StatManager
import com.mordrum.mrpg.MRPG
import com.mordrum.mrpg.common.items.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import java.util.*

open class CommonProxy {
    companion object {
        // Networking stuff
        val NETWORK_WRAPPER: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MRPG.MOD_ID)
        private var DISCRIMINATOR = 0
    }

    val items: MutableList<Item> = ArrayList()

    open fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
//        CapabilityManager.INSTANCE.register(IStamina::class.java, StaminaStorage(), StaminaImpl::class.java)
//        CapabilityManager.INSTANCE.register(IMana::class.java, ManaStorage(), ManaImpl::class.java)
//        MinecraftForge.EVENT_BUS.register(CapabilityHandler())

        StatManager.registerStat("staminaBar", StaminaStat::class)

//        NETWORK_WRAPPER.registerMessage(MessageUpdateStat::class.java, MessageUpdateStat::class.java, DISCRIMINATOR++, Side.CLIENT)
    }

    open fun init(event: FMLInitializationEvent) {
        val materials = listOf(Pair("wood", 0.0), Pair("stone", 0.5), Pair("iron", 1.0), Pair("gold", 0.0), Pair("diamond", 1.5))

        for ((material, offset) in materials) {
            items.add(WeaponBattleaxe(material, offset).register())
            items.add(WeaponFlail(material, offset).register())
            items.add(WeaponHalberd(material, offset).register())
            items.add(WeaponKnife(material, offset).register())
            items.add(WeaponKatana(material, offset * 2).register())
            items.add(WeaponSpear(material, offset * 2).register())
            items.add(WeaponWarhammer(material, offset * 2).register())
//            items.add(WeaponSword(material, offset).register())
        }
    }

    @SubscribeEvent
    fun onEntityConstructing(event: EntityEvent.EntityConstructing) {
        if (event.entity !is EntityLivingBase) return

        val entity = event.entity as EntityLivingBase
        entity.attributeMap.registerAttribute(RPGAttributes.DEFENSE)
        entity.attributeMap.registerAttribute(RPGAttributes.VITALITY)
        entity.attributeMap.registerAttribute(RPGAttributes.STRENGTH)
        entity.attributeMap.registerAttribute(RPGAttributes.CRITICAL_CHANCE)
        entity.attributeMap.registerAttribute(RPGAttributes.AGILITY)
        entity.attributeMap.registerAttribute(RPGAttributes.SWEEP_RANGE)
        entity.attributeMap.registerAttribute(RPGAttributes.DAMAGE_BLUNT)
        entity.attributeMap.registerAttribute(RPGAttributes.DAMAGE_PIERCE)
        entity.attributeMap.registerAttribute(RPGAttributes.DAMAGE_SLASH)
        entity.attributeMap.registerAttribute(RPGAttributes.RESISTANCE_BLUNT)
        entity.attributeMap.registerAttribute(RPGAttributes.RESISTANCE_PIERCE)
        entity.attributeMap.registerAttribute(RPGAttributes.RESISTANCE_SLASH)
    }

    @SubscribeEvent
    fun onEntityJoinWorld(event: EntityJoinWorldEvent) {
        if (event.world.isRemote || event.entity !is EntityLivingBase) return

        val entity = event.entity as EntityLivingBase
        if (!entity.entityData.getBoolean("rpg_attributes_set")) {
            entity.entityData.setBoolean("rpg_attributes_set", true)

            if (entity is EntityPlayer) {
                entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).baseValue = 20.0
                entity.health = 20.0f
            }
        }
    }
}