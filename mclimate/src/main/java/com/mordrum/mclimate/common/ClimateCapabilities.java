package com.mordrum.mclimate.common;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ClimateCapabilities {
    @CapabilityInject(ITemperature.class)
    public static final Capability<ITemperature> temperatureCapability = null;
}
