package com.mordrum.mrpg.common.mixins

import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiIngame
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.util.math.MathHelper
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import java.util.*

@Mixin(GuiIngame::class)
abstract class MixinHealthGui : Gui() {
    @Shadow
    lateinit var rand: Random
    @Shadow
    lateinit var mc: Minecraft
    @Shadow
    var updateCounter: Int = 0
    @Shadow
    var playerHealth: Int = 0
    @Shadow
    var lastPlayerHealth: Int = 0
    @Shadow
    var lastSystemTime: Long = 0
    @Shadow
    var healthUpdateCounter: Long = 0

    @Overwrite
    protected fun renderPlayerStats(scaledRes: ScaledResolution) {
        System.out.println("Mixed")
        if (this.mc.renderViewEntity is EntityPlayer) {
            val player = this.mc.renderViewEntity as EntityPlayer
            val i = MathHelper.ceil(player.health)
            val flag = this.healthUpdateCounter > this.updateCounter.toLong() && (this.healthUpdateCounter - this.updateCounter.toLong()) / 3L % 2L == 1L

            if (i < this.playerHealth && player.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime()
                this.healthUpdateCounter = (this.updateCounter + 20).toLong()
            } else if (i > this.playerHealth && player.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime()
                this.healthUpdateCounter = (this.updateCounter + 10).toLong()
            }

            if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
                this.playerHealth = i
                this.lastPlayerHealth = i
                this.lastSystemTime = Minecraft.getSystemTime()
            }

            this.playerHealth = i
            val j = this.lastPlayerHealth
            this.rand.setSeed((this.updateCounter * 312871).toLong())
            val foodstats = player.foodStats
            val k = foodstats.foodLevel
            val iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            val l = scaledRes.scaledWidth / 2 - 91
            val i1 = scaledRes.scaledWidth / 2 + 91
            val j1 = scaledRes.scaledHeight - 39
            val f = iattributeinstance.attributeValue.toFloat()
            val k1 = MathHelper.ceil(player.absorptionAmount)
            val l1 = MathHelper.ceil((f + k1.toFloat()) / 2.0f / 10.0f)
            val i2 = Math.max(10 - (l1 - 2), 3)
            val j2 = j1 - (l1 - 1) * i2 - 10
            val k2 = j1 - 10
            var l2 = k1
            val i3 = player.totalArmorValue
            var j3 = -1

            if (player.isPotionActive(MobEffects.REGENERATION)) {
                j3 = this.updateCounter % MathHelper.ceil(f + 5.0f)
            }

            this.mc.mcProfiler.startSection("armor")

            for (k3 in 0..9) {
                if (i3 > 0) {
                    val l3 = l + k3 * 8

                    if (k3 * 2 + 1 < i3) {
                        this.drawTexturedModalRect(l3, j2, 34, 9, 9, 9)
                    }

                    if (k3 * 2 + 1 == i3) {
                        this.drawTexturedModalRect(l3, j2, 25, 9, 9, 9)
                    }

                    if (k3 * 2 + 1 > i3) {
                        this.drawTexturedModalRect(l3, j2, 16, 9, 9, 9)
                    }
                }
            }

            this.mc.mcProfiler.endStartSection("health")

            for (j5 in MathHelper.ceil((f + k1.toFloat()) / 2.0f) - 1 downTo 0) {
                var k5 = 16

                if (player.isPotionActive(MobEffects.POISON)) {
                    k5 += 36
                } else if (player.isPotionActive(MobEffects.WITHER)) {
                    k5 += 72
                }

                var i4 = 0

                if (flag) {
                    i4 = 1
                }

                val j4 = MathHelper.ceil((j5 + 1).toFloat() / 10.0f) - 1
                val k4 = l + j5 % 10 * 8
                var l4 = j1 - j4 * i2

                if (i <= 4) {
                    l4 += this.rand.nextInt(2)
                }

                if (l2 <= 0 && j5 == j3) {
                    l4 -= 2
                }

                var i5 = 0

                if (player.world.worldInfo.isHardcoreModeEnabled) {
                    i5 = 5
                }

                this.drawTexturedModalRect(k4, l4, 16 + i4 * 9, 9 * i5, 9, 9)

                if (flag) {
                    if (j5 * 2 + 1 < j) {
                        this.drawTexturedModalRect(k4, l4, k5 + 54, 9 * i5, 9, 9)
                    }

                    if (j5 * 2 + 1 == j) {
                        this.drawTexturedModalRect(k4, l4, k5 + 63, 9 * i5, 9, 9)
                    }
                }

                if (l2 > 0) {
                    if (l2 == k1 && k1 % 2 == 1) {
                        this.drawTexturedModalRect(k4, l4, k5 + 153, 9 * i5, 9, 9)
                        --l2
                    } else {
                        this.drawTexturedModalRect(k4, l4, k5 + 144, 9 * i5, 9, 9)
                        l2 -= 2
                    }
                } else {
                    if (j5 * 2 + 1 < i) {
                        this.drawTexturedModalRect(k4, l4, k5 + 36, 9 * i5, 9, 9)
                    }

                    if (j5 * 2 + 1 == i) {
                        this.drawTexturedModalRect(k4, l4, k5 + 45, 9 * i5, 9, 9)
                    }
                }
            }

            val entity = player.ridingEntity

            if (entity == null || entity !is EntityLivingBase) {
                this.mc.mcProfiler.endStartSection("food")

                for (l5 in 0..9) {
                    var j6 = j1
                    var l6 = 16
                    var j7 = 0

                    if (player.isPotionActive(MobEffects.HUNGER)) {
                        l6 += 36
                        j7 = 13
                    }

                    if (player.foodStats.saturationLevel <= 0.0f && this.updateCounter % (k * 3 + 1) == 0) {
                        j6 = j1 + (this.rand.nextInt(3) - 1)
                    }

                    val l7 = i1 - l5 * 8 - 9
                    this.drawTexturedModalRect(l7, j6, 16 + j7 * 9, 27, 9, 9)

                    if (l5 * 2 + 1 < k) {
                        this.drawTexturedModalRect(l7, j6, l6 + 36, 27, 9, 9)
                    }

                    if (l5 * 2 + 1 == k) {
                        this.drawTexturedModalRect(l7, j6, l6 + 45, 27, 9, 9)
                    }
                }
            }

            this.mc.mcProfiler.endStartSection("air")

            if (player.isInsideOfMaterial(Material.WATER)) {
                val i6 = this.mc.player.getAir()
                val k6 = MathHelper.ceil((i6 - 2).toDouble() * 10.0 / 300.0)
                val i7 = MathHelper.ceil(i6.toDouble() * 10.0 / 300.0) - k6

                for (k7 in 0..k6 + i7 - 1) {
                    if (k7 < k6) {
                        this.drawTexturedModalRect(i1 - k7 * 8 - 9, k2, 16, 18, 9, 9)
                    } else {
                        this.drawTexturedModalRect(i1 - k7 * 8 - 9, k2, 25, 18, 9, 9)
                    }
                }
            }

            this.mc.mcProfiler.endSection()
        }
    }
}