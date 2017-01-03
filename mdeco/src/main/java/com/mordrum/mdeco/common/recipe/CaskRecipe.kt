package com.mordrum.mdeco.common.recipe

import com.mordrum.mdeco.common.block.Cask
import net.malisis.core.util.ItemUtils
import net.malisis.core.util.MBlockState
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.ShapedRecipes
import net.minecraft.nbt.NBTTagCompound

class CaskRecipe(val cask: Cask, ringsBlock: ItemStack, barrelBlock: ItemStack, feetBlock: ItemStack) : ShapedRecipes(3, 3, arrayOf(
        ringsBlock,         barrelBlock, ringsBlock,
        ringsBlock,         barrelBlock, ringsBlock,
        ItemStack.EMPTY,    feetBlock,   ItemStack.EMPTY
), ItemStack(cask)) {

    override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
        val ringsState = ItemUtils.getStateFromItemStack(inv.getStackInRowAndColumn(0, 0))
        val barrelState = ItemUtils.getStateFromItemStack(inv.getStackInRowAndColumn(1, 0))
        val feetState = ItemUtils.getStateFromItemStack(inv.getStackInRowAndColumn(1, 2))

        val nbt = NBTTagCompound()

        MBlockState.toNBT(nbt, ringsState, "rings_block", "rings_meta")
        MBlockState.toNBT(nbt, barrelState, "barrel_block", "barrel_meta")
        MBlockState.toNBT(nbt, feetState, "feet_block", "feet_meta")

        val itemStack = ItemStack(this.cask)
        itemStack.tagCompound = nbt
        return itemStack
    }
}