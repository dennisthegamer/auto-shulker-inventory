package de.dennisthegamer.autoshulkerinventory.network;

/**
 * Seam between {@code ModConfig.save()} and the loader-specific client networking.
 *
 * <p>common must never call loader classes (see common/build.gradle), so the client
 * entrypoints install the actual sender here at init time. On a dedicated server
 * nobody installs one and the default no-op applies.
 *
 * <p>The hook deliberately sits in {@code save()} rather than at the individual call
 * sites: the config is written from two places -- the YACL screen and the target-slot
 * keybind in {@code SlotSelectionHandler} -- and hooking only the former would leave
 * the keybind, which is the primary way {@code preferredEmptySlot} gets set, silently
 * out of sync.
 */
public final class ConfigSync {

    private static volatile Runnable sender = () -> {};

    private ConfigSync() {
    }

    /** Installed by the loader client entrypoints. */
    public static void setSender(Runnable newSender) {
        sender = newSender != null ? newSender : () -> {};
    }

    /** Pushes the local config to the server, if we are a client and connected. */
    public static void notifyChanged() {
        sender.run();
    }
}
