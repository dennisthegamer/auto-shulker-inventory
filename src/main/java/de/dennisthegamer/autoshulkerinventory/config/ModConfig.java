package de.dennisthegamer.autoshulkerinventory.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class ModConfig {

    public static final ConfigClassHandler<ModConfig> HANDLER =
            ConfigClassHandler.createBuilder(ModConfig.class)
                    .id(Identifier.fromNamespaceAndPath("inventory_shulker", "config"))
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(FabricLoader.getInstance().getConfigDir()
                                    .resolve("auto_shulker_inventory.json5"))
                            .setJson5(true)
                            .build())
                    .build();

    // Main Features
    @SerialEntry public boolean enableAutoStorage = true;
    @SerialEntry public boolean enableShiftClickStorage = true;

    // Priority System
    @SerialEntry public boolean enableCursorPriority = true;
    @SerialEntry public boolean enableContainerShulkerPriority = true;
    @SerialEntry public boolean enableInventoryShulkerFallback = true;

    // Feedback
    @SerialEntry public boolean enableChatNotifications = false;
    @SerialEntry public boolean enableSoundEffects = false;
    @SerialEntry public boolean enableVisualIndicators = false;
    @SerialEntry public ParticleStyle particleStyle = ParticleStyle.ENCHANT;

    // Advanced
    @SerialEntry public boolean enableDebugLogging = false;
    @SerialEntry public int storageDelayTicks = 0;

    public static ModConfig getInstance() {
        return HANDLER.instance();
    }
}
