package com.mordrum.mcore.client.gui;

import net.malisis.core.client.gui.GuiRenderer;
import net.malisis.core.client.gui.GuiTexture;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UIImage;
import net.malisis.core.client.gui.component.interaction.UIButton;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;
import java.util.HashMap;
import java.util.Map;

public class ColorPalettes {
    public static final ColorPalette DEFAULT_SKIN_TONES = new ColorPalette("Default Skin Tones", "", Type.SKIN, new Color[]{
        new Color(45, 34, 30),
        new Color(60, 46, 40),
        new Color(75, 57, 50),
        new Color(90, 69, 60),
        new Color(105, 80, 70),
        new Color(120, 92, 80),
        new Color(135, 103, 90),
        new Color(150, 114, 100),
        new Color(165, 126, 110),
        new Color(180, 138, 120),
        new Color(195, 149, 130),
        new Color(210, 161, 140),
        new Color(225, 172, 150),
        new Color(240, 184, 160),
        new Color(255, 195, 170),
        new Color(255, 206, 180),
        new Color(255, 218, 190),
        new Color(255, 229, 200)
    });

    public static final ColorPalette DEFAULT_HAIR = new ColorPalette("Default Hair", "", Type.HAIR, new Color[]{
            new Color(9, 8, 6),
            new Color(44, 34, 43),
            new Color(113, 99, 90),
            new Color(183, 166, 158),
            new Color(214, 196, 194),
            new Color(202, 191, 177),
            new Color(220, 208, 186),
            new Color(255, 245, 225),
            new Color(230, 206, 168),
            new Color(229, 200, 168),
            new Color(222, 188, 153),
            new Color(184, 151, 120),
            new Color(165, 107, 70),
            new Color(181, 82, 57),
            new Color(141, 74, 67),
            new Color(145, 85, 61),
            new Color(83, 61, 50),
            new Color(59, 48, 36),
            new Color(85, 72, 56),
            new Color(78, 67, 63),
            new Color(80, 68, 68),
            new Color(106, 78, 66),
            new Color(167, 133, 106),
            new Color(151, 121, 97)
    });

    public static final ColorPalette DEFAULT_EYES = new ColorPalette("Default Eyes", "", Type.EYES, new Color[]{
            new Color(69, 64, 181),
            new Color(69, 64, 181),
            new Color(83, 124, 181),
            new Color(95, 169, 181),
            new Color(77, 181, 126),
            new Color(55, 128, 89),
            new Color(181, 82, 57),
            new Color(229, 200, 168),
            new Color(167, 133, 106),
            new Color(151, 121, 97),
            new Color(80, 68, 68),
            new Color(113, 99, 90)
    });

    private enum Type {
        SKIN, HAIR, EYES
    }

    public static class ColorPalette {
        private final String name;
        private final String description;
        private final Type type;
        private final Color[] colors;
        private final Map<Integer, LookupOp> lookupOpMap;

        public ColorPalette(String name, String description, Type type, Color[] colors) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.colors = colors;
            this.lookupOpMap = new HashMap<>(colors.length);
            for (Color color : colors) {
                lookupOpMap.put(color.getRGB(), createLookupOp(color));
            }
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Type getType() {
            return type;
        }

        public Color[] getColors() {
            return colors;
        }

        public LookupOp getLookupOp(int color) {
            return lookupOpMap.get(color);
        }

        public UIBackgroundContainer generateUI(MalisisGui gui, int columns, int size) {
            UIBackgroundContainer window = new UIBackgroundContainer(gui);
            window.setSize(columns * size + 1, this.colors.length / columns * size + 1);
            window.setPadding(0,0);
            window.setColor(Integer.MIN_VALUE);

            int currentColumn = 0;
            int currentRow = 0;

            for (Color color : this.colors) {
                UIButton uiButton = new PaletteButton(gui, color.getRGB(), size);
                uiButton.setPosition(currentColumn * size, currentRow * size);
                uiButton.setName(type.name() + "_" + color.getRGB());
                window.add(uiButton);
                currentColumn++;
                if (currentColumn >= columns) {
                    currentColumn = 0;
                    currentRow++;
                }
            }

            return window;
        }

        private LookupOp createLookupOp(Color color) {
            short[] alpha = new short[256];
            short[] red = new short[256];
            short[] green = new short[256];
            short[] blue = new short[256];

            if (this.type == Type.EYES) {
                for (short i = 0; i < 256; i++) {
                    alpha[i] = i;
                    red[i] = i;
                    green[i] = i;
                    blue[i] = i;
                }
                red[255] = (short) color.getRed();
                green[255] = (short) color.getGreen();
                blue[255] = (short) color.getBlue();
            } else {
                for (short i = 0; i < 256; i++) {
                    alpha[i] = i;
                    red[i] = (short) ((color.getRed()) * (float) i / 255.0);
                    green[i] = (short) ((color.getGreen()) * (float) i / 255.0);
                    blue[i] = (short) ((color.getBlue()) * (float) i / 255.0);
                }
            }

            short[][] data = new short[][]{red, green, blue, alpha};

            LookupTable lookupTable = new ShortLookupTable(0, data);
            return new LookupOp(lookupTable, null);
        }
    }

    private static class PaletteButton extends UIButton {
        PaletteButton(MalisisGui gui, int color, int size) {
            super(gui);
            BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
            bufferedImage.setRGB(0, 0, color);
            UIImage uiImage = new UIImage(gui, new GuiTexture(bufferedImage, "palette_color_" + color), null);
            uiImage.setSize(size - 3, size - 3);
            super.setImage(uiImage);
            this.register(gui);
        }

        @Override
        public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick) {
            super.drawForeground(renderer, mouseX, mouseY, partialTick);
            int w = image.getWidth();
            int h = image.getHeight();

            int x = (width - w) / 2;
            int y = (height - h) / 2;
            if (x == 0) x = 1;
            if (y == 0) y = 1;

            if (isPressed && isHovered()) {
                x += 1;
                y += 1;
                image.setSize(this.getWidth() - 1, this.getHeight() - 1);
            } else {
                image.setSize(this.getWidth(), this.getHeight());
            }

            x += offsetX;
            y += offsetY;

            image.setPosition(x, y);
            image.setZIndex(zIndex);
            image.draw(renderer, mouseX, mouseY, partialTick);
        }
    }
}
