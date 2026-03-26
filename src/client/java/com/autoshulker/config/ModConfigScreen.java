package com.autoshulker.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModConfigScreen {

    public static Screen create(Screen parent) {
        ModConfig config = ModConfig.getInstance();
        ModConfig defaults = new ModConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.auto_shulker_inventory.title"))
                .setSavingRunnable(config::save);

        ConfigEntryBuilder entry = builder.entryBuilder();

        // === Main Features ===
        ConfigCategory main = builder.getOrCreateCategory(
                Component.translatable("config.auto_shulker_inventory.category.main_features"));

        main.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.auto_shulker_inventory.enable_auto_storage"),
                        config.enableAutoStorage)
                .setDefaultValue(defaults.enableAutoStorage)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_auto_storage.tooltip"))
                .setSaveConsumer(v -> config.enableAutoStorage = v)
                .build());

        main.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.auto_shulker_inventory.enable_shift_click_storage"),
                        config.enableShiftClickStorage)
                .setDefaultValue(defaults.enableShiftClickStorage)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_shift_click_storage.tooltip"))
                .setSaveConsumer(v -> config.enableShiftClickStorage = v)
                .build());

        // === Priority System ===
        ConfigCategory priority = builder.getOrCreateCategory(
                Component.translatable("config.auto_shulker_inventory.category.priority_system"));

        priority.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.auto_shulker_inventory.enable_cursor_priority"),
                        config.enableCursorPriority)
                .setDefaultValue(defaults.enableCursorPriority)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_cursor_priority.tooltip"))
                .setSaveConsumer(v -> config.enableCursorPriority = v)
                .build());

        priority.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.auto_shulker_inventory.enable_container_priority"),
                        config.enableContainerShulkerPriority)
                .setDefaultValue(defaults.enableContainerShulkerPriority)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_container_priority.tooltip"))
                .setSaveConsumer(v -> config.enableContainerShulkerPriority = v)
                .build());

        priority.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.auto_shulker_inventory.enable_inventory_fallback"),
                        config.enableInventoryShulkerFallback)
                .setDefaultValue(defaults.enableInventoryShulkerFallback)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_inventory_fallback.tooltip"))
                .setSaveConsumer(v -> config.enableInventoryShulkerFallback = v)
                .build());

        // === Notifications & Feedback ===
        ConfigCategory options = builder.getOrCreateCategory(
                Component.translatable("config.auto_shulker_inventory.category.feedback"));

        options.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.auto_shulker_inventory.enable_chat_notifications"),
                        config.enableChatNotifications)
                .setDefaultValue(defaults.enableChatNotifications)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_chat_notifications.tooltip"))
                .setSaveConsumer(v -> config.enableChatNotifications = v)
                .build());

        options.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.auto_shulker_inventory.enable_sound_effects"),
                        config.enableSoundEffects)
                .setDefaultValue(defaults.enableSoundEffects)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_sound_effects.tooltip"))
                .setSaveConsumer(v -> config.enableSoundEffects = v)
                .build());

        options.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.auto_shulker_inventory.enable_visual_indicators"),
                        config.enableVisualIndicators)
                .setDefaultValue(defaults.enableVisualIndicators)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_visual_indicators.tooltip"))
                .setSaveConsumer(v -> config.enableVisualIndicators = v)
                .build());

        options.addEntry(entry.startEnumSelector(
                        Component.translatable("config.auto_shulker_inventory.particle_style"),
                        ParticleStyle.class,
                        config.particleStyle != null ? config.particleStyle : ParticleStyle.ENCHANT)
                .setDefaultValue(defaults.particleStyle)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.particle_style.tooltip"))
                .setEnumNameProvider(e -> Component.translatable(((ParticleStyle) e).getTranslationKey()))
                .setSaveConsumer(v -> config.particleStyle = v)
                .build());

        // === Advanced Options ===
        ConfigCategory advanced = builder.getOrCreateCategory(
                Component.translatable("config.auto_shulker_inventory.category.advanced"));

        advanced.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.auto_shulker_inventory.enable_debug_logging"),
                        config.enableDebugLogging)
                .setDefaultValue(defaults.enableDebugLogging)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.enable_debug_logging.tooltip"))
                .setSaveConsumer(v -> config.enableDebugLogging = v)
                .build());

        advanced.addEntry(entry.startIntSlider(
                        Component.translatable("config.auto_shulker_inventory.storage_delay_ticks"),
                        config.storageDelayTicks, 0, 40)
                .setDefaultValue(defaults.storageDelayTicks)
                .setTooltip(Component.translatable("config.auto_shulker_inventory.storage_delay_ticks.tooltip"))
                .setSaveConsumer(v -> config.storageDelayTicks = v)
                .build());

        return builder.build();
    }
}
