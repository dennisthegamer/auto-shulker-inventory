package de.dennisthegamer.autoshulkerinventory.platform;

import java.util.ServiceLoader;

public final class Platforms {

    private static final Platform INSTANCE = ServiceLoader.load(Platform.class)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "No Platform implementation found on the classpath. "
                + "A loader-specific META-INF/services entry is missing."));

    private Platforms() {
    }

    public static Platform get() {
        return INSTANCE;
    }
}
