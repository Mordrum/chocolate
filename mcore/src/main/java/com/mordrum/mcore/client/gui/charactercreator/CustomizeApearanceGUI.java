package com.mordrum.mcore.client.gui.charactercreator;

import com.google.common.base.Converter;
import com.google.common.eventbus.Subscribe;
import com.mordrum.mcore.client.CrazyModelPlayer;
import com.mordrum.mcore.client.gui.ChatColor;
import com.mordrum.mcore.client.gui.ColorPalettes;
import com.mordrum.mcore.client.gui.MordrumGui;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UISlider;
import net.malisis.core.client.gui.event.ComponentEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CustomizeApearanceGUI extends MordrumGui {
    private final int SKIN_IMAGE_SIZE = 64;
    private final int COLOR_COLUMNS = 6;
    private final int COLOR_SIZE = 12;
    private int skinColor;
    private int hairColor;
    private int hairStyle;
    private int eyesColor;

    public static ResourceLocation skinTexture;
    private UIBackgroundContainer entityContainer;
    private long frames;

    CustomizeApearanceGUI(MalisisGui previousScreen) {
        super(previousScreen);
    }

    @Override
    public void construct() {
        this.setGuiSize(480, 255);

        // Begin info
        UILabel titleLabel = new UILabel(this, ChatColor.AQUA + "Customize Your Character's Appearance");
        titleLabel.setPosition(0, 0, Anchor.TOP | Anchor.CENTER);

        UILabel infoLabel1 = new UILabel(this, ChatColor.WHITE + "Customize how your character will look in game.");
        infoLabel1.setPosition(0, getPaddedY(titleLabel, 12), Anchor.TOP | Anchor.CENTER);

        UIBackgroundContainer infoContainer = new UIBackgroundContainer(this);
        infoContainer.setPosition(0, 10, Anchor.TOP | Anchor.CENTER);
        infoContainer.setSize(infoLabel1.getWidth(), infoContainer.getContentHeight());
        infoContainer.setBackgroundAlpha(0);
        infoContainer.add(titleLabel, infoLabel1);
        // End info

        // Begin color palette setup
        UIBackgroundContainer paletteContainer = new UIBackgroundContainer(this);
        paletteContainer.setBackgroundAlpha(0);

        UILabel skinPaletteLabel = new UILabel(this, ChatColor.WHITE + "Skin Tone");
        skinPaletteLabel.setPosition(0, 0, Anchor.CENTER | Anchor.TOP);
        UIBackgroundContainer skinPalette = ColorPalettes.DEFAULT_SKIN_TONES.generateUI(this, COLOR_COLUMNS, COLOR_SIZE);
        skinPalette.setPosition(0, getPaddedY(skinPaletteLabel, 2), Anchor.CENTER | Anchor.TOP);

        UILabel hairPaletteLabel = new UILabel(this, ChatColor.WHITE + "Hair Color");
        hairPaletteLabel.setPosition(0, getPaddedY(skinPalette, 5), Anchor.CENTER | Anchor.TOP);
        UIBackgroundContainer hairPalette = ColorPalettes.DEFAULT_HAIR.generateUI(this, COLOR_COLUMNS, COLOR_SIZE);
        hairPalette.setPosition(0, getPaddedY(hairPaletteLabel, 2), Anchor.CENTER | Anchor.TOP);

        UILabel eyePaletteLabel = new UILabel(this, ChatColor.WHITE + "Eye Color");
        eyePaletteLabel.setPosition(0, getPaddedY(hairPalette, 5), Anchor.CENTER | Anchor.TOP);
        UIBackgroundContainer eyePalette = ColorPalettes.DEFAULT_EYES.generateUI(this, COLOR_COLUMNS, COLOR_SIZE);
        eyePalette.setPosition(0, getPaddedY(eyePaletteLabel, 2), Anchor.CENTER | Anchor.TOP);

        paletteContainer.add(skinPaletteLabel, skinPalette, hairPaletteLabel, hairPalette, eyePaletteLabel, eyePalette);
        paletteContainer.setSize(skinPalette.getWidth(), paletteContainer.getContentHeight());
        paletteContainer.setAnchor(Anchor.RIGHT | Anchor.MIDDLE);
        paletteContainer.setPosition(-10,0);
        // End color palette setup

        // Begin Sliders setup
        UIBackgroundContainer sliderContainer = new UIBackgroundContainer(this);
        sliderContainer.setAnchor(Anchor.LEFT | Anchor.MIDDLE);
        sliderContainer.setBackgroundAlpha(0);

        Converter<Float, Integer> converter = new Converter<Float, Integer>() {
            @Override
            protected Integer doForward(@NotNull Float aFloat) {
                if (aFloat < 0.33) return 0;
                else if (aFloat < 0.66) return 1;
                else return 2;
            }

            @Override
            protected Float doBackward(@NotNull Integer integer) {
                if (integer == 1) return 0.33f;
                else if (integer == 2) return 0.66f;
                else return 1.0f;
            }
        };
        UISlider hairSlider = new UISlider(this, skinPalette.getWidth(), converter, "Hair Style");
        hairSlider.setPosition(0, 0, Anchor.CENTER | Anchor.TOP);
        hairSlider.register(this);

        sliderContainer.add(hairSlider);
        sliderContainer.setSize(hairSlider.getWidth(), sliderContainer.getContentHeight());
        sliderContainer.setPosition(10, 0);
        // End sliders setup

        skinColor = ColorPalettes.DEFAULT_SKIN_TONES.getColors()[0].getRGB();
        hairColor = ColorPalettes.DEFAULT_HAIR.getColors()[0].getRGB();
        eyesColor = ColorPalettes.DEFAULT_EYES.getColors()[0].getRGB();

        entityContainer = new UIBackgroundContainer(this);
        entityContainer.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        entityContainer.setSize(paletteContainer.getWidth(), paletteContainer.getHeight());
        entityContainer.setBackgroundAlpha(0);

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
        this.addToScreen(sliderContainer);
        this.addToScreen(entityContainer);
        this.addToScreen(paletteContainer);
        this.addToScreen(buttonsContainer);

        try {
            generateSkin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onButtonClick(UIButton.ClickEvent event) throws IOException {
        String buttonID = event.getComponent().getName().toLowerCase();

        switch (buttonID) {
            case "button.next":
                new ChooseBackgroundGUI(this).display();
                break;
            case "button.back":
                this.getPreviousScreen().display();
                break;
            default:
                String[] split = buttonID.split("_");
                String category = split[0];
                int color = Integer.parseInt(split[1]);

                switch (category) {
                    case "skin":
                        skinColor = color;
                        break;
                    case "hair":
                        hairColor = color;
                        break;
                    case "eyes":
                        eyesColor = color;
                        break;
                }

                generateSkin();
                break;
        }
    }

    @Subscribe
    public void onSliderChange(ComponentEvent.ValueChange event) throws IOException {
        this.hairStyle = Math.round((float) event.getNewValue());
        generateSkin();
    }

    private void generateSkin() throws IOException {
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();

        // Load our skin and hair
        BufferedImage skin = ImageIO.read(resourceManager.getResource(new ResourceLocation("mcore", "textures/skin/nude.png")).getInputStream());
        BufferedImage hair = ImageIO.read(resourceManager.getResource(new ResourceLocation("mcore", "textures/skin/hair/" + hairStyle + ".png")).getInputStream());
        BufferedImage eyes = ImageIO.read(resourceManager.getResource(new ResourceLocation("mcore", "textures/skin/eyes/1.png")).getInputStream());

        //TODO perhaps migrate all lookup tables into a global cache of sorts to make it easier in the future to work with multiple palettes
        ColorPalettes.DEFAULT_SKIN_TONES.getLookupOp(skinColor).filter(skin, skin);
        ColorPalettes.DEFAULT_HAIR.getLookupOp(hairColor).filter(hair, hair);
        ColorPalettes.DEFAULT_EYES.getLookupOp(eyesColor).filter(eyes, eyes);

        // Create the final image
        BufferedImage combined = new BufferedImage(SKIN_IMAGE_SIZE, SKIN_IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = combined.getGraphics();
        graphics.drawImage(skin, 0, 0, null);
        graphics.drawImage(hair, 0, 0, null);
        graphics.drawImage(eyes, 0, 0, null);
        graphics.dispose();

        skinTexture = Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation("dynamicskin", new DynamicTexture(combined));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        CrazyModelPlayer modelPlayer = new CrazyModelPlayer(0.0f, false);
        modelPlayer.isChild = true;
        Minecraft.getMinecraft().getTextureManager().bindTexture(skinTexture);
        int x = entityContainer.screenX() + (entityContainer.getWidth() / 2);
        int y = entityContainer.screenY() + (entityContainer.getHeight() / 4);
        modelPlayer.render(4.0f, x, y, frames);
    }

    @Override
    public void update(int mouseX, int mouseY, float partialTick) {
        super.update(mouseX, mouseY, partialTick);
        frames++;
    }
}
