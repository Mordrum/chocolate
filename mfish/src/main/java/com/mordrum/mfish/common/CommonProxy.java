package com.mordrum.mfish.common;

import com.mordrum.mfish.MFish;
import com.mordrum.mfish.common.items.ItemFish;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Mod.EventBusSubscriber(modid = MFish.MOD_ID)
public class CommonProxy {
    public static Config config;
    public static ItemFish rawItem = new ItemFish(false);
    public static ItemFish cookedItem = new ItemFish(true);

    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("Pre Init");
        if (!event.getSuggestedConfigurationFile().exists()) {
            try {
                InputStream inputStream = getClass().getResourceAsStream("/default_config.conf");
                Files.copy(inputStream, event.getSuggestedConfigurationFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = ConfigFactory.parseFile(event.getSuggestedConfigurationFile());
    }

    public void onInit(FMLInitializationEvent event) {
        System.out.println("Init");

        loadFish();
        //FIXME advancements
//        Achievements.registerAchievements();
    }

    private void loadFish() {
        ItemModelMesher itemModelMesher = null;
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        }

        List<? extends Config> fish = config.getConfigList("fish");
        for (Config fishConfig : fish) {
            String fishName = fishConfig.getString("name");
            int metadata = fishConfig.getInt("metadata");

            Fish.EnvironmentType environmentType = Fish.EnvironmentType.valueOf(fishConfig.getString("environment"));
            int rarity = fishConfig.getInt("rarity");
            new Fish(fishName, environmentType, rarity, metadata);

            if (itemModelMesher != null) {
                itemModelMesher.register(rawItem, metadata, new ModelResourceLocation(MFish.MOD_ID + ":" + fishName, "inventory"));
                itemModelMesher.register(cookedItem, metadata, new ModelResourceLocation(MFish.MOD_ID + ":cooked_" + fishName, "inventory"));

                ModelLoader.setCustomModelResourceLocation(rawItem, metadata, new ModelResourceLocation(MFish.MOD_ID + ":" + fishName, "inventory"));
                ModelLoader.setCustomModelResourceLocation(cookedItem, metadata, new ModelResourceLocation(MFish.MOD_ID + ":cooked_" + fishName, "inventory"));
            }
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(rawItem, cookedItem);
    }
}
