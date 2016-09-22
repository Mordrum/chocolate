package com.mordrum.mmetallurgy.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BricksTab extends CreativeTabs {
    private Item item;

    public BricksTab() {
        super("Bricks");
    }

    @Override
    public Item getTabIconItem() {
        return item;
    }

    public void setTabIconItem(Item item) {
        this.item = item;
    }
}
