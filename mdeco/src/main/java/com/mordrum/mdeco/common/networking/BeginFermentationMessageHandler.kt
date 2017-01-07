package com.mordrum.mdeco.common.networking

import com.mordrum.mdeco.common.CommonProxy
import com.mordrum.mdeco.common.tileentities.CaskState
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class BeginFermentationMessageHandler : IMessageHandler<BeginFermentationMessage, IMessage> {
    override fun onMessage(message: BeginFermentationMessage, ctx: MessageContext?): IMessage? {
        message.tileEntity.state = CaskState.FERMENTING
        message.tileEntity.markDirty()
        CommonProxy.NETWORK_WRAPPER.sendToAll(CaskStateChangedMessage(message.tileEntity, CaskState.FERMENTING))
        return null;
    }
}