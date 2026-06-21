package de.dennisthegamer.autoshulkerinventory.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModConfigScreen {

    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.create(ModConfig.HANDLER, (defaults, config, builder) ->
                builder
                        .title(Component.translatable("config.auto_shulker_inventory.title"))

                        // Main Features
                        .category(ConfigCategory.createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.category.main_features"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.auto_shulker_inventory.enable_auto_storage"))
                                        .description(val -> OptionDescription.of(
                                                Component.translatable("config.auto_shulker_inventory.enable_auto_storage.tooltip")))
                                        .binding(defaults.enableAutoStorage, () -> config.enableAutoStorage, v -> config.enableAutoStorage = v)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.auto_shulker_inventory.enable_shift_click_storage"))
                                        .description(val -> OptionDescription.of(
                                                Component.translatable("config.auto_shulker_inventory.enable_shift_click_storage.tooltip")))
                                        .binding(defaults.enableShiftClickStorage, () -> config.enableShiftClickStorage, v -> config.enableShiftClickStorage = v)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())

                        // Priority System
                        .category(ConfigCategory.createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.category.priority_system"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.auto_shulker_inventory.enable_cursor_priority"))
                                        .description(val -> OptionDescription.of(
                                                Component.translatable("config.auto_shulker_inventory.enable_cursor_priority.tooltip")))
                                        .binding(defaults.enableCursorPriority, () -> config.enableCursorPriority, v -> config.enableCursorPriority = v)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.auto_shulker_inventory.enable_container_priority"))
                                        .description(val -> OptionDescription.of(
                                                Component.translatable("config.auto_shulker_inventory.enable_container_priority.tooltip")))
                                        .binding(defaults.enableContainerShulkerPriority, () -> config.enableContainerShulkerPriority, v -> config.enableContainerShulkerPriority = v)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.auto_shulker_inventory.enable_inventory_fallback"))
                                        .description(val -> OptionDescription.of(
                                                Component.translatable("config.auto_shulker_inventory.enable_inventory_fallback.tooltip")))
                                        .binding(defaults.enableInventoryShulkerFallback, () -> config.enableInventoryShulkerFallback, v -> config.enableInventoryShulkerFallback = v)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())

                        // Notifications & Feedback
                        .category(ConfigCategory.createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.category.feedback"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.auto_shulker_inventory.enable_chat_notifications"))
                                        .description(val -> OptionDescription.of(
                                                Component.translatable("config.auto_shulker_inventory.enable_chat_notifications.tooltip")))
                                        .binding(defaults.enableChatNotifications, () -> config.enableChatNotifications, v -> config.enableChatNotifications = v)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.auto_shulker_inventory.enable_sound_effects"))
                                        .description(val -> OptionDescription.of(
                                                Component.translatable("config.auto_shulker_inventory.enable_sound_effects.tooltip")))
                                        .binding(defaults.enableSoundEffects, () -> config.enableSoundEffects, v -> config.enableSoundEffects = v)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.auto_shulker_inventory.enable_visual_indicators"))
                                        .description(val -> OptionDescription.of(
                                                Component.translatable("config.auto_shulker_inventory.enable_visual_indicators.tooltip")))
                                        .binding(defaults.enableVisualIndicators, () -> config.enableVisualIndicators, v -> config.enableVisualIndicators = v)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<ParticleStyle>createBuilder()
                                        .name(Component.translatable("config.auto_shulker_inventory.particle_style"))
                                        .description(val -> OptionDescription.of(
                                                Component.translatable("config.auto_shulker_inventory.particle_style.tooltip")))
                                        .binding(defaults.particleStyle, () -> config.particleStyle, v -> config.particleStyle = v)
                                        .controller(opt -> EnumControllerBuilder.create(opt)
                                                .enumClass(ParticleStyle.class)
                                                .formatValue(v -> Component.translatable(v.getTranslationKey())))
                                        .build())
                                .build())

        ).generateScreen(parent);
    }
}
