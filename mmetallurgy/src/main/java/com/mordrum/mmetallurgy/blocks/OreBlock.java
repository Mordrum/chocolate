package com.mordrum.mmetallurgy.blocks;

import com.mordrum.mmetallurgy.MMetallurgy;
import net.malisis.core.block.MalisisBlock;
import net.malisis.core.util.clientnotif.ClientNotification;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class OreBlock extends MalisisBlock {
	private Item drop;
	private int maxQuantityDropped;

	public OreBlock(String name, int blockLevel) {
		super(Material.ROCK);
		//TODO use the MalisisBlock name field
//		this.name = name + "_ore";
		this.setName(name + "_ore");
		this.maxQuantityDropped = 1;

		if (MMetallurgy.oresTab.getTabIconItem() == null)
			MMetallurgy.oresTab.setTabIconItem(Item.getItemFromBlock(this));
		this.setCreativeTab(MMetallurgy.oresTab);
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("pickaxe", blockLevel);
		this.setUnlocalizedName(MMetallurgy.MOD_ID + "." + name + ".ore");
		this.setHardness(4.0f); // Time to mine
		this.setResistance(2.0f); // Explosion resistance
		this.setTexture(MMetallurgy.MOD_ID + ":blocks/ores/" + name + "_ore");
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
		super.neighborChanged(state, world, pos, neighborBlock, neighborPos);
	}

	@Override
	public Item getItemDropped(IBlockState metadata, Random random, int fortune) {
		if (drop != null) return drop;
		else return Item.getItemFromBlock(this);
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		return random.nextInt(maxQuantityDropped) + fortune;
	}

	public void setDrop(Item drop) {
		this.drop = drop;
	}

	public void setMaxQuantityDropped(int maxQuantityDropped) {
		this.maxQuantityDropped = maxQuantityDropped;
	}
}

