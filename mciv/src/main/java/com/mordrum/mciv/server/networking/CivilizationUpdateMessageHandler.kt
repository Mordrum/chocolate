package com.mordrum.mciv.server.networking

import com.mordrum.mciv.common.networking.messages.CivilizationUpdateMessage
import com.mordrum.mciv.server.ServerAPIHelper
import com.mordrum.mcore.common.util.getPlayer
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import java.util.function.Consumer

class CivilizationUpdateMessageHandler : IMessageHandler<CivilizationUpdateMessage, IMessage> {
    override fun onMessage(message: CivilizationUpdateMessage, ctx: MessageContext): IMessage? {
        ServerAPIHelper.updateCivilization(message.civilization, Consumer { civilization ->
            ctx.getPlayer().sendMessage(TextComponentString("Changes saved"))
        }, Consumer { error ->
            //TODO more graceful error handling
        })
        return null
    }
}