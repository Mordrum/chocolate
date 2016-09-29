package com.mordrum.mfish;

import com.mordrum.mfish.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MFish.MOD_ID, name = MFish.MOD_NAME, version = MFish.MOD_VERSION)
public class MFish {
    public static final String MOD_ID = "mfish";
    public static final String MOD_NAME = "mFish";
    public static final String MOD_VERSION = "1.0";

    @Mod.Instance(MOD_ID)
    private static MFish instance;

    @SidedProxy(clientSide = "com.mordrum.mfish.client.ClientProxy", serverSide = "com.mordrum.mfish.server.ServerProxy")
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.onPreInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.onInit(event);
    }
}
