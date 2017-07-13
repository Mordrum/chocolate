package com.mordrum.mfish.common

import com.mordrum.mfish.MFish
import com.mordrum.mfish.common.items.ItemFish
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemModelMesher
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

open class CommonProxy {
    open fun onPreInit(event: FMLPreInitializationEvent) {
        println("Pre Init")
        if (!event.suggestedConfigurationFile.exists()) {
            try {
                val inputStream = javaClass.getResourceAsStream("/default_config.conf")
                Files.copy(inputStream, event.suggestedConfigurationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        config = ConfigFactory.parseFile(event.suggestedConfigurationFile)
    }

    open fun onInit(event: FMLInitializationEvent) {
        println("Init")

        loadFish()
        //FIXME advancements
        //        Achievements.registerAchievements();
    }

    private fun loadFish() {
        var itemModelMesher: ItemModelMesher? = null
        if (FMLCommonHandler.instance().side == Side.CLIENT) {
            itemModelMesher = Minecraft.getMinecraft().renderItem.itemModelMesher
        }

        val fish = config.getConfigList("fish")
        for (fishConfig in fish) {
            val fishName = fishConfig.getString("name")
            val metadata = fishConfig.getInt("metadata")

            val environmentType = Fish.EnvironmentType.valueOf(fishConfig.getString("environment"))
            val rarity = fishConfig.getInt("rarity")
            Fish(fishName, environmentType, rarity, metadata)

            if (itemModelMesher != null) {
                itemModelMesher.register(rawItem, metadata, ModelResourceLocation(MFish.MOD_ID + ":" + fishName, "inventory"))
                itemModelMesher.register(cookedItem, metadata, ModelResourceLocation(MFish.MOD_ID + ":cooked_" + fishName, "inventory"))

                ModelLoader.setCustomModelResourceLocation(rawItem, metadata, ModelResourceLocation(MFish.MOD_ID + ":" + fishName, "inventory"))
                ModelLoader.setCustomModelResourceLocation(cookedItem, metadata, ModelResourceLocation(MFish.MOD_ID + ":cooked_" + fishName, "inventory"))
            }
        }
    }

    companion object {
        lateinit var config: Config
        var rawItem = ItemFish(false)
        var cookedItem = ItemFish(true)

        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item>) {
            event.registry.registerAll(rawItem, cookedItem)
        }
    }
}
