package com.mordrum.mciv.client.gui;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.mordrum.mciv.client.ClientAPIHelper;
import com.mordrum.mcore.client.gui.ChatColor;
import com.mordrum.mcore.client.gui.MordrumGui;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public class CivScreen extends MordrumGui {
	private UIBackgroundContainer detailsContainer;
	private String civName = "";

	@Override
	public void construct() {
		// Determine if we have a civ or not

		UILabel titleLabel = new UILabel(this, ChatColor.WHITE + "Your Civilization");
		titleLabel.setAnchor(Anchor.CENTER | Anchor.TOP);
		titleLabel.setPosition(0, 10);

		UUID uuid = Minecraft.getMinecraft().player.getUniqueID();

		ClientAPIHelper.INSTANCE.getPlayer(uuid, Lists.newArrayList("civilization"), ((err, player) -> {
			if (err == null) {
				if (player == null || player.getCivilization() == null) {
					// Player not in database or has no civ
					UIButton create = new UIButton(this, "Create")
							.setName("civ.create")
							.setSize(80)
							.setAnchor(Anchor.CENTER | Anchor.TOP)
							.setPosition(0, getPaddedY(titleLabel, 20))
							.register(this);
					UIButton join = new UIButton(this, "Join")
							.setName("civ.join")
							.setSize(80)
							.setAnchor(Anchor.CENTER | Anchor.TOP)
							.setPosition(0, getPaddedY(create))
							.register(this);

					this.addToScreen(titleLabel);
					this.addToScreen(create);
					this.addToScreen(join);
				} else {
					// Civilization found, load info screen
					new CivInfoScreen(player.getCivilization()).display();
				}
			} else {
				UILabel infoLabel = new UILabel(this, ChatColor.RED + "An error occurred, please try again later");
				infoLabel.setAnchor(Anchor.CENTER | Anchor.TOP);
				infoLabel.setPosition(0, getPaddedY(titleLabel));
				this.addToScreen(infoLabel);
			}
		}));
	}

	@Subscribe
	public void onButtonClick(UIButton.ClickEvent event) {
		String buttonID = event.getComponent().getName().toLowerCase();
		if (buttonID.equals("civ.create")) {
			new CreateCivScreen().display();
		} else if (buttonID.equals("civ.join")) {
			new JoinCivScreen().display();
		}
	}
}
