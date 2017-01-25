package com.mordrum.mciv.client.gui

import com.google.common.eventbus.Subscribe
import com.mordrum.mciv.common.CommonProxy
import com.mordrum.mciv.common.models.Civilization
import com.mordrum.mciv.common.networking.messages.CivilizationUpdateMessage
import com.mordrum.mciv.common.networking.messages.InvitePlayerMessage
import com.mordrum.mcore.client.gui.ChatColor
import com.mordrum.mcore.client.gui.ChatColor.BLACK
import com.mordrum.mcore.client.gui.MordrumGui
import com.mordrum.mcore.client.util.getBottom
import net.malisis.core.client.gui.Anchor
import net.malisis.core.client.gui.component.UIComponent
import net.malisis.core.client.gui.component.container.UIBackgroundContainer
import net.malisis.core.client.gui.component.container.UIContainer
import net.malisis.core.client.gui.component.control.UIScrollBar
import net.malisis.core.client.gui.component.decoration.UILabel
import net.malisis.core.client.gui.component.interaction.UIButton
import net.malisis.core.client.gui.component.interaction.UITextField
import java.text.SimpleDateFormat
import java.util.*

class CivInfoScreen(val civilization: Civilization) : MordrumGui() {
    val panels = mapOf(Pair("info", ::InfoPanel), Pair("invite", ::InvitePanel), Pair("edit", ::EditPanel))
//    var currentPanel: UIContainer<*> = panels["info"]!!.invoke(this)
    var currentPanel: UIBackgroundContainer = panels["info"]!!.invoke(this)

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
                this.setSize(50 + 150 + 10, 160)
                this.setPosition(0, getPaddedY(nameLabel, 12))

                buttonsContainer.setPosition(0, 0)
                buttonsContainer.setSize(50, 20 * panels.size)
                this.add(buttonsContainer)

                currentPanel.setPosition(50, 0)
                currentPanel.setSize(150, 160)
                this.add(currentPanel)

                if (currentPanel.contentHeight > currentPanel.height) {
                    val uiScrollBar = UIScrollBar(this.gui, currentPanel, UIScrollBar.Type.VERTICAL)
//                    this.add(uiScrollBar)
//                    uiScrollBar.parent = currentPanel
                }
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

    class EditPanel(val gui: CivInfoScreen) : UIBackgroundContainer(gui) {
        val civilizationNameField: UITextField
        val welcomeMessageField: UITextField
        val motdField: UITextField
        val tagField: UITextField
        val descriptionField: UITextField
        var primaryColor = gui.civilization.primaryColor
        var secondaryColor = gui.civilization.secondaryColor

        init {
            this.setBackgroundAlpha(100)

            val civilizationNameLabel = UILabel(gui, BLACK + "Civilization Name")
                    .setPosition(2, 2)
            civilizationNameField = UITextField(gui, gui.civilization.name, false)
                    .setSize(this.width - 10 - 4, 12)
                    .setPosition(2, civilizationNameLabel.getBottom() + 2)
            this.add(civilizationNameLabel, civilizationNameField)

            val welcomeMessageLabel = UILabel(gui, BLACK + "Welcome Message")
                    .setPosition(2, civilizationNameField.getBottom() + 2)
            welcomeMessageField = UITextField(gui, gui.civilization.welcomeMessage, false)
                    .setSize(this.width - 10 - 4, 12)
                    .setPosition(2, welcomeMessageLabel.getBottom() + 2)
            this.add(welcomeMessageLabel, welcomeMessageField)

            val motdLabel = UILabel(gui, BLACK + "Message of the Day")
                    .setPosition(2, welcomeMessageField.getBottom() + 2)
            motdField = UITextField(gui, gui.civilization.motd, false)
                    .setSize(this.width - 10 - 4, 12)
                    .setPosition(2, motdLabel.getBottom() + 2)
            this.add(motdLabel, motdField)

            val tagLabel = UILabel(gui, BLACK + "Civilization Tag")
                    .setPosition(2, motdField.getBottom() + 2)
            tagField = UITextField(gui, gui.civilization.tag, false)
                    .setSize(this.width - 10 - 4, 12)
                    .setPosition(2, tagLabel.getBottom() + 2)
            this.add(tagLabel, tagField)

            val descriptionLabel = UILabel(gui, BLACK + "Description")
                    .setPosition(2, tagField.getBottom() + 2)
            descriptionField = UITextField(gui, gui.civilization.description, true)
                    .setSize(this.width - 10 - 4, 36)
                    .setPosition(2, descriptionLabel.getBottom() + 2)
            this.add(descriptionLabel, descriptionField)

            val primaryColorLabel = UILabel(gui, BLACK + "Primary Color")
                    .setPosition(2, descriptionField.getBottom() + 2)
            val primaryColorButton = UIButton(gui, gui.civilization.primaryColor + gui.civilization.primaryColor.name.toLowerCase().capitalize().replace("_", " "))
                    .register(this)
                    .setName("primary")
                    .setAutoSize(false)
                    .setSize(80, 15)
                    .setPosition(2, primaryColorLabel.getBottom() + 2)
            this.add(primaryColorLabel, primaryColorButton)

            val secondaryColorLabel = UILabel(gui, BLACK + "Secondary Color")
                    .setPosition(2, primaryColorButton.getBottom() + 2)
            val secondaryColorButton = UIButton(gui, gui.civilization.secondaryColor + gui.civilization.secondaryColor.name.toLowerCase().capitalize().replace("_", " "))
                    .register(this)
                    .setName("secondary")
                    .setAutoSize(false)
                    .setSize(80, 15)
                    .setPosition(2, secondaryColorLabel.getBottom() + 2)
            this.add(secondaryColorLabel, secondaryColorButton)

            val saveButton = UIButton(gui, "Save Changes")
                    .register(this)
                    .setName("save")
                    .setSize(80, 15)
                    .setPosition(2, secondaryColorButton.getBottom() + 4)
            this.add(saveButton)
        }

        @Subscribe
        fun onButtonClick(event: UIButton.ClickEvent) {
            val name = event.component.name
            val colors = ChatColor.values()

            if (name == "primary") {
                if (primaryColor.ordinal >= 15) primaryColor = colors[0]
                else primaryColor = colors[primaryColor.ordinal + 1]
                event.component.text = primaryColor + primaryColor.name.toLowerCase().capitalize().replace("_", " ")
            } else if (name == "secondary") {
                if (secondaryColor.ordinal >= 15) secondaryColor = colors[0]
                else secondaryColor = colors[secondaryColor.ordinal + 1]
                event.component.text = secondaryColor + secondaryColor.name.toLowerCase().capitalize().replace("_", " ")
            } else if (name == "save") {
                gui.civilization.name = civilizationNameField.text
                gui.civilization.welcomeMessage = welcomeMessageField.text
                gui.civilization.motd = motdField.text
                gui.civilization.tag = tagField.text
                gui.civilization.description = descriptionField.text
                gui.civilization.primaryColor = primaryColor
                gui.civilization.secondaryColor = secondaryColor
                CommonProxy.NETWORK_WRAPPER.sendToServer(CivilizationUpdateMessage(gui.civilization))
                gui.close()
            }
        }
    }
}