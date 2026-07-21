package de.dennisthegamer.autoshulkerinventory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import de.dennisthegamer.autoshulkerinventory.network.ConfigSync;
import de.dennisthegamer.autoshulkerinventory.network.ServerConfigStore;
import de.dennisthegamer.autoshulkerinventory.platform.Platforms;
import net.minecraft.world.entity.player.Player;

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

    /**
     * The config that applies to {@code player}.
     *
     * <p>Every option here is a personal preference, not a server rule, so server-side
     * code must not read the server's own file for a player: on a dedicated server that
     * made every client's setting inert (a client asking for slot 10 got the server's -1).
     *
     * <p>Falls back to {@link #getInstance()} when no config was received -- on the client
     * and in singleplayer that is the player's own file anyway, and on a dedicated server
     * it means players without the mod keep being served by the server defaults.
     */
    public static ModConfig forPlayer(Player player) {
        if (player == null || player.level().isClientSide()) {
            return getInstance();
        }
        return ServerConfigStore.get(player.getUUID()).orElseGet(ModConfig::getInstance);
    }

    /**
     * Parses a config received over the network. Returns {@code null} if the JSON is
     * malformed -- callers must treat that as "no config" rather than propagating.
     */
    public static ModConfig fromJson(String json) {
        if (json == null || json.length() > 8192) {
            return null;
        }
        try {
            ModConfig config = GSON.fromJson(json, ModConfig.class);
            if (config == null) {
                return null;
            }
            config.validate();
            return config;
        } catch (RuntimeException e) {
            // JsonParseException and friends -- a hostile client must not reach further.
            return null;
        }
    }

    /** Clamps values that arrive from disk or from the network into their valid range. */
    private void validate() {
        if (preferredEmptySlot < -1 || preferredEmptySlot > 35) {
            preferredEmptySlot = -1;
        }
        if (storageDelayTicks < 0 || storageDelayTicks > 20) {
            storageDelayTicks = 0;
        }
        if (particleStyle == null) {
            particleStyle = ParticleStyle.ENCHANT;
        }
    }

    /** Serialises this config for the sync payload. */
    public String toJson() {
        return GSON.toJson(this);
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

        config.validate();

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

        // Hooked here rather than at the call sites because the config is written from
        // two places -- the YACL screen and the target-slot keybind -- and the keybind is
        // the primary way preferredEmptySlot gets set. Guarded to INSTANCE so configs
        // received from clients (server side) never echo back out.
        if (this == INSTANCE) {
            ConfigSync.notifyChanged();
        }
    }
}
