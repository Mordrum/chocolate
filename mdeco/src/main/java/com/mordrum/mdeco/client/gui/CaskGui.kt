package com.mordrum.mdeco.client.gui

import net.malisis.core.client.gui.MalisisGui
import net.malisis.core.inventory.MalisisInventoryContainer

class CaskGui(container: MalisisInventoryContainer) : MalisisGui() {
    init {
        setInventoryContainer(container)
    }
    override fun construct() {

    }
}