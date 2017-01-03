package com.mordrum.mdeco.client.renderers

import com.mordrum.mdeco.MDeco
import com.mordrum.mdeco.common.tileentities.CaskTileEntity
import net.malisis.core.block.component.DirectionalComponent
import net.malisis.core.renderer.MalisisRenderer
import net.malisis.core.renderer.RenderParameters
import net.malisis.core.renderer.RenderType
import net.malisis.core.renderer.element.Shape
import net.malisis.core.renderer.icon.Icon
import net.malisis.core.renderer.model.MalisisModel
import net.minecraft.util.ResourceLocation

class CaskRenderer : MalisisRenderer<CaskTileEntity>() {
    init {
        this.registerFor(CaskTileEntity::class.java)
    }

    private lateinit var model: MalisisModel
    private lateinit var rp: RenderParameters

    private lateinit var shapeBarrel: Shape
    private lateinit var shapeRings: Shape
    private lateinit var shapeFeet: Shape

    override fun initialize() {
        val resourceLocation = ResourceLocation(MDeco.MOD_ID, "models/cask.obj")
        this.model = MalisisModel(resourceLocation)
        this.rp = RenderParameters()

        this.shapeBarrel = this.model.getShape("Barrel")
        this.shapeRings = this.model.getShape("Rings")
        this.shapeFeet = this.model.getShape("Feet")
    }

    override fun render() {
        this.model.resetState()

        if (this.renderType == RenderType.BLOCK) {
            this.model.rotate(DirectionalComponent.getDirection(this.blockState))

            this.rp.applyTexture.set(true)
            this.rp.interpolateUV.set(false)

            this.rp.icon.set(Icon.from(this.tileEntity.ringsState))
            drawShape(shapeRings, this.rp)

            this.rp.icon.set(Icon.from(this.tileEntity.barrelState))
            drawShape(shapeBarrel, this.rp)

            this.rp.icon.set(Icon.from(this.tileEntity.feetState))
            drawShape(shapeFeet, this.rp)
        }
    }
}