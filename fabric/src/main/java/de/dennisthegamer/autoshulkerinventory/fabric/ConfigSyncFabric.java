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
        //
        // Note for backports: Fabric API 5.x (1.21.x) calls this playC2S(); it was
        // renamed to serverboundPlay() in 6.x, which is what the 26.x branches use.
        PayloadTypeRegistry.serverboundPlay().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC);

        // Fabric invokes play payload handlers on the server thread.
        ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.TYPE,
                (payload, context) -> ServerConfigHandler.receive(context.player(), payload.json()));

        ServerPlayConnectionEvents.DISCONNECT.register(
                (listener, server) -> ServerConfigHandler.disconnect(listener.getPlayer()));
    }
}
