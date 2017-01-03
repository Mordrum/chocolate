package com.mordrum.mdeco.common.block

import com.mordrum.mdeco.client.renderers.CaskRenderer
import com.mordrum.mdeco.common.tileentities.CaskTileEntity
import net.malisis.core.block.MalisisBlock
import net.malisis.core.block.component.DirectionalComponent
import net.malisis.core.inventory.MalisisInventory
import net.malisis.core.renderer.DefaultRenderer
import net.malisis.core.renderer.MalisisRendered
import net.malisis.core.util.TileEntityUtils
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@MalisisRendered(block = CaskRenderer::class, item = DefaultRenderer.Block::class)
class Cask : MalisisBlock(Material.IRON), ITileEntityProvider {
    init {
        this.setName("cask")
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.DECORATIONS)
        this.setTexture(Blocks.LOG)
        this.addComponent(DirectionalComponent())
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


}