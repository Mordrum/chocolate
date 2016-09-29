package com.mordrum.mmetallurgy.items;

import com.mordrum.mmetallurgy.MMetallurgy;
import net.malisis.core.item.MalisisItem;

public class Ingot extends MalisisItem {
	private boolean isAlloy = false;

	public Ingot(String name) {
		this.name = name + "_ingot";

		register();

		this.setCreativeTab(MMetallurgy.metalsTab);
		this.setUnlocalizedName(MMetallurgy.MOD_ID + "." + name + ".ingot");
		this.setTexture(MMetallurgy.MOD_ID + ":items/ingots/" + name + "_ingot");
	}

	public boolean isAlloy() {
		return isAlloy;
	}

	public void setAlloy(boolean alloy) {
		isAlloy = alloy;
	}
}
