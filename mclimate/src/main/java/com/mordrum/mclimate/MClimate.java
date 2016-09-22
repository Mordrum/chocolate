package com.mordrum.mclimate;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MClimate.MOD_ID, name = MClimate.MOD_NAME, version = MClimate.MOD_VERSION)
public class MClimate {
    public static final String MOD_ID = "mclimate";
    public static final String MOD_NAME = "mClimate";
    public static final String MOD_VERSION = "1.0";

    @Mod.Instance(MOD_ID)
    private static MClimate instance;

    @SidedProxy(clientSide = "com.mordrum.mclimate.client.ClientProxy", serverSide = "com.mordrum.mclimate.server.ServerProxy")
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
