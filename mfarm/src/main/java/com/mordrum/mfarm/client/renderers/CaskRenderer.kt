package com.mordrum.mfarm.client.renderers

import com.mordrum.mfarm.MFarm
import com.mordrum.mfarm.common.tileentities.CaskState
import com.mordrum.mfarm.common.tileentities.CaskTileEntity
import net.malisis.core.block.component.DirectionalComponent
import net.malisis.core.renderer.MalisisRenderer
import net.malisis.core.renderer.RenderParameters
import net.malisis.core.renderer.RenderType
import net.malisis.core.renderer.animation.AnimationRenderer
import net.malisis.core.renderer.animation.transformation.*
import net.malisis.core.renderer.element.Shape
import net.malisis.core.renderer.icon.Icon
import net.malisis.core.renderer.model.MalisisModel
import net.malisis.core.util.MBlockState
import net.minecraft.util.ResourceLocation

class CaskRenderer : MalisisRenderer<CaskTileEntity>() {
    init {
        this.registerFor(CaskTileEntity::class.java)
    }

    private val ar: AnimationRenderer = AnimationRenderer()

    private lateinit var model: MalisisModel
    private lateinit var rp: RenderParameters

    private lateinit var shapeBarrel: Shape
    private lateinit var shapeRings: Shape
    private lateinit var shapeFeet: Shape

    private lateinit var scaleAnim: ChainedTransformation

    override fun initialize() {
        val resourceLocation = ResourceLocation(MFarm.MOD_ID, "models/cask.obj")
        this.model = MalisisModel(resourceLocation)
        this.rp = RenderParameters()

        this.shapeBarrel = this.model.getShape("Barrel")
        this.shapeRings = this.model.getShape("Rings")
        this.shapeFeet = this.model.getShape("Feet")

        this.scaleAnim = ChainedTransformation(
                Scale(1f, 1f, 1f, 1.03f, 1.03f, 1.03f).forTicks(20).movement(Transformation.SINUSOIDAL),
                Scale(1.03f, 1.03f, 1.03f, 1f, 1f, 1f).forTicks(20).movement(Transformation.SINUSOIDAL)
        ).loop(-1)
    }

    override fun render() {
        this.model.resetState()

        if (this.renderType == RenderType.TILE_ENTITY || this.renderType == RenderType.ITEM) {
            val ringIcon: Icon
            val barrelIcon: Icon
            val feetIcon: Icon
            if (this.renderType == RenderType.TILE_ENTITY) {
                ringIcon = Icon.from(this.tileEntity.ringsState)
                barrelIcon = Icon.from(this.tileEntity.barrelState)
                feetIcon = Icon.from(this.tileEntity.feetState)
                this.model.rotate(DirectionalComponent.getDirection(this.blockState))
                if (this.tileEntity.state == CaskState.FERMENTING) {
                    this.scaleAnim.transform(shapeBarrel, ar.elapsedTime)
                    this.scaleAnim.transform(shapeRings, ar.elapsedTime)
                }
            } else {
                ringIcon = Icon.from(MBlockState.fromNBT(this.itemStack.tagCompound, "rings_block", "rings_meta"))
                barrelIcon = Icon.from(MBlockState.fromNBT(this.itemStack.tagCompound, "barrel_block", "barrel_meta"))
                feetIcon = Icon.from(MBlockState.fromNBT(this.itemStack.tagCompound, "feet_block", "feet_meta"))
//                this.model.rotate(-30f, 0f, 1f, 0f, 0f, 0f, 0f)
//                this.model.scale(0.75f, 0.75f, 0.75f, 0f, 0f, 0f)
            }

            this.rp.applyTexture.set(true)
            this.rp.interpolateUV.set(false)

            this.rp.icon.set(ringIcon)
            drawShape(shapeRings, this.rp)

            this.rp.icon.set(barrelIcon)
            drawShape(shapeBarrel, this.rp)

            this.rp.icon.set(feetIcon)
            drawShape(shapeFeet, this.rp)
        } else if (this.renderType == RenderType.BLOCK) {
            this.ar.setStartTime()
        }
    }
}