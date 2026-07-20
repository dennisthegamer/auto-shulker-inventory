package de.dennisthegamer.autoshulkerinventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import de.dennisthegamer.autoshulkerinventory.platform.Platforms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Persistence is plain Gson, NOT YACL's ConfigClassHandler.
//
// This class is touched on the dedicated server: InventoryMixin and
// AbstractContainerMenuMixin run there and call getInstance() on every item
// pickup. YACL is a client-only library, so the static ConfigClassHandler field
// that used to live here loaded dev.isxander classes on first access and killed
// the server with NoClassDefFoundError the moment a player picked anything up.
// Gson ships with Minecraft and exists on both sides.
//
// The file path and format are unchanged (plain JSON at
// auto_shulker_inventory.json) -- YACL wrote the very same shape here, so
// existing configs are read as-is with no migration.
//
// The YACL config screen is unaffected -- ConfigScreen binds to this instance
// manually and calls save() itself.
public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ModConfig INSTANCE;

    private static Path configPath() {
        return Platforms.get().getConfigDir().resolve("auto_shulker_inventory.json");
    }

    // Main Features
    public boolean enableAutoStorage = true;
    public boolean enableShiftClickStorage = true;

    // Priority System
    public boolean enableCursorPriority = true;
    public boolean enableContainerShulkerPriority = true;
    public boolean enableInventoryShulkerFallback = true;

    // Preferred slot to empty during auto-storage (-1 = automatic, 0-8 hotbar, 9-35 main inventory)
    public int preferredEmptySlot = -1;

    // Notifications & Feedback
    public boolean enableChatNotifications = true;
    public boolean enableSoundEffects = true;
    public boolean enableVisualIndicators = false;

    // Advanced
    public boolean enableDebugLogging = false;
    public int storageDelayTicks = 0;

    public static ModConfig getInstance() {
        if (INSTANCE == null) {
            loadAndValidate();
        }
        return INSTANCE;
    }

    public static void loadAndValidate() {
        Path path = configPath();

        ModConfig config = null;
        if (Files.exists(path)) {
            try {
                config = GSON.fromJson(Files.readString(path), ModConfig.class);
            } catch (IOException | RuntimeException e) {
                AutoShulkerInventory.LOGGER.error("Failed to load config, using defaults", e);
            }
        }
        if (config == null) {
            config = new ModConfig();
        }

        if (config.preferredEmptySlot < -1 || config.preferredEmptySlot > 35) {
            config.preferredEmptySlot = -1;
        }

        INSTANCE = config;

        if (!Files.exists(path)) {
            INSTANCE.save();
        }
    }

    public void save() {
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(this));
        } catch (IOException e) {
            AutoShulkerInventory.LOGGER.error("Failed to save config", e);
        }
    }
}
