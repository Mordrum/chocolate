package com.mordrum.mfish

import com.mordrum.mfish.common.CommonProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = MFish.MOD_ID, name = MFish.MOD_NAME, version = MFish.MOD_VERSION, dependencies = "required-after:malisiscore;required-after:mcore")
class MFish {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        proxy.onPreInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.onInit(event)
    }

    companion object {
        const val MOD_NAME = "mFish"
        const val MOD_ID = "mfish"
        const val MOD_VERSION = "\$VERSION"

        @Mod.Instance(MOD_ID)
        private lateinit var instance: MFish

        @SidedProxy(clientSide = "com.mordrum.mfish.client.ClientProxy", serverSide = "com.mordrum.mfish.server.ServerProxy")
        private lateinit var proxy: CommonProxy
    }
}
