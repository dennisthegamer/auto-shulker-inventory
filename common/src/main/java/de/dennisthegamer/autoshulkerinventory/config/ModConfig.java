package de.dennisthegamer.autoshulkerinventory.config;

import de.dennisthegamer.autoshulkerinventory.platform.Platforms;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.resources.ResourceLocation;

public class ModConfig {

    // Keeps the pre-existing config path/format (plain JSON at auto_shulker_inventory.json)
    // so users upgrading from the Cloth Config builds keep their settings.
    public static final ConfigClassHandler<ModConfig> HANDLER =
            ConfigClassHandler.createBuilder(ModConfig.class)
                    .id(ResourceLocation.fromNamespaceAndPath("inventory_shulker", "config"))
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(Platforms.get().getConfigDir()
                                    .resolve("auto_shulker_inventory.json"))
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

    // Notifications & Feedback
    @SerialEntry public boolean enableChatNotifications = true;
    @SerialEntry public boolean enableSoundEffects = true;
    @SerialEntry public boolean enableVisualIndicators = false;

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
