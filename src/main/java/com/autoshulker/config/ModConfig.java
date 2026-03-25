package com.autoshulker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(
        FabricLoader.getInstance().getConfigDir().toFile(),
        "auto_shulker_inventory.json"
    );

    private static ModConfig INSTANCE = null;

    // === Feature Toggles ===

    // Main Features
    public boolean enableAutoStorage = true;  // PlayerInventoryMixin
    public boolean enableShiftClickStorage = true;  // ScreenHandlerMixin

    // Priority System (for ScreenHandlerMixin)
    public boolean enableCursorPriority = true;  // Priority 1
    public boolean enableContainerShulkerPriority = true;  // Priority 2
    public boolean enableInventoryShulkerFallback = true;  // Priority 3

    // === Additional Options ===

    public boolean enableDebugLogging = false;
    // TODO: Re-enable after Cloth Config is updated to support 26.1
    public boolean enableChatNotifications = false;
    public boolean enableSoundEffects = false;
    public boolean enableVisualIndicators = false;  // Future: particle effects

    // Advanced Options
    public int storageDelayTicks = 0;  // Delay before auto-storage (0 = instant)

    // === Config Management ===

    public static ModConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static ModConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (IOException e) {
                System.err.println("Failed to load config, using defaults: " + e.getMessage());
            }
        }

        // Return default config
        ModConfig config = new ModConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    public void reset() {
        INSTANCE = new ModConfig();
        INSTANCE.save();
    }
}
