package com.mordrum.mdeco.common.networking

import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class CaskStateChangedMessageHandler :IMessageHandler<CaskStateChangedMessage, IMessage> {
    override fun onMessage(message: CaskStateChangedMessage, ctx: MessageContext?): IMessage? {
        message.tileEntity.state = message.caskState
        message.tileEntity.markDirty()
        return null
    }
}