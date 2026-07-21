package de.dennisthegamer.autoshulkerinventory.neoforge;

import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import de.dennisthegamer.autoshulkerinventory.network.ConfigSync;
import de.dennisthegamer.autoshulkerinventory.network.ConfigSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

/**
 * Client half of the NeoForge config sync.
 */
public final class ConfigSyncNeoForgeClient {

    private ConfigSyncNeoForgeClient() {
    }

    public static void register() {
        ConfigSync.setSender(ConfigSyncNeoForgeClient::send);

        // Push once on join: the server needs the config before the first item pickup,
        // not only after the player next edits a setting.
        NeoForge.EVENT_BUS.addListener(ConfigSyncNeoForgeClient::onLoggingIn);
    }

    private static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        send();
    }

    private static void send() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) {
            return;
        }

        // Nothing to talk to -- a vanilla server, or one without this mod.
        if (!NetworkRegistry.hasChannel(connection, ConfigSyncPayload.TYPE.id())) {
            return;
        }

        // Sent through the vanilla packet rather than ClientPacketDistributor so this
        // file stays identical across every branch: on the 1.21-1.21.8 line no NeoForge
        // helper spans the range (PacketDistributor.sendToServer only up to 21.6,
        // ClientPacketDistributor only from 21.7). ServerboundCustomPayloadPacket and
        // ClientCommonPacketListenerImpl#send exist unchanged on all of them.
        connection.send(new ServerboundCustomPayloadPacket(
                new ConfigSyncPayload(ModConfig.getInstance().toJson())));
    }
}
