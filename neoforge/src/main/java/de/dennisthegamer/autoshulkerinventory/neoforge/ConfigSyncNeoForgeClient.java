package de.dennisthegamer.autoshulkerinventory.neoforge;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import de.dennisthegamer.autoshulkerinventory.network.ConfigSync;
import de.dennisthegamer.autoshulkerinventory.network.ConfigSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Client half of the NeoForge config sync.
 */
public final class ConfigSyncNeoForgeClient {

    /**
     * {@code NetworkRegistry.hasChannel}, looked up rather than called directly.
     *
     * <p>Its only overloads take the payload id, whose mojmap name changes from
     * {@code ResourceLocation} to {@code Identifier} at 1.21.11 -- and a direct call would
     * bake that name into this class's bytecode descriptor, so the jar would resolve on
     * either 1.21.9/1.21.10 or 1.21.11 but never all three. The parameter types come from
     * live classes here, so no name is spelled out. Same reasoning as
     * {@link ConfigSyncPayload#TYPE}.
     */
    private static final Method HAS_CHANNEL = findHasChannel();

    private ConfigSyncNeoForgeClient() {
    }

    private static Method findHasChannel() {
        try {
            return NetworkRegistry.class.getMethod(
                    "hasChannel", ICommonPacketListener.class, ConfigSyncPayload.TYPE_ID.getClass());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("NetworkRegistry.hasChannel not found", e);
        }
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

        // Nothing to talk to -- a vanilla server, or one without this mod. Without this
        // check the payload would be encoded against a channel the connection never
        // negotiated.
        if (!hasChannel(connection)) {
            return;
        }

        // Sent through the vanilla packet rather than ClientPacketDistributor: the helper is
        // available across all of 21.9-21.11, but going through vanilla keeps the send path
        // the same as on the mc1.21-1.21.8 branch, where no NeoForge helper spans the range.
        // ServerboundCustomPayloadPacket and ClientCommonPacketListenerImpl#send are
        // identical across every version both branches cover.
        connection.send(new ServerboundCustomPayloadPacket(
                new ConfigSyncPayload(ModConfig.getInstance().toJson())));
    }

    private static boolean hasChannel(ClientPacketListener connection) {
        try {
            return (boolean) HAS_CHANNEL.invoke(null, connection, ConfigSyncPayload.TYPE_ID);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // Staying quiet would mean silently never syncing; better to skip this send and
            // say why.
            AutoShulkerInventory.LOGGER.error("Could not query the config sync channel", e);
            return false;
        }
    }
}
