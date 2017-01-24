package com.mordrum.mciv.client.gui;

import com.google.common.eventbus.Subscribe;
import com.mordrum.mciv.common.CommonProxy;
import com.mordrum.mciv.common.models.Civilization;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;

import java.util.Optional;

public class CreateCivScreen extends MordrumGui {
	private String civName;

	@Override
	public void construct() {
		boolean bannerUnique = false;

		// Screen title
		UILabel titleLabel = new UILabel(this, ChatColor.WHITE + "Create New Civilization");
		titleLabel.setAnchor(Anchor.CENTER | Anchor.TOP);
		titleLabel.setPosition(0, 10);

		// Container that will hold everything
		UIBackgroundContainer detailsContainer = new UIBackgroundContainer(this);
		detailsContainer.setAnchor(Anchor.CENTER | Anchor.TOP);
		detailsContainer.setPosition(0, getPaddedY(titleLabel));
		detailsContainer.setBackgroundAlpha(0);
		detailsContainer.setSize(340, 140);


		// Check to make sure the player has enough gold blocks
		boolean playerHasThreeGoldBlocks = playerHasEnoughGold(Minecraft.getMinecraft().player, 3);

		// Check if the player is far enough away from the nearest civ
		boolean isPlayerTooClose = false;

		// Determine if the player is looking at a banner and if it is unique
		Optional<String> bannerFromRayTrace = getBannerFromRayTrace();
		if (bannerFromRayTrace.isPresent()) {
			bannerUnique = true;
			for (Civilization civilization : CommonProxy.Companion.getCivilizationMap().values()) {
				if (civilization.getBanner().equalsIgnoreCase(bannerFromRayTrace.get())) {
					bannerUnique = false;
					break;
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.WHITE);
		sb.append("To create a new civilization, you must meet the following requirements:\n\n");

		sb.append("- Possess 3 gold blocks");
		if (playerHasThreeGoldBlocks) {
			sb.append(ChatColor.GREEN);
			sb.append(" ✔");
		} else {
			sb.append(ChatColor.RED);
			sb.append(" ✗");
		}
		sb.append("\n");

		sb.append(ChatColor.WHITE);
		sb.append("- Be 60 blocks from the nearest civilization");
		if (!isPlayerTooClose) {
			sb.append(ChatColor.GREEN);
			sb.append(" ✔");
		} else {
			sb.append(ChatColor.RED);
			sb.append(" ✗");
		}
		sb.append("\n");

		sb.append(ChatColor.WHITE);
		sb.append("- Create a unique banner");
		if (bannerUnique) {
			sb.append(ChatColor.GREEN);
			sb.append(" ✔");
		} else {
			sb.append(ChatColor.RED);
			sb.append(" ✗");
		}
		sb.append("\n\n");

		if (playerHasThreeGoldBlocks && !isPlayerTooClose && bannerUnique) {
			UITextField nameInputField = new UITextField(this, "")
					.setAnchor(Anchor.CENTER | Anchor.BOTTOM)
					.setSize(detailsContainer.getWidth() - 4, 14)
					.setPosition(0, -24)
					.setName("civ.create.nameInput")
					.register(this);
			UIButton create = new UIButton(this, "Create").setSize(80)
					.setAnchor(Anchor.CENTER | Anchor.BOTTOM)
					.setPosition(0, -2)
					.setName("civ.create.confirm")
					.register(this);
			detailsContainer.add(nameInputField, create);
		} else {
			sb.append(ChatColor.WHITE);
			sb.append("Once you have done these things, place and look at your unique banner to continue");
		}

		UILabel uiLabel = new UILabel(this, sb.toString(), true);
		uiLabel.setSize(detailsContainer.getWidth() - 2, detailsContainer.getHeight() - 42);
		uiLabel.setPosition(2, 2);
		detailsContainer.add(uiLabel);

		this.addToScreen(titleLabel);
		this.addToScreen(detailsContainer);
	}

	@Subscribe
	public void onButtonClick(UIButton.ClickEvent event) {
		String buttonID = event.getComponent().getName().toLowerCase();
		if (buttonID.equals("civ.create.confirm")) {
			if (civName == null || civName.length() == 0) {
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("You must supply a civilization name"));
			} else {
				String bannerId = getBannerFromRayTrace().orElse("");
				CommonProxy.Companion.getNETWORK_WRAPPER().sendToServer(new CreateCivMessage.Request(bannerId, civName));
				this.close();
			}
		}
	}

	@Subscribe
	public void onTextInput(ComponentEvent.ValueChange event) {
		String name = event.getComponent().getName().toLowerCase();

		if (name.equals("civ.create.nameinput")) {
			civName = event.getNewValue().toString();
		}
	}

	private Optional<String> getBannerFromRayTrace() {
		Minecraft minecraft = Minecraft.getMinecraft();
		RayTraceResult traceResult = minecraft.player.rayTrace(5.0, 1.0f);
		if (traceResult != null) {
			IBlockState blockState = minecraft.player.getEntityWorld().getBlockState(traceResult.getBlockPos());
			if (blockState.getBlock() instanceof BlockBanner) {
				TileEntityBanner tileEntity = (TileEntityBanner) minecraft.player.getEntityWorld()
						.getTileEntity(traceResult.getBlockPos());
				String bannerPattern = tileEntity.getPatternResourceLocation();
				return Optional.of(bannerPattern);
			}
		}
		return Optional.empty();
	}

	private boolean playerHasEnoughGold(EntityPlayer player, int requiredAmount) {
		int foundSoFar = 0;

		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			ItemStack itemStack = player.inventory.getStackInSlot(i);
			if (itemStack.getItem().equals(Item.getItemFromBlock(Blocks.GOLD_BLOCK))) {
				foundSoFar += itemStack.getCount();
			}
		}
		return foundSoFar >= requiredAmount;
	}
}
