package com.mordrum.mmetallurgy.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class OresTab extends CreativeTabs {
    private Item item;

    public OresTab() {
        super("Ores");
    }

    @Override
    public Item getTabIconItem() {
        return item;
    }

    public void setTabIconItem(Item item) {
        this.item = item;
    }
}
