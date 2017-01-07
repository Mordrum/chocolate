package com.mordrum.mdeco.client.gui

import com.google.common.eventbus.Subscribe
import com.mordrum.mdeco.common.CommonProxy
import com.mordrum.mdeco.common.networking.BeginFermentationMessage
import com.mordrum.mdeco.common.tileentities.CaskState
import com.mordrum.mdeco.common.tileentities.CaskTileEntity
import net.malisis.core.client.gui.Anchor
import net.malisis.core.client.gui.MalisisGui
import net.malisis.core.client.gui.component.UISlot
import net.malisis.core.client.gui.component.container.UIContainer
import net.malisis.core.client.gui.component.container.UIPlayerInventory
import net.malisis.core.client.gui.component.container.UIWindow
import net.malisis.core.client.gui.component.decoration.UILabel
import net.malisis.core.client.gui.component.interaction.UIButton
import net.malisis.core.inventory.MalisisInventory
import net.malisis.core.inventory.MalisisInventoryContainer

class CaskGui(container: MalisisInventoryContainer, val tileEntity: CaskTileEntity) : MalisisGui() {
    init {
        setInventoryContainer(container)
        guiscreenBackground = false
    }

    override fun construct() {
        val window = UIWindow(this, 320, 200).setPosition(0, 0, Anchor.CENTER or Anchor.MIDDLE)
        window.setClipContent(false)
        val uiPlayerInventory = UIPlayerInventory(this, inventoryContainer.playerInventory)
        window.add(uiPlayerInventory)
        window.add(SlotContainer(this, inventoryContainer.getInventory(0)).setAnchor(Anchor.CENTER).setPosition(0, 10))

        if (this.tileEntity.state == CaskState.FERMENTING) {
            window.add(UILabel(this, "Fermenting Complete in 12h").setAnchor(Anchor.CENTER).setPosition(0, 60 + 10))
        } else if (this.tileEntity.state == CaskState.EMPTY) {
            window.add(UIButton(this, "Begin Fermentation").setAnchor(Anchor.CENTER).setPosition(0, 60 + 10).setName("begin").register(this))
        }
        addToScreen(window)
    }

    @Subscribe
    fun onButtonClick(event: UIButton.ClickEvent) {
        if (event.component.name == "begin") {
            CommonProxy.NETWORK_WRAPPER.sendToServer(BeginFermentationMessage(this.tileEntity))
        }
    }

    class SlotContainer(gui: MalisisGui, val inventory: MalisisInventory) : UIContainer<SlotContainer>(gui) {
        init {
            val baseX = 0
            for (i in 0..8) {
                val slot = inventory.getSlot(i)
                this.add(UISlot(gui, slot).setPosition(baseX + (i % 3 * 18), i / 3 * 18).setTooltip("Slot number ${i + 1}"))
            }

            val slot = inventory.getSlot(9)
            this.add(UISlot(gui, slot).setPosition(baseX + (2 * 18 + 54), 1 * 18))
            this.width = this.contentWidth
            this.height = this.contentHeight
        }
    }
}