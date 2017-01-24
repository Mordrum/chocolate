package com.mordrum.mciv.client.gui

import com.google.common.eventbus.Subscribe
import com.mordrum.mciv.common.CommonProxy
import com.mordrum.mciv.common.networking.messages.InviteRequestMessage
import com.mordrum.mcore.client.gui.ChatColor
import com.mordrum.mcore.client.gui.MordrumGui
import net.malisis.core.client.gui.Anchor
import net.malisis.core.client.gui.component.container.UIBackgroundContainer
import net.malisis.core.client.gui.component.decoration.UILabel
import net.malisis.core.client.gui.component.interaction.UIButton

class CivInvitedScreen(val message: String) : MordrumGui() {
    override fun construct() {
        val label = UILabel(this, ChatColor.WHITE + this.message)
                .setAnchor(Anchor.CENTER)
                .setPosition(0, 60)

        val acceptButton = UIButton(this, "Accept")
                .setName("accept")
                .setSize(60)
                .register(this)
        val denyButton = UIButton(this, "Deny")
                .setName("deny")
                .setSize(60)
                .setPosition(62, 0)
                .register(this)

        val buttonContainer = UIBackgroundContainer(this)
                .setAnchor(Anchor.CENTER)
                .setPosition(0, 60 + label.height + 2)
                .setSize(122, 20)
                .setBackgroundAlpha(0)
        buttonContainer.add(acceptButton, denyButton)

        this.addToScreen(label)
        this.addToScreen(buttonContainer)
    }

    @Subscribe
    fun onButtonClick(event: UIButton.ClickEvent) {
        if (event.component.name == "accept") {
            CommonProxy.NETWORK_WRAPPER.sendToServer(InviteRequestMessage.Response(true))
        } else if (event.component.name == "deny") {
            CommonProxy.NETWORK_WRAPPER.sendToServer(InviteRequestMessage.Response(false))
        }
        this.close()
    }
}