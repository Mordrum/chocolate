package com.mordrum.mfarm.common.item;

import com.mordrum.mfarm.MFarm;
import net.minecraft.item.ItemFood;

public class ItemHeirloomWheat extends ItemFood {
    public ItemHeirloomWheat() {
        super(1, 0.6f, false);
        this.setUnlocalizedName(MFarm.MOD_ID + "_heirloom_wheat");
        this.setRegistryName(MFarm.MOD_ID, "heirloom_wheat");
    }
}
