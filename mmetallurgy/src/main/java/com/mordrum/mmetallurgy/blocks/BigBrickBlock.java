package com.mordrum.mmetallurgy.blocks;

import com.mordrum.mmetallurgy.MMetallurgy;
import net.malisis.core.block.MalisisBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class BigBrickBlock extends MalisisBlock {

    public BigBrickBlock(String metal) {
        super(Material.IRON);

        this.setName(MMetallurgy.MOD_ID + "." + metal + ".brick.big");
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 1);
        this.setHardness(1.0f); // Time to mine
        this.setResistance(4.0f); // Explosion resistance
        this.setTexture(MMetallurgy.MOD_ID + ":blocks/bricks/" + metal + "_brick big bricks");

        if (MMetallurgy.bricksTab.getTabIconItem() == null) MMetallurgy.bricksTab.setTabIconItem(Item.getItemFromBlock(this));
        this.setCreativeTab(MMetallurgy.bricksTab);
    }

    @Override
    public Item getItemDropped(IBlockState metadata, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }
}

