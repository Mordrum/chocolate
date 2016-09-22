package com.mordrum.mcore.client;

import com.mordrum.mcore.client.gui.InstallMusicGUI;
import com.mordrum.mcore.client.gui.MainMenuGUI;
import com.mordrum.mcore.client.gui.charactercreator.CustomizeApearanceGUI;
import com.mordrum.mcore.client.gui.charactercreator.NameEntryGUI;
import com.mordrum.mcore.common.CommonProxy;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ClientProxy extends CommonProxy {
	private InstallMusicGUI installMusicGUI;
	private MainMenuGUI mainMenuGUI;

	@Override
	public void onPreInit() {
		super.onPreInit();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		new MultiMineClient();
		installMusicGUI = new InstallMusicGUI();
		mainMenuGUI = new MainMenuGUI();
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		GuiScreen guiBeingOpened = event.getGui();

		if (guiBeingOpened instanceof GuiMainMenu) {
			event.setCanceled(true);
			mainMenuGUI.display();
//			if (AmbientMusicUtility.isMusicPresent()) mainMenuGUI.display();
//			else installMusicGUI.display();
		} else if (guiBeingOpened instanceof GuiMultiplayer) {
			event.setCanceled(true);
			mainMenuGUI.display();
		} else if (guiBeingOpened instanceof MainMenuGUI) {
//			if (CustomizeApearanceGUI.skinTexture == null) {
//				event.setCanceled(true);
//				new NameEntryGUI().display();
//			}
		}
	}

	@SubscribeEvent
	public void onFish(PlayerEvent event) {

	}
}
