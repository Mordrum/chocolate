package com.mordrum.mfarm.client.gui

import com.google.common.eventbus.Subscribe
import com.mordrum.mfarm.common.CommonProxy
import com.mordrum.mfarm.common.networking.BeginFermentationMessage
import com.mordrum.mfarm.common.tileentities.CaskState
import com.mordrum.mfarm.common.tileentities.CaskTileEntity
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
import net.malisis.core.util.TileEntityUtils
import java.util.*

class CaskGui(container: MalisisInventoryContainer, val tileEntity: CaskTileEntity) : MalisisGui() {
    var timerLabel: UILabel? = null

    init {
        setInventoryContainer(container)
        guiscreenBackground = false
        TileEntityUtils.linkTileEntityToGui(this.tileEntity, this)
    }

    override fun construct() {
        val window = UIWindow(this, 320, 200).setPosition(0, 0, Anchor.CENTER or Anchor.MIDDLE)
        window.setClipContent(false)
        val uiPlayerInventory = UIPlayerInventory(this, inventoryContainer.playerInventory)
        window.add(uiPlayerInventory)

        if (this.tileEntity.state == CaskState.FERMENTING) {
            this.timerLabel = UILabel(this, "Fermenting Complete in 12h").setAnchor(Anchor.CENTER).setPosition(0, 60 + 10)
            window.add(this.timerLabel)
        } else if (this.tileEntity.state == CaskState.EMPTY) {
            window.add(SlotContainer(this, inventoryContainer.getInventory(0)).setAnchor(Anchor.CENTER).setPosition(0, 10))
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

    override fun update(mouseX: Int, mouseY: Int, partialTick: Float) {
        if (this.timerLabel != null) {
            val completionTime = this.tileEntity.stateUpdatedAt + 12 * 60 * 60 * 1000
            val timeRemaining = completionTime - System.currentTimeMillis()
            if (timeRemaining <= 0) {
                this.timerLabel!!.text = "Fermentation Complete"
            } else {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timeRemaining
                this.timerLabel!!.text = "${calendar.get(Calendar.HOUR_OF_DAY)}h ${calendar.get(Calendar.MINUTE)}m ${calendar.get(Calendar.SECOND)}s remaining"
            }
        }
    }

    override fun updateGui() {
        clearScreen()
        construct()
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