package com.mordrum.mcore.client.util

import net.malisis.core.client.gui.component.UIComponent

fun UIComponent<*>.getBottom(): Int {
    return this.getY() + this.getHeight()
}