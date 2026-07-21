package de.dennisthegamer.autoshulkerinventory.fabric;

import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import de.dennisthegamer.autoshulkerinventory.network.ConfigSync;
import de.dennisthegamer.autoshulkerinventory.network.ConfigSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Client half of the Fabric config sync. Kept apart from {@link ConfigSyncFabric} so
 * the dedicated server never has to resolve client-only networking classes.
 */
public final class ConfigSyncFabricClient {

    private ConfigSyncFabricClient() {
    }

    public static void register() {
        ConfigSync.setSender(() -> {
            // Guards both "not connected" and "server does not know this channel",
            // so a vanilla or mod-less server just never hears from us.
            if (ClientPlayNetworking.canSend(ConfigSyncPayload.TYPE)) {
                ClientPlayNetworking.send(new ConfigSyncPayload(ModConfig.getInstance().toJson()));
            }
        });

        // Push once on join: the server needs the config before the first item pickup,
        // not only after the player next edits a setting.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ConfigSync.notifyChanged());
    }
}
