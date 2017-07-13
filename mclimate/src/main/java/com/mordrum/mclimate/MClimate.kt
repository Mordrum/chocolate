package com.mordrum.mclimate

import com.mordrum.mclimate.common.CommonProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = MClimate.MOD_ID, name = MClimate.MOD_NAME, version = MClimate.MOD_VERSION)
class MClimate {

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        proxy.onPreInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.onInit(event)
    }

    companion object {
        const val MOD_ID = "mclimate"
        const val MOD_NAME = "mClimate"
        const val MOD_VERSION = "\$VERSION"

        @Mod.Instance(MOD_ID)
        private lateinit var instance: MClimate

        @SidedProxy(clientSide = "com.mordrum.mclimate.client.ClientProxy", serverSide = "com.mordrum.mclimate.server.ServerProxy")
        private lateinit var proxy: CommonProxy
    }
}
