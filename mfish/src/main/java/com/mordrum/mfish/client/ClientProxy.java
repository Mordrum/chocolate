package com.mordrum.mfish.client;

import com.mordrum.mfish.CommonProxy;
import com.mordrum.mfish.MFish;
import com.mordrum.mfish.common.items.ItemFish;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
        super.onPreInit(event);
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);

//        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
//        for (String fishName : fishToRegister) {
//            ItemFish itemFish = new ItemFish(fishName);
//            renderItem.getItemModelMesher().register(itemFish, 0, new ModelResourceLocation(MFish.MOD_ID + ":" + itemFish.getName(), "inventory"));
//        }
    }
}
