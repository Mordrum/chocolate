package com.mordrum.mmetallurgy.client;

import com.mordrum.mmetallurgy.common.CommonProxy;
import com.mordrum.mmetallurgy.common.blocks.VolatileTNT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		this.armors.forEach((armor -> {
			itemModelMesher.register(armor, 0, new ModelResourceLocation(armor.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(armor, 0, new ModelResourceLocation(armor.getRegistryName(), "inventory"));
		}));

		this.items.forEach((item -> {
			itemModelMesher.register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}));
	}
}
