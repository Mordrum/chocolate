package com.mordrum.mciv.client;

import com.mordrum.mciv.client.gui.CivScreen;
import com.mordrum.mciv.common.CommonProxy;
import com.mordrum.mciv.common.networking.messages.ChunkClaimMessage;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {
	private KeyBinding keyBinding;
	private CivScreen civScreen;
	private boolean interactDebounce = false;

	@Override
	public void onPreInit(FMLPreInitializationEvent event) {
		super.onPreInit(event);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onInit(FMLInitializationEvent event) {
		super.onInit(event);

		keyBinding = new KeyBinding("Civ Screen", Keyboard.KEY_R, "key.categories.mordrum");
		ClientRegistry.registerKeyBinding(keyBinding);
	}

	@SubscribeEvent
	public void onKeyboardEvent(InputEvent.KeyInputEvent event) {
		if (keyBinding.isPressed()) {
			if (civScreen != null) {
				civScreen.close();
				civScreen = null;
			} else {
				civScreen = new CivScreen();
				civScreen.display();
			}
		}
	}

	@SubscribeEvent
	public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		populateCaches();
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		if (event.getItemStack() != null && event.getItemStack().getItem().equals(Items.BOOK)) {
			BlockPos pos = event.getEntityPlayer().getPosition();

			// Make sure the chunk isn't already occupied
			if (getChunkCache().containsKey(pos.getX() >> 4)) {
				if (getChunkCache().get(pos.getX() >> 4).containsKey(pos.getZ() >> 4)) {
					long civilizationID = getChunkCache().get(pos.getX() >> 4).get(pos.getZ() >> 4);
					event.getEntityPlayer().sendMessage(new TextComponentString("This chunk has already been claimed by civilization #" + civilizationID));
					return;
				}
			}

			CommonProxy.NETWORK_WRAPPER.sendToServer(new ChunkClaimMessage.Request());
		}
	}
}
