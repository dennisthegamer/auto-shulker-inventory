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
// The YACL config screen is unaffected -- ConfigScreen binds to this instance
// manually and calls save() itself.
public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ModConfig INSTANCE;

    private static Path configPath() {
        return Platforms.get().getConfigDir().resolve("auto_shulker_inventory.json");
    }

    /** Pre-Gson installs wrote json5 through YACL; read it once so settings survive the upgrade. */
    private static Path legacyPath() {
        return Platforms.get().getConfigDir().resolve("auto_shulker_inventory.json5");
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

    // Feedback
    public boolean enableChatNotifications = false;
    public boolean enableSoundEffects = false;
    public boolean enableVisualIndicators = false;
    public ParticleStyle particleStyle = ParticleStyle.ENCHANT;

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
        Path current = configPath();
        Path legacy = legacyPath();
        Path readFrom = Files.exists(current) ? current
                      : Files.exists(legacy) ? legacy
                      : null;

        ModConfig config = null;
        if (readFrom != null) {
            try {
                // Gson reads leniently, so this also copes with the json5 file
                // YACL used to write (comments, trailing commas).
                config = GSON.fromJson(Files.readString(readFrom), ModConfig.class);
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

        // Materialise the .json file on a fresh install and after a json5 migration.
        if (!Files.exists(current)) {
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
