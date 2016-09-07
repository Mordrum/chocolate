package com.mordrum.mfish;

import com.google.common.collect.Lists;
import com.mordrum.mfish.common.Fish;
import com.mordrum.mfish.common.items.ItemFish;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class CommonProxy {
//    protected String[] fishToRegister = new String[]{"carp", "trout", "koi", "steed", "dace", "catfish", "chub", "bitterling", "loach", "bluegill", "bass", "snakehead", "eel", "goby", "smelt", "char", "stringfish", "goldfish", "guppy", "angelfish", "piranha", "arowana", "arapaima", "crawfish", "frog", "killifish", "jellyfish", "snapper", "knifejaw"};
    public static Config config;
    public static ItemFish rawItem;
    public static ItemFish cookedItem;

    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("Pre Init");
        if (!event.getSuggestedConfigurationFile().exists()) {
            try {
//                ConfigRenderOptions configRenderOptions = ConfigRenderOptions.defaults().setOriginComments(false).setComments(false).setJson(false);
//                System.out.println(ConfigFactory.empty().withValue("fish", ConfigValueFactory.fromIterable(Lists.asList(new Fish("carp", Fish.EnvironmentType.WARM, 10, 0), new Fish[]{}))).root().render(configRenderOptions));

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
    }

    private void loadFish() {
//        Arrays.sort(fishToRegister);
        ItemModelMesher itemModelMesher = null;
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        }

        rawItem = GameRegistry.register(new ItemFish(false));
        cookedItem = GameRegistry.register(new ItemFish(true));

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
}
