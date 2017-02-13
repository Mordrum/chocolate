package com.mordrum.mrpg.common.items

import com.google.common.collect.Multimap
import com.mordrum.mrpg.MRPG
import com.mordrum.mrpg.common.RPGAttributes
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

abstract class AbstractWeapon(val material: String, val type: String, val attackSpeed: Double, val criticalChance: Double, val damageBlunt: Double, val damagePierce: Double, val damageSlash: Double, val sweepRange: Double) : Item() {
    companion object {
        val CRITICAL_CHANCE_MODIFIER: UUID = UUID.fromString("243637f3-8036-4499-9a01-ae5d3c010069")
        val DAMAGE_BLUNT_MODIFIER: UUID = UUID.fromString("f75e7f90-5f42-4dfc-ae4e-6d3d3df67c11")
        val DAMAGE_PIERCE_MODIFIDER: UUID = UUID.fromString("3c67e69d-fd78-465e-bd88-76643a6cc540")
        val DAMAGE_SLASH_MODIFIER: UUID = UUID.fromString("cf540465-6f01-4030-98a4-4a95e4840639")
        val SWEEP_RANGE_MODIFIER: UUID = UUID.fromString("eaee5d0c-8d96-4ef7-9be7-0aa07f7e9034")
    }


    init {
        this.creativeTab = CreativeTabs.COMBAT
        this.maxStackSize = 1
        this.unlocalizedName = "${MRPG.MOD_ID}.$material.$type"
    }

    override fun getAttributeModifiers(slot: EntityEquipmentSlot?, stack: ItemStack?): Multimap<String, AttributeModifier> {
        val attributeModifiers = super.getAttributeModifiers(slot, stack)

        if (slot == EntityEquipmentSlot.MAINHAND) {
            attributeModifiers.put(SharedMonsterAttributes.ATTACK_SPEED.name, AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, 0))
            attributeModifiers.put(RPGAttributes.CRITICAL_CHANCE.name, AttributeModifier(CRITICAL_CHANCE_MODIFIER, "Weapon modifier", this.criticalChance, 0))
            attributeModifiers.put(RPGAttributes.DAMAGE_BLUNT.name, AttributeModifier(DAMAGE_BLUNT_MODIFIER, "Weapon modifier", this.damageBlunt, 0))
            attributeModifiers.put(RPGAttributes.DAMAGE_PIERCE.name, AttributeModifier(DAMAGE_PIERCE_MODIFIDER, "Weapon modifier", this.damagePierce, 0))
            attributeModifiers.put(RPGAttributes.DAMAGE_SLASH.name, AttributeModifier(DAMAGE_SLASH_MODIFIER, "Weapon modifier", this.damageSlash, 0))
            if (sweepRange > 0.0) {
                attributeModifiers.put(RPGAttributes.SWEEP_RANGE.name, AttributeModifier(SWEEP_RANGE_MODIFIER, "Weapon modifier", this.sweepRange, 0))
            }
        }

        return attributeModifiers
    }

    override fun hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean {
        stack.damageItem(1, attacker)
        return true
    }

    override fun onBlockDestroyed(stack: ItemStack, worldIn: World, state: IBlockState, pos: BlockPos, entityLiving: EntityLivingBase): Boolean {
        if (state.getBlockHardness(worldIn, pos).toDouble() != 0.0) {
            stack.damageItem(2, entityLiving)
        }

        return true
    }

    override fun canHarvestBlock(blockIn: IBlockState): Boolean {
        return blockIn.block === Blocks.WEB && damageSlash > damageBlunt && damageSlash > damagePierce
    }

    override fun getStrVsBlock(stack: ItemStack, state: IBlockState): Float {
        if (damageSlash < damageBlunt || damageSlash < damagePierce) return 1.0f

        val block = state.block

        if (block === Blocks.WEB) {
            return 15.0f
        } else {
            val material = state.material
            return if (material !== Material.PLANTS && material !== Material.VINE && material !== Material.CORAL && material !== Material.LEAVES && material !== Material.GOURD) 1.0f else 1.5f
        }
    }

    @SideOnly(Side.CLIENT)
    override fun isFull3D(): Boolean {
        return true
    }

    override fun getIsRepairable(toRepair: ItemStack, repair: ItemStack): Boolean {
        val mat = Item.ToolMaterial.valueOf(this.material.toUpperCase()).repairItemStack
        if (!mat.isEmpty && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true
        return super.getIsRepairable(toRepair, repair)
    }

    override fun onLeftClickEntity(stack: ItemStack?, player: EntityPlayer, entity: Entity?): Boolean {
        return player.isSwingInProgress
    }

    override fun onEntitySwing(entityLiving: EntityLivingBase, stack: ItemStack?): Boolean {
        return entityLiving.isSwingInProgress
    }

    open fun register(): AbstractWeapon {
        this.setRegistryName(MRPG.MOD_ID, "$type.$material")
        GameRegistry.register(this)
        return this
    }
}
