package de.dennisthegamer.autoshulkerinventory.fabric;

import de.dennisthegamer.autoshulkerinventory.network.ConfigSyncPayload;
import de.dennisthegamer.autoshulkerinventory.network.ServerConfigHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Side-neutral half of the Fabric config sync. Called from the main entrypoint,
 * which runs on both the client and the dedicated server.
 */
public final class ConfigSyncFabric {

    private ConfigSyncFabric() {
    }

    public static void register() {
        // The payload type must be registered on BOTH ends -- the client to encode,
        // the server to decode -- which is why this sits in the main entrypoint and
        // not in the client one.
        PayloadTypeRegistry.playC2S().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC);

        // Fabric invokes play payload handlers on the server thread.
        ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.TYPE,
                (payload, context) -> ServerConfigHandler.receive(context.player(), payload.json()));

        ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> ServerConfigHandler.disconnect(handler.getPlayer()));
    }
}
