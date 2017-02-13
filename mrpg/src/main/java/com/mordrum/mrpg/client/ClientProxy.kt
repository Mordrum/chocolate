package com.mordrum.mrpg.client

import com.mordrum.mrpg.client.gui.HUDOverlayGui
import com.mordrum.mrpg.common.CommonProxy
import net.malisis.core.client.gui.MalisisGui
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ClientProxy : CommonProxy() {
    private val DISABLE_VANILLA_HUD = true
    private lateinit var overlay: MalisisGui

    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)

        if (DISABLE_VANILLA_HUD) {
            GuiIngameForge.renderArmor = false
            GuiIngameForge.renderHealth = false
            GuiIngameForge.renderFood = false
            GuiIngameForge.renderExperiance = false
        }

        this.overlay = HUDOverlayGui()

        val itemModelMesher = Minecraft.getMinecraft().renderItem.itemModelMesher
        this.items.forEach { item ->
            itemModelMesher.register(item, 0, ModelResourceLocation(item.registryName, "inventory"))
            ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName, "inventory"))
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        if (!DISABLE_VANILLA_HUD) return
        this.overlay.displayOverlay()
    }

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent.Pre) {
        if (!DISABLE_VANILLA_HUD) return

        when (event.type) {
            RenderGameOverlayEvent.ElementType.ARMOR,
            RenderGameOverlayEvent.ElementType.HEALTH,
            RenderGameOverlayEvent.ElementType.FOOD,
            RenderGameOverlayEvent.ElementType.AIR,
            RenderGameOverlayEvent.ElementType.HEALTHMOUNT -> {
                event.isCanceled = true
            }
            else -> {
                // Do nothing
            }
        }
    }
}