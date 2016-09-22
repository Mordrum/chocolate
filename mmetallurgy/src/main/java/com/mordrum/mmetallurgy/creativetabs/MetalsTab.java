package com.mordrum.mmetallurgy.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class MetalsTab extends CreativeTabs {
    private Item item;

    public MetalsTab() {
        super("Metals");
    }

    @Override
    public Item getTabIconItem() {
        return item;
    }

    public void setTabIconItem(Item item) {
        this.item = item;
    }
}
