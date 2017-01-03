package com.mordrum.mdeco.common.tileentities

import com.mordrum.mdeco.client.gui.CaskGui
import net.malisis.core.client.gui.MalisisGui
import net.malisis.core.inventory.IInventoryProvider
import net.malisis.core.inventory.MalisisInventory
import net.malisis.core.inventory.MalisisInventoryContainer
import net.malisis.core.inventory.MalisisSlot
import net.malisis.core.util.MBlockState
import net.malisis.core.util.syncer.Syncable
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.text.ITextComponent
import java.util.function.Supplier

@Syncable("TileEntity")
class CaskTileEntity : TileEntity(), IInventoryProvider.IDirectInventoryProvider {
    private val inv: MalisisInventory
    var ringsState: IBlockState = Blocks.IRON_BLOCK.defaultState
    var barrelState: IBlockState = Blocks.PLANKS.defaultState
    var feetState: IBlockState = Blocks.PLANKS.defaultState

    init {
        class adhocSlot : MalisisSlot()
        this.inv = MalisisInventory(this, Supplier { adhocSlot() }, 3)
        this.ringsState = Blocks.IRON_BLOCK.defaultState
        this.barrelState = Blocks.PLANKS.defaultState
        this.feetState = Blocks.PLANKS.defaultState
    }

    override fun getGui(container: MalisisInventoryContainer): MalisisGui {
        return CaskGui(container)
    }

    override fun getInventory(): MalisisInventory {
        return inv
    }

    override fun getDisplayName(): ITextComponent? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun markDirty() {
        super<TileEntity>.markDirty()
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        inv.readFromNBT(compound)

        readCaskStateFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        inv.writeToNBT(compound)

        MBlockState.toNBT(compound, ringsState, "rings_block", "rings_meta")
        MBlockState.toNBT(compound, barrelState, "barrel_block", "barrel_meta")
        MBlockState.toNBT(compound, feetState, "feet_block", "feet_meta")

        return compound
    }

    fun readCaskStateFromNBT(compound: NBTTagCompound) {
        ringsState = MBlockState.fromNBT(compound, "rings_block", "rings_meta")
        barrelState = MBlockState.fromNBT(compound, "barrel_block", "barrel_meta")
        feetState = MBlockState.fromNBT(compound, "feet_block", "feet_meta")
    }
}