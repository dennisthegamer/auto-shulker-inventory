package de.dennisthegamer.autoshulkerinventory.platform;

import java.nio.file.Path;

/**
 * Loader-specific functionality, implemented once per loader and resolved via {@link java.util.ServiceLoader}.
 */
public interface Platform {

    Path getConfigDir();
}
