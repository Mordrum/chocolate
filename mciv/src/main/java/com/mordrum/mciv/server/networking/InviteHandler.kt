package com.mordrum.mciv.server.networking

import com.mordrum.mciv.common.CommonProxy
import com.mordrum.mciv.common.models.Player
import com.mordrum.mciv.common.networking.messages.InvitePlayerMessage
import com.mordrum.mciv.common.networking.messages.InviteRequestMessage
import com.mordrum.mciv.server.ServerAPIHelper
import com.mordrum.mcore.client.gui.ChatColor
import com.mordrum.mcore.common.util.getPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import java.util.*
import java.util.function.BiConsumer

object InviteHandler {
    val pendingInvitesMap: MutableMap<UUID, Player> = HashMap()

    // When the inviter sends an invite to another player
    class InvitePlayer : IMessageHandler<InvitePlayerMessage.Request, IMessage> {
        //TODO replace this with a ROBUST mail system that allows for offline inviting + magic
        override fun onMessage(message: InvitePlayerMessage.Request, ctx: MessageContext): IMessage? {
            val player = ctx.getPlayer()
            val playerToInvite = ctx.serverHandler.playerEntity.server!!.playerList.getPlayerByUsername(message.playerToInvite)

            if (playerToInvite == null) {
                CommonProxy.NETWORK_WRAPPER.sendTo(InvitePlayerMessage.Response(ChatColor.RED + message.playerToInvite + " is not online"), player as EntityPlayerMP)
            } else {
                ServerAPIHelper.getPlayer(player.uniqueID, listOf("civilization"), BiConsumer { error, response ->
                    if (error != null) {
                        CommonProxy.Companion.NETWORK_WRAPPER.sendTo(InvitePlayerMessage.Response(ChatColor.RED + "An error: " + error.message), player as EntityPlayerMP)
                    } else if (response.civilization == null) {
                        CommonProxy.Companion.NETWORK_WRAPPER.sendTo(InvitePlayerMessage.Response(ChatColor.RED + "You are not part of a civilization"), player as EntityPlayerMP)
                    } else if (playerToInvite.uniqueID == player.uniqueID) {
                        CommonProxy.Companion.NETWORK_WRAPPER.sendTo(InvitePlayerMessage.Response(ChatColor.RED + "You cannot invite yourself"), player as EntityPlayerMP)
                    } else {
                        // Keep track of the invite in our map, then send out the packets to let each player know what's going on
                        pendingInvitesMap[playerToInvite.uniqueID] = response
                        CommonProxy.Companion.NETWORK_WRAPPER.sendTo(InviteRequestMessage.Request(player.name + " is inviting you to join the civilization of " + response.civilization!!.primaryColor + response.civilization!!.name), playerToInvite)
                        CommonProxy.Companion.NETWORK_WRAPPER.sendTo(InvitePlayerMessage.Response(ChatColor.BLACK + "Invite sent"), player as EntityPlayerMP)
                    }
                })
            }

            return null
        }
    }

    // When the invited responds to the invite
    class ResponseHandler : IMessageHandler<InviteRequestMessage.Response, IMessage> {
        override fun onMessage(message: InviteRequestMessage.Response, ctx: MessageContext): IMessage? {
            val player = ctx.getPlayer()
            val playerModel = pendingInvitesMap[player.uniqueID]
            if (playerModel == null) {
                // No pending invite, explode here
            } else if (message.accepted) {
                ServerAPIHelper.addPlayerToCivilization(player.uniqueID, playerModel.civilization!!.id, BiConsumer { error, response ->
                    if (error == null) {
                        for (p in player.server!!.playerList.players) {
                            p.sendMessage(TextComponentString(player.name + " has joined the civilization of " + playerModel.civilization!!.name))
                        }
                    } else {
                        player.sendMessage(TextComponentString(ChatColor.RED + "An error occurred while accepting the invite: ${error.message}"))
                    }
                })
            } else {
                // Get the player who sent the invite, let them know it was rejected
                val invitingPlayer = player.server!!.playerList.getPlayerByUUID(playerModel.uuid)
                invitingPlayer.sendMessage(TextComponentString(ChatColor.RED + player.name + " rejected your invite"))
            }

            return null
        }

    }
}