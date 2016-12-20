package com.mordrum.mcore.common.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

public class ServerUtilities {
	public static void broadcastMessageToPlayers(ITextComponent... textComponents) {
		List<EntityPlayerMP> players = DimensionManager.getWorld(0).getMinecraftServer().getPlayerList().getPlayers();
		for (EntityPlayerMP entityPlayerMP : players) {
			for (ITextComponent iTextComponent : textComponents) {
				entityPlayerMP.sendMessage(iTextComponent);
			}
		}
	}
}
