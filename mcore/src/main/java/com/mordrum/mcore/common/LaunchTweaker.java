package com.mordrum.mcore.common;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinTweaker;

import java.io.File;
import java.util.List;

public class LaunchTweaker implements ITweaker {
    private static MixinTweaker mixinTweaker;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        mixinTweaker = new MixinTweaker();
        mixinTweaker.acceptOptions(args, gameDir, assetsDir, profile);
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        mixinTweaker.injectIntoClassLoader(classLoader);
    }

    @Override
    public String getLaunchTarget() {
        return "";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
