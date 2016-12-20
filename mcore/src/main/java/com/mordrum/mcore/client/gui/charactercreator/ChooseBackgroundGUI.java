package com.mordrum.mcore.client.gui.charactercreator;

import com.google.common.eventbus.Subscribe;
import com.mordrum.mcore.client.CharacterManager;
import com.mordrum.mcore.client.character.CharacterBackground;
import com.mordrum.mcore.client.gui.ChatColor;
import com.mordrum.mcore.client.gui.MordrumGui;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;

import java.awt.*;

public class ChooseBackgroundGUI extends MordrumGui {
    private DetailsContainer detailsContainer;

    ChooseBackgroundGUI(MalisisGui previousScreen) {
        super(previousScreen);
    }

    @Override
    public void construct() {
        this.setGuiSize(480, 255);
        // Begin info
        UILabel titleLabel = new UILabel(this, ChatColor.AQUA + "Choose Your Character's Background");
        titleLabel.setPosition(0, 0, Anchor.TOP | Anchor.CENTER);

        UILabel infoLabel1 = new UILabel(this, ChatColor.WHITE + "Choose who your character was before they came to Mordrum.");
        infoLabel1.setPosition(0, getPaddedY(titleLabel, 12), Anchor.TOP | Anchor.CENTER);

        UILabel infoLabel2 = new UILabel(this, ChatColor.WHITE + "Your background grants you a small, permanent bonus and determines your starting equipment.", true);
        infoLabel2.setSize(infoLabel1.getWidth(), 20);
        infoLabel2.setPosition(0, getPaddedY(infoLabel1, 4), Anchor.TOP | Anchor.CENTER);

        UIBackgroundContainer infoContainer = new UIBackgroundContainer(this);
        infoContainer.add(titleLabel, infoLabel1, infoLabel2);
        infoContainer.setPosition(0, 10, Anchor.TOP | Anchor.CENTER);
        infoContainer.setSize(infoLabel1.getWidth(), infoContainer.getContentHeight());
        infoContainer.setBackgroundAlpha(0);
        // End info

        // Begin background buttons
        UIBackgroundContainer backgroundButtonsContainer = new UIBackgroundContainer(this);
        int i = 0;
        for (CharacterBackground characterBackground : CharacterManager.getInstance().getCharacterBackgrounds()) {
            UIButton btn = new UIButton(this, characterBackground.getTitle())
                    .setName("background." + characterBackground.getId())
                    .setPosition(0, i * 20)
                    .setSize(80);
            btn.register(this);
            backgroundButtonsContainer.add(btn);
            i++;
        }
        backgroundButtonsContainer.setAnchor(Anchor.TOP | Anchor.LEFT);
        backgroundButtonsContainer.setSize(80, backgroundButtonsContainer.getContentHeight());
        backgroundButtonsContainer.setPosition(0, 0);
        backgroundButtonsContainer.setBackgroundAlpha(0);
        // End background buttons

        // Begin details
        this.detailsContainer = new DetailsContainer(this);
        detailsContainer.setPosition(80, 0, Anchor.TOP | Anchor.LEFT);
        detailsContainer.setSize((infoLabel2.getContentWidth() - 80), backgroundButtonsContainer.getContentHeight());
        detailsContainer.setColor(new Color(0, 0, 0).getRGB());
        detailsContainer.setBackgroundAlpha(150);
        // End details

        UIBackgroundContainer interactiveContainer = new UIBackgroundContainer(this);
        interactiveContainer.add(detailsContainer, backgroundButtonsContainer);
        interactiveContainer.setPosition(0, getPaddedY(infoContainer, 4), Anchor.TOP | Anchor.CENTER);
        interactiveContainer.setSize(infoLabel2.getContentWidth(), interactiveContainer.getContentHeight());
        interactiveContainer.setBackgroundAlpha(0);

        UIButton backButton = new UIButton(this, "Back");
        backButton.setSize(60);
        backButton.setName("button.back");
        backButton.register(this);

        UIButton nextButton = new UIButton(this, "Next");
        nextButton.setPosition(62, 0);
        nextButton.setSize(60);
        nextButton.setName("button.next");
        nextButton.register(this);

        UIBackgroundContainer buttonsContainer = new UIBackgroundContainer(this);
        buttonsContainer.setPosition(0, -10);
        buttonsContainer.setSize(122, 20);
        buttonsContainer.setAnchor(Anchor.BOTTOM | Anchor.CENTER);
        buttonsContainer.setBackgroundAlpha(0);
        buttonsContainer.add(backButton, nextButton);

        this.addToScreen(infoContainer);
        this.addToScreen(interactiveContainer);
        this.addToScreen(buttonsContainer);
    }

    @Subscribe
    public void onButtonClick(UIButton.ClickEvent event) {
        String buttonID = event.getComponent().getName().toLowerCase();
        if (buttonID.equals("button.next")) {
            new SelectTraitsGUI(this).display();
        } else if (buttonID.equals("button.back")) {
            this.getPreviousScreen().display();
        } else if (buttonID.startsWith("background.")) {
            int backgroundId = Integer.parseInt(buttonID.substring("background.".length()));
            detailsContainer.refresh(CharacterManager.getInstance().getCharacterBackground(backgroundId));
        }
    }

    private class DetailsContainer extends UIBackgroundContainer {
        private final UILabel titleLabel;
        private final UILabel descriptionLabel;
        private final UILabel perkLabel;
        private final UILabel heirloomLabel;

        DetailsContainer(MalisisGui gui) {
            super(gui);
            titleLabel = new UILabel(gui)
                    .setPosition(0, 4, Anchor.TOP | Anchor.CENTER);
            descriptionLabel = new UILabel(gui, true)
                    .setPosition(8, getPaddedY(titleLabel, 12), Anchor.TOP | Anchor.CENTER)
                    .setSize(this.width - 16, 60);
            heirloomLabel = new UILabel(gui)
                    .setPosition(8, getPaddedY(descriptionLabel, 12), Anchor.TOP | Anchor.LEFT);
            perkLabel = new UILabel(gui)
                    .setPosition(8, getPaddedY(heirloomLabel, 12), Anchor.TOP | Anchor.LEFT);

            this.add(titleLabel, descriptionLabel, heirloomLabel, perkLabel);
        }

        void refresh(CharacterBackground background) {
            titleLabel.setText(ChatColor.UNDERLINE.toString() + ChatColor.AQUA + background.getTitle());
            descriptionLabel.setText(ChatColor.WHITE + background.getDescription());
            heirloomLabel.setText(ChatColor.YELLOW + "Heirloom: " + ChatColor.WHITE + background.getHeirloomDescription());
            perkLabel.setText(ChatColor.YELLOW + "Perk: " + ChatColor.WHITE + background.getPerkDescription());
        }
    }
}
