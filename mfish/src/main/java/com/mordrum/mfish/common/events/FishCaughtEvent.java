package com.mordrum.mfish.common.events;

import com.mordrum.mfish.common.Fish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraftforge.fml.common.eventhandler.Event;

public class FishCaughtEvent extends Event {
    private final EntityPlayer player;
    private final EntityFishHook fishHook;
    private final Fish fish;
    private final double weight;

    public FishCaughtEvent(EntityPlayer player, EntityFishHook fishHook, Fish fish, double weight) {
        this.player = player;
        this.fishHook = fishHook;
        this.fish = fish;
        this.weight = weight;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public EntityFishHook getFishHook() {
        return fishHook;
    }

    public Fish getFish() {
        return fish;
    }

    public double getWeight() {
        return weight;
    }
}
