package com.mordrum.mfarm.block;

import com.mordrum.mfarm.MFarm;
import net.minecraft.block.BlockCrops;

public class BlockHeirloomWheat extends BlockCrops {
    public BlockHeirloomWheat() {
        super();
        this.setUnlocalizedName(MFarm.MOD_ID + "heirloom_wheat");
        this.setRegistryName(MFarm.MOD_ID, "heirloom_wheat");
    }
}
