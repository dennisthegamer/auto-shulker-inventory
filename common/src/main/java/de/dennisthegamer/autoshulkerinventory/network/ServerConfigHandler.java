package de.dennisthegamer.autoshulkerinventory.network;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server-side reception logic, shared by both loaders so the two network layers stay
 * thin adapters around the same behaviour.
 */
public final class ServerConfigHandler {

    private ServerConfigHandler() {
    }

    public static void receive(ServerPlayer player, String json) {
        if (player == null) {
            return;
        }

        // Never deserialise unchecked: the string comes off the wire, and a malformed
        // or hostile payload must not take the server down -- it just leaves the
        // player on the server defaults.
        ModConfig config = ModConfig.fromJson(json);
        if (config == null) {
            AutoShulkerInventory.LOGGER.warn("Discarded malformed config sync from {}", player.getGameProfile().getName());
            return;
        }

        ServerConfigStore.put(player.getUUID(), config);
    }

    public static void disconnect(ServerPlayer player) {
        if (player != null) {
            ServerConfigStore.remove(player.getUUID());
        }
    }
}
