package com.mordrum.mcore.client.gui.charactercreator;

import com.google.common.eventbus.Subscribe;
import com.mordrum.mcore.client.gui.ChatColor;
import com.mordrum.mcore.client.gui.MainMenuGUI;
import com.mordrum.mcore.client.gui.MordrumGui;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UITextField;

public class ConfirmCharacterGUI extends MordrumGui {
    public ConfirmCharacterGUI(MalisisGui previousScreen) {
        super(previousScreen);
    }

    @Override
    public void construct() {
        UIBackgroundContainer window = new UIBackgroundContainer(this);
        window.setSize(200, 225);
        window.setAnchor(Anchor.LEFT | Anchor.MIDDLE);
        window.setColor(Integer.MIN_VALUE);
        window.setBackgroundAlpha(0);

        UILabel titleLabel = new UILabel(this, ChatColor.RED + "Choose Your Character's Name");
        titleLabel.setPosition(0, 60, Anchor.TOP | Anchor.CENTER);

        UILabel infoLabel1 = new UILabel(this, ChatColor.WHITE + "This name will be displayed everywhere your Minecraft username would normally be displayed.");
        infoLabel1.setPosition(0, getPaddedY(titleLabel, 12), Anchor.TOP | Anchor.CENTER);

        UILabel infoLabel2 = new UILabel(this, ChatColor.WHITE + "Try and choose one word, using only letters.");
        infoLabel2.setPosition(0, getPaddedY(infoLabel1, 4), Anchor.TOP | Anchor.CENTER);

        UITextField nameInput = new UITextField(this, false);
        nameInput.setPosition(0, getPaddedY(infoLabel2, 12), Anchor.TOP | Anchor.CENTER);
        nameInput.setSize(titleLabel.getWidth(), 30);

        UIButton nextButton = new UIButton(this, "Next");
        nextButton.setPosition(0, getPaddedY(nameInput, 12), Anchor.TOP | Anchor.CENTER);
        nextButton.setName("button.next");
        nextButton.register(this);

        window.add(titleLabel, infoLabel1, infoLabel2, nameInput, nextButton);
        this.addToScreen(window);
    }

    @Subscribe
    public void onButtonClick(UIButton.ClickEvent event) {
        String buttonID = event.getComponent().getName().toLowerCase();
        if (buttonID.equals("button.next")) {
            new MainMenuGUI().display();
        }
    }
}
