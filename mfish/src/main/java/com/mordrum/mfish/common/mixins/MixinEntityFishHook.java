package com.mordrum.mfish.common.mixins;

import com.mordrum.mfish.server.FishingLootGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityFishHook.class)
public abstract class MixinEntityFishHook extends Entity {
    @Shadow
    private int ticksCatchable;
    @Shadow
    private boolean inGround;
    @Shadow
    private Entity caughtEntity;
    @Shadow
    private EntityPlayer angler;

    public MixinEntityFishHook(World worldIn) {
        super(worldIn);
    }

    @Overwrite
    public int handleHookRetraction() {
        if (this.world.isRemote) {
            return 0;
        } else {
            int i = 0;

            if (this.caughtEntity != null) {
                this.bringInHookedEntity();
                this.world.setEntityState(this, (byte) 31);
                i = this.caughtEntity instanceof EntityItem ? 3 : 5;
            } else if (this.ticksCatchable > 0) {
                ItemStack itemStack = FishingLootGenerator.getFishingLoot(angler, this);

                EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, itemStack);
                double d0 = this.angler.posX - this.posX;
                double d1 = this.angler.posY - this.posY;
                double d2 = this.angler.posZ - this.posZ;
                double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                entityitem.motionX = d0 * 0.1D;
                entityitem.motionY = d1 * 0.1D + (double) MathHelper.sqrt(d3) * 0.08D;
                entityitem.motionZ = d2 * 0.1D;
                this.world.spawnEntity(entityitem);
                this.angler.world.spawnEntity(new EntityXPOrb(this.angler.world, this.angler.posX, this.angler.posY + 0.5D, this.angler.posZ + 0.5D, this.rand.nextInt(6) + 1));

                i = 1;
            }

            if (this.inGround) {
                i = 2;
            }

            this.setDead();
            this.angler.fishEntity = null;
            return i;
        }
    }

    @Shadow
    protected abstract void bringInHookedEntity();
}
