package com.autoshulker.fabric;

import com.autoshulker.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatform implements Platform {

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
