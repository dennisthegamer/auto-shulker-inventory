package de.dennisthegamer.autoshulkerinventory.neoforge;

import de.dennisthegamer.autoshulkerinventory.network.ConfigSyncPayload;
import de.dennisthegamer.autoshulkerinventory.network.ServerConfigHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Side-neutral half of the NeoForge config sync.
 */
public final class ConfigSyncNeoForge {

    private ConfigSyncNeoForge() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ConfigSyncNeoForge::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(ConfigSyncNeoForge::onLoggedOut);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        // optional() is not a nicety: "If any non-optional payloads are missing during a
        // connection attempt, the connection will fail" -- without it every player who
        // does not have this mod would be kicked off a server that does.
        event.registrar("1").optional().playToServer(
                ConfigSyncPayload.TYPE,
                ConfigSyncPayload.CODEC,
                (payload, context) -> {
                    // PayloadRegistrar wraps handlers onto the main thread by default.
                    if (context.player() instanceof ServerPlayer serverPlayer) {
                        ServerConfigHandler.receive(serverPlayer, payload.json());
                    }
                });
    }

    private static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerConfigHandler.disconnect(serverPlayer);
        }
    }
}
