package com.mordrum.mmetallurgy.blocks;

import com.mordrum.mmetallurgy.MMetallurgy;
import net.malisis.core.block.MalisisBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class OreBlock extends MalisisBlock {
	private Item drop;
	private int maxQuantityDropped;
	private final boolean isToxic;

	public OreBlock(String name, int blockLevel, boolean isToxic) {
		super(Material.ROCK);

		this.setName(name + "_ore");
		this.maxQuantityDropped = 1;
		this.isToxic = isToxic;
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("pickaxe", blockLevel);
		this.setUnlocalizedName(MMetallurgy.MOD_ID + "." + name + ".ore");
		this.setHardness(4.0f); // Time to mine
		this.setResistance(2.0f); // Explosion resistance
		this.setTexture(MMetallurgy.MOD_ID + ":blocks/ores/" + name + "_ore");

		if (MMetallurgy.oresTab.getTabIconItem() == null) MMetallurgy.oresTab.setTabIconItem(Item.getItemFromBlock(this));
		this.setCreativeTab(MMetallurgy.oresTab);
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
		return random.nextInt(maxQuantityDropped) + fortune + 1;
	}

	public void setDrop(Item drop) {
		this.drop = drop;
	}

	public void setMaxQuantityDropped(int maxQuantityDropped) {
		this.maxQuantityDropped = maxQuantityDropped;
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		super.onBlockClicked(worldIn, pos, playerIn);
		if (this.isToxic) {
			if (playerIn.getHealth() <= 0.5f) playerIn.setHealth(0.0f);
			playerIn.addPotionEffect(new PotionEffect(MobEffects.POISON, 20 * 10, 2));
			playerIn.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 20 * 10, 2));
			playerIn.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 20 * 10, 2));
		}
	}
}

