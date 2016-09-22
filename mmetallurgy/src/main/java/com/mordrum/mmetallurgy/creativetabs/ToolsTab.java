package com.mordrum.mmetallurgy.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

class ToolsTab extends CreativeTabs {
    private Item item;

    public ToolsTab() {
        super("Tools");
    }

    @Override
    public Item getTabIconItem() {
        return item;
    }

    public void setTabIconItem(Item item) {
        this.item = item;
    }
}
