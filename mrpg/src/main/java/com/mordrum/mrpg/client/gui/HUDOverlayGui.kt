package com.mordrum.mrpg.client.gui

import com.mordrum.mcore.common.stats.StatManager
import com.mordrum.mrpg.common.StaminaStat
import net.malisis.core.client.gui.Anchor
import net.malisis.core.client.gui.GuiRenderer
import net.malisis.core.client.gui.MalisisGui
import net.malisis.core.client.gui.component.UIComponent
import net.malisis.core.client.gui.component.decoration.UILabel
import net.malisis.core.client.gui.element.SimpleGuiShape
import net.malisis.core.renderer.font.FontOptions
import net.minecraft.util.math.MathHelper
import java.awt.Color

class HUDOverlayGui : MalisisGui() {
    lateinit var healthBar: BarComponent
    lateinit var healthLabel: UILabel
    lateinit var staminaBar: BarComponent
    lateinit var staminaLabel: UILabel

    override fun construct() {
        this.guiscreenBackground = false

        val left = resolution.scaledWidth / 2 - 91
        val top = resolution.scaledHeight - 31

        // Initialize and add health bar
        this.healthBar = BarComponent(this, Color(229, 37, 4), 1f, 1f)
        healthBar.setPosition(left, top)
        this.addToScreen(healthBar)

        // Initialize and add staminaBar bar
        this.staminaBar = BarComponent(this, Color(106, 229, 23), 1f, 1f)
        staminaBar.setPosition(left, top - 9)
        this.addToScreen(staminaBar)

        this.healthLabel = UILabel(this, "1 / 1").setPosition(0, top + 2, Anchor.CENTER).setFontOptions(FontOptions.FontOptionsBuilder().scale(0.6f).build())
        this.addToScreen(healthLabel)

        this.staminaLabel = UILabel(this, "1 / 1").setPosition(0, top - 9 + 2, Anchor.CENTER).setFontOptions(FontOptions.FontOptionsBuilder().scale(0.6f).build())
        this.addToScreen(staminaLabel)
    }

    override fun update(mouseX: Int, mouseY: Int, partialTick: Float) {
        this.healthBar.isVisible = !this.mc.player.isCreative
        this.healthLabel.isVisible = !this.mc.player.isCreative
        this.staminaBar.isVisible = !this.mc.player.isCreative
        this.staminaLabel.isVisible = !this.mc.player.isCreative

        this.healthBar.maximum = this.mc.player.maxHealth
        this.healthBar.current = this.mc.player.health

        val formattedHealth = MathHelper.ceil(this.mc.player.health * 2).toFloat() / 2
        this.healthLabel.text = "$formattedHealth / ${MathHelper.ceil(this.mc.player.maxHealth)}"

        val staminaStat = StatManager.getStatForPlayer("staminaBar", this.mc.player) as StaminaStat
        this.staminaBar.maximum = staminaStat.maxStamina
        this.staminaBar.current = staminaStat.stamina
        this.staminaLabel.text = "${MathHelper.floor(staminaStat.stamina)} / ${MathHelper.ceil(staminaStat.maxStamina)}"
    }

    class BarComponent(gui: MalisisGui, var color: Color, var maximum: Float, var current: Float) : UIComponent<BarComponent>(gui) {
        init {
            setSize(182, 8)
            shape = SimpleGuiShape()
        }

        override fun drawBackground(renderer: GuiRenderer, mouseX: Int, mouseY: Int, partialTick: Float) {
            rp.reset()
            renderer.disableTextures()

            // Draw black rectangle
            shape.resetState()
            shape.setSize(width, height)
            rp.setColor(Color.BLACK.rgb)
            renderer.drawShape(shape, rp)

            // Draw grey inner rectangle
            shape.resetState()
            shape.setSize(width - 2, height - 2)
            shape.setPosition(1, 1)
            rp.setColor(Color.GRAY.rgb)
            renderer.drawShape(shape, rp)
        }

        override fun drawForeground(renderer: GuiRenderer, mouseX: Int, mouseY: Int, partialTick: Float) {
            rp.reset()
            shape.resetState()
            renderer.disableTextures()

            val percent = this.current / this.maximum
            shape.setSize(MathHelper.floor(percent * width) - 2, height - 2)
            shape.setPosition(1, 1)
            rp.setColor(this.color.rgb)
            renderer.drawShape(shape, rp)

            renderer.next()
            renderer.enableTextures()
        }
    }
}