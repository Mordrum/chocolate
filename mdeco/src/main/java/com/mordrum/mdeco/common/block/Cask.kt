package com.mordrum.mdeco.common.block

import com.mordrum.mdeco.client.renderers.CaskRenderer
import com.mordrum.mdeco.common.tileentities.CaskTileEntity
import net.malisis.core.MalisisCore
import net.malisis.core.block.MalisisBlock
import net.malisis.core.block.component.DirectionalComponent
import net.malisis.core.inventory.MalisisInventory
import net.malisis.core.renderer.DefaultRenderer
import net.malisis.core.renderer.MalisisRendered
import net.malisis.core.util.MBlockState
import net.malisis.core.util.TileEntityUtils
import net.malisis.core.util.syncer.Syncer
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.SideOnly
import net.malisis.core.block.component.ItemTransformComponent
import net.malisis.core.util.TransformBuilder
import net.minecraftforge.fml.relauncher.Side


@MalisisRendered(block = CaskRenderer::class, item = CaskRenderer::class)
class Cask : MalisisBlock(Material.IRON), ITileEntityProvider {
    init {
        this.setName("cask")
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.DECORATIONS)
        this.addComponent(DirectionalComponent())

        if (MalisisCore.isClient()) {
            this.addComponent(this.getTransform())

        }
    }

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        // Do nothing on the client
        if (world.isRemote) return true;

        val caskTE = world.getTileEntity(pos) as CaskTileEntity
        MalisisInventory.open(player as EntityPlayerMP, caskTE)

        return true;
    }

    override fun onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack)

        val tileEntity = TileEntityUtils.getTileEntity(CaskTileEntity::class.java, world, pos)
        if (tileEntity != null) {
            stack.tagCompound?.let { tileEntity.readCaskStateFromNBT(it) }
        }
    }

    override fun isSideSolid(base_state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, side: EnumFacing?): Boolean {
        return false
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return CaskTileEntity()
    }

    override fun isOpaqueCube(state: IBlockState?): Boolean {
        return false
    }

    override fun getPickBlock(state: IBlockState?, target: RayTraceResult?, world: World?, pos: BlockPos?, player: EntityPlayer?): ItemStack {
        val tileEntity = TileEntityUtils.getTileEntity(CaskTileEntity::class.java, world, pos)
        val itemStack = super.getPickBlock(state, target, world, pos, player)

        itemStack.tagCompound = NBTTagCompound()
        MBlockState.toNBT(itemStack.tagCompound, tileEntity.ringsState, "rings_block", "rings_meta")
        MBlockState.toNBT(itemStack.tagCompound, tileEntity.barrelState, "barrel_block", "barrel_meta")
        MBlockState.toNBT(itemStack.tagCompound, tileEntity.feetState, "feet_block", "feet_meta")

        return itemStack
    }

    @SideOnly(Side.CLIENT)
    private fun getTransform(): ItemTransformComponent {
        val gui = TransformBuilder().rotate(0f, -30f, 0f).scale(.75f).get()
        val firstPerson = TransformBuilder().rotate(0f, 135f, 0f).scale(0.2f).get()
        val thirdPerson = TransformBuilder().translate(0f, 0.155f, -0.1f).rotateAfter(75f, 100f, 0f).scale(0.25f).get()
        val fixed = TransformBuilder().translate(0f, -.2f, 0f).scale(0.4f).get()
        val ground = TransformBuilder().translate(0f, 0.3f, 0f).scale(0.20f).get()

        return ItemTransformComponent().thirdPerson(thirdPerson, thirdPerson)
                .firstPerson(firstPerson, firstPerson)
                .fixed(fixed)
                .gui(gui)
                .ground(ground)
    }
}