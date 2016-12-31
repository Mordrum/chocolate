package com.mordrum.mfarm;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MFarm.MOD_ID, name = MFarm.MOD_NAME, version = MFarm.MOD_VERSION)
public class MFarm {
    public static final String MOD_ID = "mfarm";
    public static final String MOD_NAME = "mFarm";
    public static final String MOD_VERSION = "$VERSION";

    @Mod.Instance(MOD_ID)
    private static MFarm instance;

    @SidedProxy(clientSide = "com.mordrum.mfarm.client.ClientProxy", serverSide = "com.mordrum.mfarm.server.ServerProxy")
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
