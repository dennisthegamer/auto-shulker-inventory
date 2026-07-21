package de.dennisthegamer.autoshulkerinventory.platform;

import java.nio.file.Path;

/**
 * Loader-specific functionality, implemented once per loader and resolved via {@link java.util.ServiceLoader}.
 */
public interface Platform {

    Path getConfigDir();

    /**
     * Needed because YACL is only a soft dependency: the mod itself runs without it
     * (config persistence is plain Gson), but the config screen would fail with
     * NoClassDefFoundError if opened when YACL is absent.
     */
    boolean isModLoaded(String modId);
}
