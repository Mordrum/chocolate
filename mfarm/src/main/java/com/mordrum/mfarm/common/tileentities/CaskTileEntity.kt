package com.mordrum.mfarm.common.tileentities

import com.mordrum.mfarm.client.gui.CaskGui
import net.malisis.core.client.gui.MalisisGui
import net.malisis.core.inventory.IInventoryProvider
import net.malisis.core.inventory.MalisisInventory
import net.malisis.core.inventory.MalisisInventoryContainer
import net.malisis.core.util.MBlockState
import net.malisis.core.util.TileEntityUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTUtil
import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.ITickable
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

class CaskTileEntity : TileEntity(), IInventoryProvider.IDirectInventoryProvider, ITickable {
    private val inventory: MalisisInventory
    var ringsState: IBlockState
    var barrelState: IBlockState
    var feetState: IBlockState

    var state: CaskState = CaskState.EMPTY
        set(value) {
            field = value
            this.stateUpdatedAt = System.currentTimeMillis()
        }
    var stateUpdatedAt: Long
    var ticksTicked: Long
    var itemsFermenting: NonNullList<ItemStack>

    val rand: Random = Random()


    init {
        this.inventory = MalisisInventory(this, 10)
        this.inventory.inventoryStackLimit = 1
        this.inventory.getSlot(9).setOutputSlot()
        this.ringsState = Blocks.IRON_BLOCK.defaultState
        this.barrelState = Blocks.PLANKS.defaultState
        this.feetState = Blocks.PLANKS.defaultState
        this.stateUpdatedAt = 0
        this.ticksTicked = 0
        this.itemsFermenting = NonNullList.withSize(9, ItemStack.EMPTY)
    }

    @SideOnly(Side.CLIENT)
    override fun getGui(container: MalisisInventoryContainer): MalisisGui {
        return CaskGui(container, this)
    }

    override fun getInventory(): MalisisInventory {
        return inventory
    }

    override fun getDisplayName(): ITextComponent? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun markDirty() {
        super<TileEntity>.markDirty()
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        inventory.readFromNBT(compound)

        readCaskStateFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        inventory.writeToNBT(compound)

        MBlockState.toNBT(compound, ringsState, "rings_block", "rings_meta")
        MBlockState.toNBT(compound, barrelState, "barrel_block", "barrel_meta")
        MBlockState.toNBT(compound, feetState, "feet_block", "feet_meta")
        compound.setString("cask_state", state.toString())
        compound.setLong("updated_at", stateUpdatedAt)

        itemsFermenting.forEachIndexed { i, itemStack ->
            val tag = NBTTagCompound()
            itemStack.writeToNBT(tag)
            compound.setTag("fermenting_item_" + i, tag)
        }

        return compound
    }

    fun readCaskStateFromNBT(compound: NBTTagCompound) {
        ringsState = MBlockState.fromNBT(compound, "rings_block", "rings_meta")
        barrelState = MBlockState.fromNBT(compound, "barrel_block", "barrel_meta")
        feetState = MBlockState.fromNBT(compound, "feet_block", "feet_meta")
        state = CaskState.valueOf(compound.getString("cask_state"))
        stateUpdatedAt = compound.getLong("updated_at")

        for (i in 0 .. 9) {
            val key = "fermenting_item_" + i
            if (!compound.hasKey(key)) break

            itemsFermenting[i] = ItemStack(compound.getTag(key) as NBTTagCompound)
        }
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.serializeNBT()
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        return SPacketUpdateTileEntity(this.pos, 0, this.serializeNBT())
    }

    override fun onDataPacket(networkManager: NetworkManager, pkt: SPacketUpdateTileEntity) {
        this.readFromNBT(pkt.nbtCompound)
        if (networkManager.direction == EnumPacketDirection.CLIENTBOUND) {
            TileEntityUtils.updateGui(this)
        }
    }

    override fun update() {
        ticksTicked++
        if (ticksTicked % 5 == 0L && this.state == CaskState.AGING) {
            val motionX = rand.nextGaussian() * 0.02
            val motionY = rand.nextGaussian() * 0.02
            val motionZ = rand.nextGaussian() * 0.02
            this.world.spawnParticle(
                    EnumParticleTypes.VILLAGER_HAPPY,
                    this.pos.x + rand.nextDouble(),
                    this.pos.y + rand.nextDouble(),
                    this.pos.z + rand.nextDouble(),
                    motionX,
                    motionY,
                    motionZ
            )
        }
    }

    fun beginFermentation() {
        if (this.inventory.itemStackList.isEmpty()) return

        this.state = CaskState.FERMENTING
        this.itemsFermenting = this.inventory.itemStackList
        this.inventory.itemStackList.clear()
    }
}

enum class CaskState {
    EMPTY, FERMENTING, AGING
}
