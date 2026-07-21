package de.dennisthegamer.autoshulkerinventory.network;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.lang.reflect.Constructor;

/**
 * C2S: the client's own config, so the server can apply that player's preferences.
 *
 * <p>The payload carries the config as a JSON string rather than field-by-field on
 * purpose -- adding a config option then never requires touching the packet format,
 * and both sides already agree on the Gson shape because it is the very same one
 * written to disk.
 *
 * <p>Deliberately built from vanilla classes only: Architectury's NetworkManager would
 * drag in a runtime dependency whose versions are pinned per MC minor, which cannot be
 * expressed for a range this wide.
 */
public record ConfigSyncPayload(String json) implements CustomPacketPayload {

    /** Guard against oversized payloads; a full config serialises to a few hundred bytes. */
    public static final int MAX_JSON_LENGTH = 8192;

    public static final Type<ConfigSyncPayload> TYPE;

    /**
     * The payload id, deliberately typed as {@link Object}.
     *
     * <p>Loader code needs to hand this to APIs that take the id (NeoForge's channel check),
     * but naming its type -- even only as a local variable or a call's return value -- would
     * bake the renamed class into a bytecode descriptor. See {@link #TYPE}.
     */
    public static final Object TYPE_ID;

    public static final StreamCodec<FriendlyByteBuf, ConfigSyncPayload> CODEC =
            CustomPacketPayload.codec(ConfigSyncPayload::write, ConfigSyncPayload::read);

    /**
     * Builds the payload id without ever naming the id class.
     *
     * <p>This jar spans the mojmap rename of {@code ResourceLocation} to {@code Identifier}
     * at 1.21.10 -> 1.21.11 (same package, new name). It only bites on NeoForge, which runs
     * on the runtime version's mojmap names -- Fabric jars are remapped to intermediary and
     * never see it -- so naming either class would cap the NeoForge jar at part of the range.
     *
     * <p>Nothing here is a name. {@code Type} itself is never renamed, so its canonical
     * constructor yields the id class as a parameter type, and the id is then built through
     * its own {@code (namespace, path)} constructor -- constructors have no name to rename.
     * That also keeps it correct on Fabric, where reflective *strings* would not be remapped.
     *
     * <p>Note what does NOT work, both of which the bytecode verifier caught: calling
     * {@code Type#id()} bakes the renamed class into the call site's return descriptor even
     * though the source never spells it, and handing {@code CustomPacketPayload.createType}
     * the whole id resolves through {@code withDefaultNamespace}, which NeoForge then refuses
     * to register ("Cannot register payload ... using the domain \"minecraft\"").
     */
    static {
        try {
            Constructor<?> typeCtor = Type.class.getDeclaredConstructors()[0];
            Class<?> idClass = typeCtor.getParameterTypes()[0];

            Constructor<?> idCtor = idClass.getDeclaredConstructor(String.class, String.class);
            idCtor.setAccessible(true);
            Object id = idCtor.newInstance(AutoShulkerInventory.MOD_ID, "config_sync");

            // Fails loudly at class init rather than handing out a silently wrong channel,
            // should the argument order ever change.
            String expected = AutoShulkerInventory.MOD_ID + ":config_sync";
            if (!expected.equals(id.toString())) {
                throw new IllegalStateException("Built payload id " + id + ", expected " + expected);
            }

            typeCtor.setAccessible(true);
            @SuppressWarnings("unchecked")
            Type<ConfigSyncPayload> type = (Type<ConfigSyncPayload>) typeCtor.newInstance(id);

            TYPE_ID = id;
            TYPE = type;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not build the config sync payload id", e);
        }
    }

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
