package com.modmountain.gameseer.plugins.forge;

import com.modmountain.gameseer.util.SystemOutForwarder;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.io.PrintStream;
import java.util.Map;

public class CorePlugin implements IFMLLoadingPlugin {
    public CorePlugin() {
        PrintStream out = System.out;
        System.setOut(new SystemOutForwarder(out));
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
