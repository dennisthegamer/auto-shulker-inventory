package de.dennisthegamer.autoshulkerinventory.network;

import de.dennisthegamer.autoshulkerinventory.config.ModConfig;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-player configs as received from clients, held for the lifetime of their connection.
 *
 * <p>Every option this mod has is a personal preference, not a server rule, so a single
 * set of values on the server cannot represent them. Entries are dropped on disconnect --
 * without that the map grows for the whole server uptime.
 *
 * <p>Concurrent because payload handlers may touch it off the main thread depending on
 * loader; reads happen from the server thread during item pickup.
 */
public final class ServerConfigStore {

    private static final Map<UUID, ModConfig> CONFIGS = new ConcurrentHashMap<>();

    private ServerConfigStore() {
    }

    public static void put(UUID playerId, ModConfig config) {
        CONFIGS.put(playerId, config);
    }

    public static Optional<ModConfig> get(UUID playerId) {
        return Optional.ofNullable(CONFIGS.get(playerId));
    }

    public static void remove(UUID playerId) {
        CONFIGS.remove(playerId);
    }

    /** Integrated server shutdown -- the next world may have different players. */
    public static void clear() {
        CONFIGS.clear();
    }
}
