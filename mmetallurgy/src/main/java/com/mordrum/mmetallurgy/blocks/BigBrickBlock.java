package com.mordrum.mmetallurgy.blocks;

import com.mordrum.mmetallurgy.MMetallurgy;
import net.malisis.core.block.MalisisBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class BigBrickBlock extends MalisisBlock {
    private final String name;

    public BigBrickBlock(String metal) {
        super(Material.IRON);
        this.name = MMetallurgy.MOD_ID + "." + metal + ".brick.big";

        register();

        if (MMetallurgy.bricksTab.getTabIconItem() == null) MMetallurgy.bricksTab.setTabIconItem(Item.getItemFromBlock(this));
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(MMetallurgy.bricksTab);
        this.setHarvestLevel("pickaxe", 1);
        this.setUnlocalizedName(this.name);
        this.setHardness(1.0f); // Time to mine
        this.setResistance(4.0f); // Explosion resistance
        this.setTexture(MMetallurgy.MOD_ID + ":blocks/bricks/" + metal + "_brick big bricks");
    }

    public String getName() {
        return name;
    }

    @Override
    public Item getItemDropped(IBlockState metadata, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }
}
