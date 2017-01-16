package com.mordrum.mfarm.common.networking

import com.mordrum.mfarm.common.CommonProxy
import com.mordrum.mfarm.common.tileentities.CaskState
import net.malisis.core.util.TileEntityUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class BeginFermentationMessageHandler : IMessageHandler<BeginFermentationMessage, IMessage> {
    override fun onMessage(message: BeginFermentationMessage, ctx: MessageContext?): IMessage? {
        message.tileEntity.beginFermentation()
        message.tileEntity.markDirty()
        TileEntityUtils.notifyUpdate(message.tileEntity)
        return null;
    }
}