package com.mordrum.mfish.common.mixins

import com.mordrum.mfish.server.FishingLootGenerator
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.item.EntityXPOrb
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityFishHook
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow

@Mixin(EntityFishHook::class)
abstract class MixinEntityFishHook(worldIn: World) : Entity(worldIn) {
    @Shadow
    private val ticksCatchable: Int = 0
    @Shadow
    private val inGround: Boolean = false
    @Shadow
    private val caughtEntity: Entity? = null
    @Shadow
    private val angler: EntityPlayer? = null

    @Overwrite
    fun handleHookRetraction(): Int {
        if (this.world.isRemote) {
            return 0
        } else {
            var i = 0

            if (this.caughtEntity != null) {
                this.bringInHookedEntity()
                this.world.setEntityState(this, 31.toByte())
                i = if (this.caughtEntity is EntityItem) 3 else 5
            } else if (this.ticksCatchable > 0) {
                val itemStack = FishingLootGenerator.getFishingLoot(angler!!, this)

                val entityitem = EntityItem(this.world, this.posX, this.posY, this.posZ, itemStack)
                val d0 = this.angler.posX - this.posX
                val d1 = this.angler.posY - this.posY
                val d2 = this.angler.posZ - this.posZ
                val d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2).toDouble()
                entityitem.motionX = d0 * 0.1
                entityitem.motionY = d1 * 0.1 + MathHelper.sqrt(d3).toDouble() * 0.08
                entityitem.motionZ = d2 * 0.1
                this.world.spawnEntity(entityitem)
                this.angler.world.spawnEntity(EntityXPOrb(this.angler.world, this.angler.posX, this.angler.posY + 0.5, this.angler.posZ + 0.5, this.rand.nextInt(6) + 1))

                i = 1
            }

            if (this.inGround) {
                i = 2
            }

            this.setDead()
            this.angler!!.fishEntity = null
            return i
        }
    }

    @Shadow
    protected abstract fun bringInHookedEntity()
}
