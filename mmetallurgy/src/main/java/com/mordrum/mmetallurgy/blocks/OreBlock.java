package com.mordrum.mmetallurgy.blocks;

import com.mordrum.mmetallurgy.MMetallurgy;
import net.malisis.core.block.MalisisBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class OreBlock extends MalisisBlock {
	private final String name;
	private Item drop;

	public OreBlock(String name, int blockLevel) {
		super(Material.ROCK);
		this.name = name + "_ore";

		register();

		if (MMetallurgy.oresTab.getTabIconItem() == null) MMetallurgy.oresTab.setTabIconItem(Item.getItemFromBlock(this));
		this.setCreativeTab(MMetallurgy.oresTab);
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("pickaxe", blockLevel);
		this.setUnlocalizedName(MMetallurgy.MOD_ID + "." + name + ".ore");
		this.setHardness(4.0f); // Time to mine
		this.setResistance(2.0f); // Explosion resistance
		this.setTexture(MMetallurgy.MOD_ID + ":blocks/ores/" + name + "_ore");
	}

	public String getName() {
		return name;
	}

	@Override
	public Item getItemDropped(IBlockState metadata, Random random, int fortune) {
		if (drop != null) return drop;
		else return Item.getItemFromBlock(this);
	}

	public void setDrop(Item drop) {
		this.drop = drop;
	}
}

