package com.mordrum.mcore.client.gui;

import com.google.common.eventbus.Subscribe;
import com.mordrum.mcore.common.query.ServerPinger;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.GuiTexture;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UIImage;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.renderer.font.FontRenderOptions;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.net.UnknownHostException;
import java.util.Calendar;

public class MainMenuGUI extends PanoramaBackgroundGUI {
	private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("mcore", "textures/gui/mordrum.png");

	@Override
	public void construct() {
		constructButtonsContainer();
		constructServerStats();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}

	private void constructServerStats() {
		ServerPinger serverPinger = new ServerPinger();
		UILabel statsLabel = new UILabel(this);

		try {
			ServerData serverData = new ServerData("mordrum", "play.mordrum.com", false);
			serverPinger.ping(serverData);
			statsLabel.setText(TextFormatting.AQUA + "Players Online: " + TextFormatting.RED + serverData.populationInfo);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			statsLabel.setText("Error: " + e.getMessage());
		}

		statsLabel.setAnchor(Anchor.RIGHT | Anchor.MIDDLE);
		this.addToScreen(statsLabel);
	}

	private void constructButtonsContainer() {
		UIBackgroundContainer buttonsContainer = new UIBackgroundContainer(this);
		buttonsContainer.setAnchor(Anchor.LEFT);
		buttonsContainer.setBackgroundAlpha(0);

		UILabel titleLabel = new UILabel(this, ChatColor.WHITE + "Welcome to Mordrum");
		titleLabel.setPosition(0, 5, Anchor.CENTER | Anchor.TOP);

		UIImage logoImage = new UIImage(this, new GuiTexture(LOGO_LOCATION), null);
		logoImage.setAnchor(Anchor.CENTER | Anchor.TOP);
		logoImage.setPosition(0, this.getPaddedY(titleLabel, 12));
		logoImage.setSize(64, 64);

		UIButton multiplayerButton = new UIButton(this, ChatColor.AQUA + "Connect to Mordrum");
		multiplayerButton.setSize(180);
		multiplayerButton.setPosition(0, this.getPaddedY(logoImage, 12), Anchor.CENTER | Anchor.TOP);
		multiplayerButton.setName("button.multiplayer");
		multiplayerButton.register(this);

		UIButton optionsButton = new UIButton(this, "Options");
		optionsButton.setSize(180);
		optionsButton.setPosition(0, this.getPaddedY(multiplayerButton, 4), Anchor.CENTER | Anchor.TOP);
		optionsButton.setName("button.options");
		optionsButton.register(this);

		UIButton quitButton = new UIButton(this, "Quit");
		quitButton.setSize(50);
		quitButton.setName("button.quit");
		quitButton.register(this);

		String environment = System.getProperty("environment");
		if ((environment != null && environment.equalsIgnoreCase("development"))) {
			UIButton singlePlayerButton = new UIButton(this, "Single Player");
			singlePlayerButton.setSize(180);
			singlePlayerButton.setPosition(0, this.getPaddedY(optionsButton, 4), Anchor.LEFT | Anchor.TOP);
			singlePlayerButton.setName("button.singleplayer");
			singlePlayerButton.register(this);
			buttonsContainer.add(singlePlayerButton);

			quitButton.setPosition(0, this.getPaddedY(singlePlayerButton, 14), Anchor.CENTER | Anchor.TOP);
		} else {
			quitButton.setPosition(0, this.getPaddedY(optionsButton, 14), Anchor.CENTER | Anchor.TOP);
		}

		FontRenderOptions fro = new FontRenderOptions();
		fro.fontScale = 0.7f;

		UILabel copyrightLabel = new UILabel(this, ChatColor.DARK_GRAY + "Copyright Mordrum 2011 - " + Calendar.getInstance().get(Calendar.YEAR));
		copyrightLabel.setPosition(0, -9, Anchor.CENTER | Anchor.BOTTOM);
		copyrightLabel.setFontRenderOptions(fro);

		UILabel trademarkLabel = new UILabel(this, ChatColor.DARK_GRAY + "Minecraft is a registered trademark of Mojang AB");
		trademarkLabel.setPosition(0, -1, Anchor.CENTER | Anchor.BOTTOM);
		trademarkLabel.setFontRenderOptions(fro);

		buttonsContainer.add(titleLabel, logoImage, multiplayerButton, optionsButton, quitButton, copyrightLabel, trademarkLabel);
		buttonsContainer.setSize(buttonsContainer.getContentWidth(), this.height);
		buttonsContainer.setPosition(10, 0);
		this.addToScreen(buttonsContainer);
	}

	@Subscribe
	public void onButtonClick(UIButton.ClickEvent event) {
		String buttonID = event.getComponent().getName().toLowerCase();
		if (buttonID.equalsIgnoreCase("button.multiplayer")) {
			FMLClientHandler.instance().setupServerList();

			String environment = System.getProperty("environment");
			if ((environment != null && environment.equalsIgnoreCase("development"))) {
				ServerData data = new ServerData("Mordrum", "localhost:25565", false);
				FMLClientHandler.instance().connectToServer(this, data);
			} else {
				ServerData data = new ServerData("Mordrum", "play.mordrum.com:25575", false);
				FMLClientHandler.instance().connectToServer(this, data);
			}
		} else if (buttonID.equalsIgnoreCase("button.quit")) {
			this.mc.shutdown();
		} else if (buttonID.equalsIgnoreCase("button.options")) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		} else if (buttonID.equalsIgnoreCase("button.singleplayer")) {
			this.mc.displayGuiScreen(new GuiWorldSelection(this));
		}
	}
}
