package com.mordrum.mciv.common.listeners;

import com.mordrum.mciv.common.CommonProxy;
import com.mordrum.mcore.client.gui.ChatColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.vecmath.Tuple2i;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMoveListener {
	private final Map<UUID, Tuple2i> chunkPosMap;
	private long lastCiv;

	public PlayerMoveListener() {
		this.chunkPosMap = new HashMap<>();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerMove(LivingEvent.LivingUpdateEvent event) {
		// Short-circuit if this isn't a player
		if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

		EntityPlayer player = (EntityPlayer) event.getEntity();
		int currentChunkX = (int) player.posX >> 4;
		int currentChunkZ = (int) player.posZ >> 4;
		if (chunkPosMap.containsKey(player.getUniqueID())) {
			Tuple2i previousChunkPos = chunkPosMap.get(player.getUniqueID());
			if (previousChunkPos.getX() != currentChunkX || previousChunkPos.getY() != currentChunkZ) {
				if (CommonProxy.getChunkCache().containsKey(currentChunkX)&& CommonProxy.getChunkCache().get(currentChunkX).containsKey(currentChunkZ)) {
					long civId = CommonProxy.getChunkCache().get(currentChunkX).get(currentChunkZ);
					System.out.println("Player passing through new chunk, claimed by " + civId);
					if (lastCiv != civId) {
						if (CommonProxy.getCivilizationMap().containsKey(civId)) {
							lastCiv = civId;
							player.sendMessage(new TextComponentString(ChatColor.YELLOW + "~" + CommonProxy.getCivilizationMap().get(civId).getName() + "~"));
						} else {
							System.out.println("Civilization sync issue, refreshing caches");
							CommonProxy.populateCaches();
						}
					}
				} else {
					if (lastCiv != -1) {
						lastCiv = -1;
						player.sendMessage(new TextComponentString(ChatColor.DARK_GREEN + "~Wilderness~"));
					}
					System.out.println("Player passing through new, unclaimed chunk");
				}
			}
		}
		chunkPosMap.put(player.getUniqueID(), new Tuple2i(currentChunkX, currentChunkZ){});
	}
}
