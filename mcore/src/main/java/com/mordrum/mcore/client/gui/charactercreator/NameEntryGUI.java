package com.mordrum.mcore.client.gui.charactercreator;

import com.google.common.eventbus.Subscribe;
import com.mordrum.mcore.client.gui.ChatColor;
import com.mordrum.mcore.client.gui.MordrumGui;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UITextField;

public class NameEntryGUI extends MordrumGui {
    @Override
    public void construct() {
        this.setGuiSize(480, 255);

        UILabel titleLabel = new UILabel(this, ChatColor.AQUA + "Choose Your Character's Name");
        titleLabel.setPosition(0, 10, Anchor.TOP | Anchor.CENTER);

        UILabel infoLabel1 = new UILabel(this, ChatColor.WHITE + "This name will be displayed everywhere your Minecraft username would normally be displayed. Try and choose one word, using only letters.", true);
        infoLabel1.setPosition(this.width / 2 - 160, getPaddedY(titleLabel, 12));
        infoLabel1.setSize(320, 30);

        UITextField nameInput = new UITextField(this, false);
        nameInput.setPosition(0, getPaddedY(infoLabel1, 12), Anchor.TOP | Anchor.CENTER);
        nameInput.setSize(titleLabel.getWidth(), 30);

        UIButton nextButton = new UIButton(this, "Next");
        nextButton.setPosition(0, getPaddedY(nameInput, 12), Anchor.TOP | Anchor.CENTER);
        nextButton.setSize(60);
        nextButton.setName("button.next");
        nextButton.register(this);

        this.addToScreen(titleLabel);
        this.addToScreen(infoLabel1);
        this.addToScreen(nameInput);
        this.addToScreen(nextButton);
    }

    @Subscribe
    public void onButtonClick(UIButton.ClickEvent event) {
        String buttonID = event.getComponent().getName().toLowerCase();
        if (buttonID.equals("button.next")) {
            new CustomizeApearanceGUI(this).display();
        }
    }
}
