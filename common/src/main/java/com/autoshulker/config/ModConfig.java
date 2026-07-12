package com.autoshulker.config;

import com.autoshulker.platform.Platforms;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(
        Platforms.get().getConfigDir().toFile(),
        "auto_shulker_inventory.json"
    );

    private static ModConfig INSTANCE = null;

    // === Feature Toggles ===

    // Main Features
    public boolean enableAutoStorage = true;  // InventoryMixin
    public boolean enableShiftClickStorage = true;  // AbstractContainerMenuMixin

    // Priority System (for AbstractContainerMenuMixin)
    public boolean enableCursorPriority = true;  // Priority 1
    public boolean enableContainerShulkerPriority = true;  // Priority 2
    public boolean enableInventoryShulkerFallback = true;  // Priority 3

    // Preferred slot to empty during auto-storage (-1 = automatic, 0-8 hotbar, 9-35 main inventory)
    public int preferredEmptySlot = -1;

    // === Additional Options ===

    public boolean enableDebugLogging = false;
    public boolean enableChatNotifications = true;
    public boolean enableSoundEffects = true;
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
                    if (config.preferredEmptySlot < -1 || config.preferredEmptySlot > 35) {
                        config.preferredEmptySlot = -1;
                    }
                    return config;
                }
            } catch (IOException | JsonParseException e) {
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
