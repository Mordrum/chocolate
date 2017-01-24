package com.mordrum.mciv.client.gui

import com.google.common.eventbus.Subscribe
import com.mordrum.mciv.common.CommonProxy
import com.mordrum.mciv.common.models.Civilization
import com.mordrum.mciv.common.networking.messages.InvitePlayerMessage
import com.mordrum.mcore.client.gui.ChatColor.BLACK
import com.mordrum.mcore.client.gui.MordrumGui
import net.malisis.core.client.gui.Anchor
import net.malisis.core.client.gui.component.container.UIBackgroundContainer
import net.malisis.core.client.gui.component.container.UIContainer
import net.malisis.core.client.gui.component.decoration.UILabel
import net.malisis.core.client.gui.component.interaction.UIButton
import net.malisis.core.client.gui.component.interaction.UITextField
import java.text.SimpleDateFormat
import java.util.*

class CivInfoScreen(val civilization: Civilization) : MordrumGui() {
    val panels = mapOf(Pair("info", ::InfoPanel), Pair("invite", ::InvitePanel), Pair("edit", ::EditPanel))
    var currentPanel: UIContainer<*> = panels["info"]!!.invoke(this)

    override fun construct() {
        // Name of the civilization
        val nameLabel = UILabel(this, "${this.civilization.primaryColor}${this.civilization.name}")
        nameLabel.anchor = Anchor.CENTER or Anchor.TOP
        nameLabel.setPosition(0, 10)
        this.addToScreen(nameLabel)

        val buttonsContainer = ButtonsContainer(this, panels.keys)
        this.addToScreen(object : UIBackgroundContainer(this) {
            init {
                this.setBackgroundAlpha(0)
                this.anchor = Anchor.CENTER or Anchor.TOP
                this.setSize(50 + 150, 160)
                this.setPosition(0, getPaddedY(nameLabel, 12))

                buttonsContainer.setPosition(0, 0)
                buttonsContainer.setSize(50, 20 * panels.size)
                this.add(buttonsContainer)

                currentPanel.setPosition(50, 0)
                currentPanel.setSize(150, 160)
                this.add(currentPanel)
            }
        })

    }

    fun switchPanel(panelName: String) {
        val kClass = panels[panelName]
        currentPanel = kClass?.invoke(this)!!

        this.clearScreen()
        this.construct()
    }

    class ButtonsContainer(val gui: CivInfoScreen, buttonNames: Set<String>) : UIBackgroundContainer(gui) {
        init {
            buttonNames.forEachIndexed { i, buttonName ->
                val button = UIButton(gui, buttonName)
                button.setSize(50)
                button.setPosition(0, i * 20)
                button.name = buttonName.toLowerCase()
                button.text = buttonName.capitalize()
                button.register(this)
                this.add(button)
            }
        }

        @Subscribe
        fun onButtonClick(event: UIButton.ClickEvent) {
            gui.switchPanel(event.component.name)
        }
    }

    class InfoPanel(gui: CivInfoScreen) : UIBackgroundContainer(gui) {
        init {
            this.setBackgroundAlpha(100)

            val dateString = SimpleDateFormat("MM dd, yyyy").format(Date(gui.civilization.createdAt.time))
            this.add(UILabel(gui, BLACK + "Citizens: ${gui.civilization.players.size}").setPosition(2, 2))
            this.add(UILabel(gui, BLACK + "Territory: ${gui.civilization.chunks.size} chunks").setPosition(2, 12))
            this.add(UILabel(gui, BLACK + "Founded: $dateString").setPosition(2, 24))

            // Allies
            if (gui.civilization.allies.isEmpty()) {
                this.add(UILabel(gui, BLACK + "Allies: None").setPosition(2, 36))
            } else {
                this.add(UILabel(gui, BLACK + "Allies: ${gui.civilization.allies.size}").setPosition(2, 46))
            }

            // Whether or not the civilization is at war depends on if they have enemies or not
            val warring = if (gui.civilization.enemies.isNotEmpty()) "Yes" else "No"
            this.add(UILabel(gui, BLACK + "At War: $warring").setPosition(2, 48))
        }
    }

    class InvitePanel(gui: CivInfoScreen) : UIBackgroundContainer(gui) {
        val inviteResultLabel: UILabel
        val playerNameField: UITextField

        init {
            CommonProxy.bus.register(this)

            this.setBackgroundAlpha(100)
            this.add(UILabel(gui, BLACK + "Player to invite").setPosition(2, 2))
            this.playerNameField = UITextField(gui, "", false)
                    .setSize(this.width - 4, 12)
                    .setPosition(2, 12)
            this.add(playerNameField)

            val inviteButton = UIButton(gui, "Invite")
                    .setName("sendInvite")
                    .setPosition(0, 24 + 2)
                    .setSize(60)
                    .setAnchor(Anchor.CENTER)
                    .register(this)
            this.add(inviteButton)

            this.inviteResultLabel = UILabel(gui, "")
                    .setPosition(0, 50)
                    .setAnchor(Anchor.CENTER)
            this.add(inviteResultLabel)
        }

        @Subscribe
        fun onButtonClick(event: UIButton.ClickEvent) {
            val playerName = this.playerNameField.text
            CommonProxy.NETWORK_WRAPPER.sendToServer(InvitePlayerMessage.Request(playerName))
        }

        @Subscribe
        fun receivedServerReply(event: InvitePlayerMessage.Response) {
            this.inviteResultLabel.text = BLACK + event.message
        }
    }

    class EditPanel(gui: CivInfoScreen) : UIBackgroundContainer(gui) {
        init {
            this.setBackgroundAlpha(100)

            val playerNameField = UITextField(gui, "", false).setPosition(2, 2)

            this.add(playerNameField)
        }
    }
}