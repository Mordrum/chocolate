package com.mordrum.mciv.client.networking

import com.mordrum.mciv.common.CommonProxy
import com.mordrum.mciv.common.networking.messages.InvitePlayerMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class InvitePlayerResultHandler : IMessageHandler<InvitePlayerMessage.Response, IMessage> {
    override fun onMessage(message: InvitePlayerMessage.Response, ctx: MessageContext): IMessage? {
        CommonProxy.bus.post(message)

        return null
    }

}