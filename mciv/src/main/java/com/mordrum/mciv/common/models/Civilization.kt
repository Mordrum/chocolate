package com.mordrum.mciv.common.models

import com.mordrum.mcore.client.gui.ChatColor
import java.util.*

class Civilization : SuperModel() {
    var name: String = ""
    var banner: String = ""
    var players: List<Player> = ArrayList()
    var inviteOnly: Boolean = false

    var founder: Player? = null

    var chunks: List<CivilizationChunk> = ArrayList()

    var primaryColor: ChatColor = ChatColor.YELLOW
    var secondaryColor: ChatColor = ChatColor.BLUE
    var tertiaryColor: ChatColor = ChatColor.GREEN
    // Short version of the civ name, shown in chat and other places
    var tag = ""
    // Shown when a non-citizen enters this civ's land
    var welcomeMessage = ""
    // Shown to all citizens upon login
    var motd = ""
    // Shown to anyone who looks at the civilization in a menu of some sort
    var description = ""
    // Allied civilizations
    var allies: List<Civilization> = ArrayList()
    // Civilizations this civ is at war with
    var enemies: List<Civilization> = ArrayList()
    // What this civ focuses on primarily
    var primaryFocus: Focus? = null
    var secondaryFocuses: Collection<Focus> = ArrayList()

    enum class Focus {
        MILITARY, TRADING, PRODUCTION, SCIENCE, EXPANSION
    }
}