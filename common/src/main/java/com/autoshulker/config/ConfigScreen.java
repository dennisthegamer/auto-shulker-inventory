package com.autoshulker.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// Client-only by structure: referenced solely from the ModMenu entrypoint (Fabric)
// and the Dist.CLIENT @Mod class (NeoForge) — never annotate with @Environment.
public class ConfigScreen {

    public static Screen create(Screen parent) {
        ModConfig config = ModConfig.getInstance();

        // Create the config builder
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("config.auto_shulker_inventory.title"))
            .setSavingRunnable(() -> {
                config.save();
            });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // === MAIN FEATURES CATEGORY ===
        ConfigCategory mainFeatures = builder.getOrCreateCategory(
            Component.translatable("config.auto_shulker_inventory.category.main_features")
        );

        mainFeatures.addEntry(entryBuilder.startBooleanToggle(
            Component.translatable("config.auto_shulker_inventory.enable_auto_storage"),
            config.enableAutoStorage
        )
            .setDefaultValue(true)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_auto_storage.tooltip"))
            .setSaveConsumer(value -> config.enableAutoStorage = value)
            .build());

        mainFeatures.addEntry(entryBuilder.startBooleanToggle(
            Component.translatable("config.auto_shulker_inventory.enable_shift_click_storage"),
            config.enableShiftClickStorage
        )
            .setDefaultValue(true)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_shift_click_storage.tooltip"))
            .setSaveConsumer(value -> config.enableShiftClickStorage = value)
            .build());

        // === PRIORITY SYSTEM CATEGORY ===
        ConfigCategory prioritySystem = builder.getOrCreateCategory(
            Component.translatable("config.auto_shulker_inventory.category.priority_system")
        );

        prioritySystem.addEntry(entryBuilder.startBooleanToggle(
            Component.translatable("config.auto_shulker_inventory.enable_cursor_priority"),
            config.enableCursorPriority
        )
            .setDefaultValue(true)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_cursor_priority.tooltip"))
            .setSaveConsumer(value -> config.enableCursorPriority = value)
            .build());

        prioritySystem.addEntry(entryBuilder.startBooleanToggle(
            Component.translatable("config.auto_shulker_inventory.enable_container_priority"),
            config.enableContainerShulkerPriority
        )
            .setDefaultValue(true)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_container_priority.tooltip"))
            .setSaveConsumer(value -> config.enableContainerShulkerPriority = value)
            .build());

        prioritySystem.addEntry(entryBuilder.startBooleanToggle(
            Component.translatable("config.auto_shulker_inventory.enable_inventory_fallback"),
            config.enableInventoryShulkerFallback
        )
            .setDefaultValue(true)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_inventory_fallback.tooltip"))
            .setSaveConsumer(value -> config.enableInventoryShulkerFallback = value)
            .build());

        // === NOTIFICATIONS & FEEDBACK CATEGORY ===
        ConfigCategory feedback = builder.getOrCreateCategory(
            Component.translatable("config.auto_shulker_inventory.category.feedback")
        );

        feedback.addEntry(entryBuilder.startBooleanToggle(
            Component.translatable("config.auto_shulker_inventory.enable_chat_notifications"),
            config.enableChatNotifications
        )
            .setDefaultValue(true)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_chat_notifications.tooltip"))
            .setSaveConsumer(value -> config.enableChatNotifications = value)
            .build());

        feedback.addEntry(entryBuilder.startBooleanToggle(
            Component.translatable("config.auto_shulker_inventory.enable_sound_effects"),
            config.enableSoundEffects
        )
            .setDefaultValue(true)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_sound_effects.tooltip"))
            .setSaveConsumer(value -> config.enableSoundEffects = value)
            .build());

        feedback.addEntry(entryBuilder.startBooleanToggle(
            Component.translatable("config.auto_shulker_inventory.enable_visual_indicators"),
            config.enableVisualIndicators
        )
            .setDefaultValue(false)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_visual_indicators.tooltip"))
            .setSaveConsumer(value -> config.enableVisualIndicators = value)
            .build());

        // === ADVANCED OPTIONS CATEGORY ===
        ConfigCategory advanced = builder.getOrCreateCategory(
            Component.translatable("config.auto_shulker_inventory.category.advanced")
        );

        advanced.addEntry(entryBuilder.startBooleanToggle(
            Component.translatable("config.auto_shulker_inventory.enable_debug_logging"),
            config.enableDebugLogging
        )
            .setDefaultValue(false)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_debug_logging.tooltip"))
            .setSaveConsumer(value -> config.enableDebugLogging = value)
            .build());

        advanced.addEntry(entryBuilder.startIntField(
            Component.translatable("config.auto_shulker_inventory.storage_delay_ticks"),
            config.storageDelayTicks
        )
            .setDefaultValue(0)
            .setMin(0)
            .setMax(20)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.storage_delay_ticks.tooltip"))
            .setSaveConsumer(value -> config.storageDelayTicks = value)
            .build());

        return builder.build();
    }
}
