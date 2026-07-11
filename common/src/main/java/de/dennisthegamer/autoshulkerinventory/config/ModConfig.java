package de.dennisthegamer.autoshulkerinventory.config;

import de.dennisthegamer.autoshulkerinventory.platform.Platforms;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.resources.Identifier;

public class ModConfig {

    public static final ConfigClassHandler<ModConfig> HANDLER =
            ConfigClassHandler.createBuilder(ModConfig.class)
                    .id(Identifier.fromNamespaceAndPath("inventory_shulker", "config"))
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(Platforms.get().getConfigDir()
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

    // Preferred slot to empty during auto-storage (-1 = automatic, 0-8 hotbar, 9-35 main inventory)
    @SerialEntry public int preferredEmptySlot = -1;

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

    public static void loadAndValidate() {
        HANDLER.load();
        ModConfig config = HANDLER.instance();
        if (config.preferredEmptySlot < -1 || config.preferredEmptySlot > 35) {
            config.preferredEmptySlot = -1;
            HANDLER.save();
        }
    }

    public void save() {
        HANDLER.save();
    }
}
