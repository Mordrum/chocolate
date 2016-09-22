package com.mordrum.mcore.client.gui;

import com.google.common.eventbus.Subscribe;
import com.mordrum.mcore.client.AmbientMusicUtility;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;

public class InstallMusicGUI extends MordrumGui {

    private UIButton yesButton;
    private UIButton noButton;
    private UILabel progressLabel;

    @Override
    public void construct() {
        UIBackgroundContainer window = new UIBackgroundContainer(this);
        window.setSize(this.width, this.height);
        window.setAnchor(Anchor.LEFT | Anchor.MIDDLE);
        window.setColor(Integer.MIN_VALUE);
        window.setBackgroundAlpha(0);

        // Center = horizontal, middle = vertical
        UILabel titleLabel = new UILabel(this, ChatColor.RED + "Install Custom Music");
        titleLabel.setPosition(0, 60, Anchor.TOP | Anchor.CENTER);

        UILabel infoLabel = new UILabel(this, ChatColor.WHITE + "Would you like to install Mordrum's custom music pack? (200MB Download)");
        infoLabel.setPosition(0, getPaddedY(titleLabel, 12), Anchor.TOP | Anchor.CENTER);

        yesButton = new UIButton(this, "Yes");
        yesButton.setSize(50, 16);
        yesButton.setPosition(-26, getPaddedY(infoLabel, 12), Anchor.TOP | Anchor.CENTER);
        yesButton.setName("button.yes");
        yesButton.register(this);

        noButton = new UIButton(this, "No");
        noButton.setSize(50, 16);
        noButton.setPosition(26, getPaddedY(infoLabel, 12), Anchor.TOP | Anchor.CENTER);
        noButton.setName("button.no");
        noButton.register(this);

        progressLabel = new UILabel(this, ChatColor.WHITE + "Downloading...");
        progressLabel.setPosition(0, getPaddedY(noButton, 12), Anchor.TOP | Anchor.CENTER);
        progressLabel.setVisible(false);

        window.add(titleLabel, infoLabel, yesButton, noButton, progressLabel);
        this.addToScreen(window);
    }

    @Subscribe
    public void onButtonClick(UIButton.ClickEvent event) {
        String buttonID = event.getComponent().getName().toLowerCase();
        if (buttonID.equals("button.no")) {
            new MainMenuGUI().display();
        } else if (buttonID.equals("button.yes")) {
            yesButton.setDisabled(true);
            noButton.setDisabled(true);
            progressLabel.setVisible(true);

            AmbientMusicUtility.downloadMusic(
                    (fileName) -> progressLabel.setText(ChatColor.WHITE + "Downloading... " + fileName),
                    () -> new MainMenuGUI().display()
            );
        }
    }
}
