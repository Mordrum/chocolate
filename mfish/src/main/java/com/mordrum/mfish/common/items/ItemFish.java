package com.mordrum.mfish.common.items;

import com.mordrum.mfish.MFish;
import com.mordrum.mfish.common.Fish;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.List;

public class ItemFish extends ItemFishFood {
    private boolean cooked;

    public ItemFish(boolean cooked) {
        super(cooked);
        this.cooked = cooked;
        this.setUnlocalizedName(MFish.MOD_ID + "_fish");
        this.setHasSubtypes(true);
        this.setRegistryName(MFish.MOD_ID, (this.cooked ? "cooked" : "raw") + "_fish");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (Fish fish : Fish.getFishes()) {
            subItems.add(new ItemStack(this, 1, fish.getMetadata()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Fish fish = Fish.byItemStack(stack);
        return this.getUnlocalizedName() + "_" + fish.getName() + (this.cooked ? "_cooked" : "_raw");
    }


    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack) {
        if (par1ItemStack.hasTagCompound()) {
            if (par1ItemStack.getTagCompound().hasKey("Prefix")) {
                return par1ItemStack.getTagCompound().getString("Prefix") + " " + super.getItemStackDisplayName(par1ItemStack);
            }
        }
        return super.getItemStackDisplayName(par1ItemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (stack.hasTagCompound()) {
            if (stack.getTagCompound().hasKey("Weight")) {
                float weight = stack.getTagCompound().getFloat("Weight");

                DecimalFormat df = new DecimalFormat("#,###.##");
                BigDecimal bd = new BigDecimal(weight);
                bd = bd.round(new MathContext(3));
                if (bd.doubleValue() > 999)
                    tooltip.add("Weight: " + df.format(bd.doubleValue()) + "lb");
                else
                    tooltip.add("Weight: " + bd + "lb");
            }
        }
    }
}
