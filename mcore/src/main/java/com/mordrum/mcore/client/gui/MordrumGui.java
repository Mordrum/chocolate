package com.mordrum.mcore.client.gui;

import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.UIComponent;

public abstract class MordrumGui extends MalisisGui {
    private MalisisGui previousScreen;

    public MordrumGui() {
    }

    public MordrumGui(MalisisGui previousScreen) {
        this.previousScreen = previousScreen;
    }

    protected int getPaddedY(UIComponent component) {
        return getPaddedY(component, 4);
    }

    protected int getPaddedY(UIComponent component, int padding) {
        if (component == null) {
            return 0;
        }
        return component.getY() + component.getHeight() + padding;
    }

    public MalisisGui getPreviousScreen() {
        return previousScreen;
    }
}
