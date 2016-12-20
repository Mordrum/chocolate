package com.mordrum.mciv.client.gui;

import com.google.common.eventbus.Subscribe;
import com.mordrum.mciv.common.Civilization;
import com.mordrum.mciv.common.CommonProxy;
import com.mordrum.mciv.common.networking.ClientAPIHelper;
import com.mordrum.mciv.common.networking.messages.CreateCivMessage;
import com.mordrum.mcore.client.gui.ChatColor;
import com.mordrum.mcore.client.gui.MordrumGui;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UITextField;
import net.malisis.core.client.gui.event.ComponentEvent;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.math.RayTraceResult;

import java.util.Arrays;
import java.util.Optional;

public class CivScreen extends MordrumGui {
	private UIBackgroundContainer detailsContainer;
	private String civName = "";

	@Override
	public void construct() {
		// Determine if we have a civ or not

		UILabel titleLabel = new UILabel(this, ChatColor.WHITE + "Your Civilization");
		titleLabel.setAnchor(Anchor.CENTER | Anchor.TOP);
		titleLabel.setPosition(0, 10);

		ClientAPIHelper.getPlayerCivilization(Minecraft.getMinecraft().player.getUniqueID(), (civilization -> {
			if (civilization.isPresent()) {
				//TODO migrate this to a separate screen, details / settings view
				Civilization playerCiv = civilization.get();
				UILabel infoLabel = new UILabel(this, ChatColor.WHITE + "You are part of " + playerCiv.getName());
				infoLabel.setAnchor(Anchor.CENTER | Anchor.TOP);
				infoLabel.setPosition(0, getPaddedY(titleLabel));
				this.addToScreen(infoLabel);
			} else {
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

				this.addToScreen(create);
				this.addToScreen(join);
			}
		}));

		this.addToScreen(titleLabel);
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
