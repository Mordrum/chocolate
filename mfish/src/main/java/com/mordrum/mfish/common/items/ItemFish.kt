package com.mordrum.mfish.common.items

import com.mordrum.mfish.MFish
import com.mordrum.mfish.common.Fish
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemFishFood
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.math.BigDecimal
import java.math.MathContext
import java.text.DecimalFormat

class ItemFish(private val cooked: Boolean) : ItemFishFood(cooked) {

    init {
        this.unlocalizedName = MFish.MOD_ID + "_fish"
        this.setHasSubtypes(true)
        this.setRegistryName(MFish.MOD_ID, (if (this.cooked) "cooked" else "raw") + "_fish")
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, subItems: NonNullList<ItemStack>) {
        Fish.getFishes().mapTo(subItems) { ItemStack(this, 1, it.metadata) }
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        val fish = Fish.byItemStack(stack)
        return this.unlocalizedName + "_" + fish.name + if (this.cooked) "_cooked" else "_raw"
    }


    override fun getItemStackDisplayName(par1ItemStack: ItemStack): String {
        if (par1ItemStack.hasTagCompound()) {
            if (par1ItemStack.tagCompound!!.hasKey("Prefix")) {
                return par1ItemStack.tagCompound!!.getString("Prefix") + " " + super.getItemStackDisplayName(par1ItemStack)
            }
        }
        return super.getItemStackDisplayName(par1ItemStack)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack?, worldIn: World?, tooltip: MutableList<String>?, flagIn: ITooltipFlag?) {
        if (stack!!.hasTagCompound()) {
            if (stack.tagCompound!!.hasKey("Weight")) {
                val weight = stack.tagCompound!!.getFloat("Weight")

                val df = DecimalFormat("#,###.##")
                var bd = BigDecimal(weight.toDouble())
                bd = bd.round(MathContext(3))
                if (bd.toDouble() > 999)
                    tooltip!!.add("Weight: " + df.format(bd.toDouble()) + "lb")
                else
                    tooltip!!.add("Weight: " + bd + "lb")
            }
        }
    }
}
