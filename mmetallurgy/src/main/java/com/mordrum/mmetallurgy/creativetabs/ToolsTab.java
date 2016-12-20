package com.mordrum.mmetallurgy.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class ToolsTab extends CreativeTabs {
    private Item item;

    public ToolsTab() {
        super("Tools");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(item);
    }

    public void setTabIconItem(Item item) {
        this.item = item;
    }
}
