package de.dennisthegamer.autoshulkerinventory.network;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * C2S: the client's own config, so the server can apply that player's preferences.
 *
 * <p>The payload carries the config as a JSON string rather than field-by-field on
 * purpose -- adding a config option then never requires touching the packet format,
 * and both sides already agree on the Gson shape because it is the very same one
 * written to disk.
 *
 * <p>Deliberately built from vanilla classes only: this mod ships one jar for
 * 1.21-1.21.8, and Architectury's NetworkManager would drag in a runtime dependency
 * whose versions are pinned per MC minor (13.x=1.21/1.21.1 ... 17.x=1.21.6-8), which
 * cannot be expressed for a range this wide.
 */
public record ConfigSyncPayload(String json) implements CustomPacketPayload {

    /** Guard against oversized payloads; a full config serialises to a few hundred bytes. */
    public static final int MAX_JSON_LENGTH = 8192;

    public static final Type<ConfigSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AutoShulkerInventory.MOD_ID, "config_sync"));

    public static final StreamCodec<FriendlyByteBuf, ConfigSyncPayload> CODEC =
            CustomPacketPayload.codec(ConfigSyncPayload::write, ConfigSyncPayload::read);

    private void write(FriendlyByteBuf buf) {
        buf.writeUtf(json, MAX_JSON_LENGTH);
    }

    private static ConfigSyncPayload read(FriendlyByteBuf buf) {
        return new ConfigSyncPayload(buf.readUtf(MAX_JSON_LENGTH));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
