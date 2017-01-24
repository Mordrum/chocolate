package com.mordrum.mciv.client.networking

import com.mordrum.mciv.client.gui.CivInvitedScreen
import com.mordrum.mciv.common.networking.messages.InviteRequestMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class InviteRequestHandler : IMessageHandler<InviteRequestMessage.Request, IMessage> {
    override fun onMessage(message: InviteRequestMessage.Request, ctx: MessageContext): IMessage? {
        CivInvitedScreen(message.message).display()
        return null
    }
}