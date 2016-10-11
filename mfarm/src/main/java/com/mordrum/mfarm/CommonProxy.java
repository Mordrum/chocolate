package com.mordrum.mfarm;

import com.google.common.eventbus.Subscribe;
import com.mordrum.mfarm.common.GeneListener;
import com.mordrum.mfarm.common.GeneticsListener;
import com.mordrum.mfarm.common.block.BlockHeirloomWheat;
import com.mordrum.mfarm.events.EntityBreedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    public void onInit(FMLInitializationEvent event) {
        BlockHeirloomWheat blockHeirloomWheat = new BlockHeirloomWheat();
        GameRegistry.register(blockHeirloomWheat);

        ItemBlock itemBlock = new ItemBlock(blockHeirloomWheat);
        itemBlock.setUnlocalizedName(MFarm.MOD_ID + "heirloom_wheat");
        itemBlock.setRegistryName(MFarm.MOD_ID, "heirloom_wheat_item");
        GameRegistry.register(itemBlock);
        MinecraftForge.EVENT_BUS.register(new GeneticsListener());
        MinecraftForge.EVENT_BUS.register(new GeneListener());

//        ItemHeirloomWheat itemHeirloomWheat = new ItemHeirloomWheat();
//        GameRegistry.register(itemHeirloomWheat);

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
//            itemModelMesher.register(itemHeirloomWheat, 1, new ModelResourceLocation(MFarm.MOD_ID + ":wheat_m_1", "inventory"));

        }
    }

    @Subscribe
    public void onBreed(EntityBreedEvent event) {
    }
}
