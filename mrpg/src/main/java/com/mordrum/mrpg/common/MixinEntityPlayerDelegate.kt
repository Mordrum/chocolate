package com.mordrum.mrpg.common

import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.stats.StatList
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumHand
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.event.ForgeEventFactory

object MixinEntityPlayerDelegate {
    fun attackTargetEntityWithCurrentItem(targetEntity: Entity, _this: EntityPlayer) {
        // Post the event, short circuit method if it's cancelled
        if (!ForgeHooks.onPlayerAttackTarget(_this, targetEntity)) return

        // If this entity is not alive, use the Vanilla method instead of our mixin's
        if (targetEntity !is EntityLivingBase) return

        // Check to see if the attack against the target entity is valid
        if (!targetEntity.canBeAttackedWithItem() || targetEntity.hitByEntity(_this)) return

        // Return if the player attacked too soon, otherwise reset the cooldown timer
        if (_this.getCooledAttackStrength(0.5f) < 1.0f) return
        else _this.resetCooldown()

        // The raw damage amount for this attack, we will modify this to calculate the final damage done to the target
        var attackPower = calculateDamageAgainstEntity(_this, targetEntity)

        // Check if this attack will sweep across multiple enemies
        val sweepRange = _this.getEntityAttribute(RPGAttributes.SWEEP_RANGE).attributeValue
        if (sweepRange > 0.0) performSweepingAttack(_this, sweepRange, attackPower, targetEntity)

        // If the player performs a sprint or falling strike, double the damage
        val isSprintStrike =_this.isSprinting
        val isFallingStrike = _this.fallDistance > 6.0f && !_this.onGround && !_this.isOnLadder && !_this.isInWater && !_this.isRiding
        if (isSprintStrike || isFallingStrike) {
            attackPower *= 2.0f
            _this.world.playSound(null, _this.posX, _this.posY, _this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, _this.soundCategory, 1.0f, 1.0f)
        }

        val criticalChance = _this.getEntityAttribute(RPGAttributes.CRITICAL_CHANCE).attributeValue
        if (criticalChance > Math.random() * 100) {
            attackPower *= 2.0f
            _this.world.playSound(null, _this.posX, _this.posY, _this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, _this.soundCategory, 1.0f, 1.0f)
        }

        // Store the target's health before damage, then damage the target
        val targetEntityHealth = targetEntity.health
        targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(_this), attackPower)

        // We've struck a player, change their velocity
        if (targetEntity is EntityPlayerMP && targetEntity.velocityChanged) {
            targetEntity.connection.sendPacket(SPacketEntityVelocity(targetEntity))
            targetEntity.velocityChanged = false
            targetEntity.motionX = _this.motionX
            targetEntity.motionY = _this.motionY
            targetEntity.motionZ = _this.motionZ
        }

        // Misc stuff
        _this.setLastAttacker(targetEntity)
        EnchantmentHelper.applyThornEnchantments(targetEntity, _this)
        val mainHeldItem = _this.heldItemMainhand
        if (!mainHeldItem.isEmpty) {
            val beforeHitCopy = mainHeldItem.copy()
            mainHeldItem.hitEntity(targetEntity, _this)

            if (mainHeldItem.isEmpty) {
                _this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY)
                ForgeEventFactory.onPlayerDestroyItem(_this, beforeHitCopy, EnumHand.MAIN_HAND)
            }
        }
        val damageDealt = targetEntityHealth - targetEntity.health
        _this.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10.0f))
    }

    private fun calculateDamageAgainstEntity(player: EntityPlayer, entity: EntityLivingBase): Float {
        val damageBlunt = player.getEntityAttribute(RPGAttributes.DAMAGE_BLUNT).attributeValue
        val damagePierce = player.getEntityAttribute(RPGAttributes.DAMAGE_PIERCE).attributeValue
        val damageSlash = player.getEntityAttribute(RPGAttributes.DAMAGE_SLASH).attributeValue
        var attackTotal = 0.0

        attackTotal += damageBlunt * (100 / (100 + entity.getEntityAttribute(RPGAttributes.RESISTANCE_BLUNT).attributeValue))
        attackTotal += damagePierce * (100 / (100 + entity.getEntityAttribute(RPGAttributes.RESISTANCE_PIERCE).attributeValue))
        attackTotal += damageSlash * (100 / (100 + entity.getEntityAttribute(RPGAttributes.RESISTANCE_SLASH).attributeValue))

        return attackTotal.toFloat()
    }

    private fun performSweepingAttack(player: EntityPlayer, sweepRange: Double, attackPower: Float, targetEntity: Entity) {
        val bb = targetEntity.entityBoundingBox.expand(sweepRange, 0.25, sweepRange)
        player.world.getEntitiesWithinAABB(EntityLivingBase::class.java, bb)
                .filter { it !== player && it !== targetEntity }
                .forEach { it.attackEntityFrom(DamageSource.causePlayerDamage(player), attackPower) }
    }
}