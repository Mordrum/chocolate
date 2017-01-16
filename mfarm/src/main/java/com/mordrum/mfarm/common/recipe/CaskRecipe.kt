package com.mordrum.mfarm.common.recipe

import com.mordrum.mfarm.common.CommonProxy
import com.mordrum.mfarm.common.block.Cask
import com.mordrum.mfarm.common.tileentities.CaskState
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
        val ringsState = CommonProxy.ringBlockItemToBlockMap[inv.getStackInRowAndColumn(0, 0).item]?.defaultState
        val barrelState = ItemUtils.getStateFromItemStack(inv.getStackInRowAndColumn(1, 0))
        val feetState = ItemUtils.getStateFromItemStack(inv.getStackInRowAndColumn(1, 2))

        //TODO find a better way to go about setting the default state, perhaps make a helper function on the cask tile entity
        val nbt = NBTTagCompound()

        MBlockState.toNBT(nbt, ringsState, "rings_block", "rings_meta")
        MBlockState.toNBT(nbt, barrelState, "barrel_block", "barrel_meta")
        MBlockState.toNBT(nbt, feetState, "feet_block", "feet_meta")
        nbt.setString("cask_state", CaskState.EMPTY.toString())
        nbt.setLong("updated_at", 0)

        val itemStack = ItemStack(this.cask)
        itemStack.tagCompound = nbt
        itemStack.setStackDisplayName(inv.getStackInRowAndColumn(0, 0).displayName.removeSuffix("Ingot").trim() + " Ringed " + inv.getStackInRowAndColumn(1, 0).displayName.removeSuffix("Wood Planks").trim() + " Cask")
        return itemStack
    }
}