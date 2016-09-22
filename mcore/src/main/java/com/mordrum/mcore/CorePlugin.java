package com.mordrum.mcore;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class CorePlugin implements IFMLLoadingPlugin {
    public CorePlugin() {
        MixinBootstrap.init();
        Lists.asList("mcore", new String[]{"mclimate", "mfish", "mfarm"})
                .forEach((s) -> Mixins.addConfiguration("mixins." + s + ".json"));
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
