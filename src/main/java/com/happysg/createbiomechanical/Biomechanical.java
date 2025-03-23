package com.happysg.createbiomechanical;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Biomechanical.MODID)
public class Biomechanical {
    public static final String MODID = "createbiomechanical";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Biomechanical(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Hello from Create Biomechanical!");

    }

}
