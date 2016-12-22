package com.mordrum.mcore.client.gui;

import com.google.common.eventbus.Subscribe;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.GuiTexture;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UIImage;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.renderer.font.FontOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.Calendar;

public class MainMenuGUI extends MordrumGui {
	private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("mcore", "textures/gui/freshlogo.png");
	private UIImage logoImage;
	private final String serverAddress;
	private UILabel populationLabel;
	private ServerData serverData;
	private ServerPinger pinger;
	private ResourceLocation backgroundTexture;

	public MainMenuGUI() {
		String environment = System.getProperty("environment");
		if ((environment != null && environment.equalsIgnoreCase("development"))) {
			serverAddress = "localhost:25565";
		} else {
			serverAddress = "modded.mordrum.com:25585";
		}
		this.guiscreenBackground = false;
	}

	@Override
	public void construct() {
		constructButtonsContainer();
		constructServerStats();
		backgroundTexture = new ResourceLocation("mcore", "textures/gui/background.png");
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTexture);
		int x = 0;
		int y = 0;
		drawCompleteImage(x, y, this.width, this.height);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public static void drawCompleteImage(int posX, int posY, int width, int height) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) posX, (float) posY, 0.0F);
		GL11.glBegin(7);
		GL11.glTexCoord2f(0.0F, 0.0F);
		GL11.glVertex3f(0.0F, 0.0F, 0.0F);
		GL11.glTexCoord2f(0.0F, 1.0F);
		GL11.glVertex3f(0.0F, (float) height, 0.0F);
		GL11.glTexCoord2f(1.0F, 1.0F);
		GL11.glVertex3f((float) width, (float) height, 0.0F);
		GL11.glTexCoord2f(1.0F, 0.0F);
		GL11.glVertex3f((float) width, 0.0F, 0.0F);
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	private void constructEventDetailsContainer() {
		UIBackgroundContainer eventContainer = new UIBackgroundContainer(this);
		eventContainer.setPosition(-20, 0);
		eventContainer.setSize(this.width/5*2, this.height/3*2);
		eventContainer.setAnchor(Anchor.RIGHT | Anchor.MIDDLE);
		this.addToScreen(eventContainer);

		UILabel eventTitle = new UILabel(this, "Current Event: Brave New World");

		UILabel eventDescription = new UILabel(this, true);
		eventDescription.setText(
				"Settlers from the Old World have finally made landfall on a new and exciting continent, full of surprises and adventure. " +
						"In an effort to learn more about this new land, the Council of Mordrum is offering bounties in exchange for discoveries.\n" +
						"To participate, unlock achievements"
		);
		eventDescription.setPosition(eventTitle.getX(), getPaddedY(eventTitle));
		eventDescription.setSize(eventContainer.getWidth(), eventContainer.getHeight() - eventDescription.getY());

		eventContainer.add(eventTitle, eventDescription);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}

	private void constructServerStats() {
		populationLabel = new UILabel(this);
		populationLabel.setText("Players Online: Loading...");

		try {
			serverData = new ServerData("mordrum", this.serverAddress, false);
			pinger = new ServerPinger();
			pinger.ping(serverData);
			pinger.pingPendingNetworks();
		} catch (IOException e) {
			e.printStackTrace();
		}

		populationLabel.setAnchor(Anchor.CENTER | Anchor.TOP);
		populationLabel.setPosition(0, getPaddedY(logoImage, 20));
		this.addToScreen(populationLabel);
	}

	private void constructButtonsContainer() {
		UIBackgroundContainer buttonsContainer = new UIBackgroundContainer(this);

		UILabel updateName = new UILabel(this, "Brave New World");
		updateName.setPosition(0, 20, Anchor.CENTER | Anchor.TOP);
		updateName.setFontOptions(new FontOptions.FontOptionsBuilder().color(Color.GREEN.getRGB()).scale(2.0f).build());

		logoImage = new UIImage(this, new GuiTexture(LOGO_LOCATION), null);
		logoImage.setAnchor(Anchor.CENTER | Anchor.TOP);
		logoImage.setPosition(0, 20);
//		logoImage.setSize(362, 64);
		logoImage.setSize(254, 45);
		this.addToScreen(logoImage);

		UIButton multiplayerButton = new UIButton(this, ChatColor.AQUA + "Play");
		multiplayerButton.setSize(180);
		multiplayerButton.setPosition(0, 120, Anchor.CENTER | Anchor.TOP);
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
			singlePlayerButton.setPosition(0, this.getPaddedY(optionsButton, 4), Anchor.CENTER | Anchor.TOP);
			singlePlayerButton.setName("button.singleplayer");
			singlePlayerButton.register(this);
			buttonsContainer.add(singlePlayerButton);

			quitButton.setPosition(0, this.getPaddedY(singlePlayerButton, 14), Anchor.CENTER | Anchor.TOP);
		} else {
			quitButton.setPosition(0, this.getPaddedY(optionsButton, 14), Anchor.CENTER | Anchor.TOP);
		}


		FontOptions fro = new FontOptions.FontOptionsBuilder()
				.scale(0.7f)
				.build();

		UILabel copyrightLabel = new UILabel(this,
				ChatColor.DARK_GRAY + "Copyright Mordrum 2011 - " + Calendar.getInstance().get(Calendar.YEAR));
		copyrightLabel.setPosition(0, -12, Anchor.CENTER | Anchor.BOTTOM);
		copyrightLabel.setFontOptions(fro);

		UILabel trademarkLabel = new UILabel(this,
				ChatColor.DARK_GRAY + "Minecraft is a registered trademark of Mojang AB");
		trademarkLabel.setPosition(0, -4, Anchor.CENTER | Anchor.BOTTOM);
		trademarkLabel.setFontOptions(fro);

		buttonsContainer.add(multiplayerButton, optionsButton, quitButton, copyrightLabel, trademarkLabel);
		buttonsContainer.setSize(180, this.height);
		buttonsContainer.setAnchor(Anchor.CENTER);
		buttonsContainer.setBackgroundAlpha(0);
//		buttonsContainer.setPosition((int) (this.width * -0.25), 0);


		this.addToScreen(buttonsContainer);
	}

	@Override
	public void update(int mouseX, int mouseY, float partialTick) {
		super.update(mouseX, mouseY, partialTick);
		pinger.pingPendingNetworks();

		int playersOnline = 0;
		if (serverData.playerList != null) {
			playersOnline = serverData.playerList.split(" ").length;
			if (playersOnline > 2) playersOnline -= 1;
		}
		populationLabel.setText(TextFormatting.AQUA + "Players Online: " + TextFormatting.RED + playersOnline);
	}

	@Subscribe
	public void onButtonClick(UIButton.ClickEvent event) {
		String buttonID = event.getComponent().getName().toLowerCase();
		if (buttonID.equalsIgnoreCase("button.multiplayer")) {
			FMLClientHandler.instance().setupServerList();

			ServerData data = new ServerData("Mordrum", this.serverAddress, false);
			FMLClientHandler.instance().connectToServer(this, data);
		} else if (buttonID.equalsIgnoreCase("button.quit")) {
			this.mc.shutdown();
		} else if (buttonID.equalsIgnoreCase("button.options")) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		} else if (buttonID.equalsIgnoreCase("button.singleplayer")) {
			this.mc.displayGuiScreen(new GuiWorldSelection(this));
		}
	}

}
