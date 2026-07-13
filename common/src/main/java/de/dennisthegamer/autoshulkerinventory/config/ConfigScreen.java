package de.dennisthegamer.autoshulkerinventory.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// Client-only by structure: referenced solely from the ModMenu entrypoint (Fabric)
// and the Dist.CLIENT @Mod class (NeoForge) — never annotate with @Environment,
// NeoForge 21.9+ shows a loading warning for the resulting @OnlyIn.
//
// This branch spans the ResourceLocation -> Identifier mojmap rename (1.21.10 -> 1.21.11),
// so it deliberately avoids YACL's ConfigClassHandler (which requires an Identifier/ResourceLocation).
// ModConfig keeps its own Gson persistence and the screen binds to it manually via .save(config::save);
// that keeps the shared/NeoForge jar free of any renamed class, so it runs on all of 1.21.9-1.21.11.
public class ConfigScreen {

    public static Screen create(Screen parent) {
        ModConfig config = ModConfig.getInstance();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("config.auto_shulker_inventory.title"))

                // Main Features
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("config.auto_shulker_inventory.category.main_features"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.enable_auto_storage"))
                                .description(val -> OptionDescription.of(
                                        Component.translatable("config.auto_shulker_inventory.enable_auto_storage.tooltip")))
                                .binding(true, () -> config.enableAutoStorage, v -> config.enableAutoStorage = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.enable_shift_click_storage"))
                                .description(val -> OptionDescription.of(
                                        Component.translatable("config.auto_shulker_inventory.enable_shift_click_storage.tooltip")))
                                .binding(true, () -> config.enableShiftClickStorage, v -> config.enableShiftClickStorage = v)
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
                                .binding(true, () -> config.enableCursorPriority, v -> config.enableCursorPriority = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.enable_container_priority"))
                                .description(val -> OptionDescription.of(
                                        Component.translatable("config.auto_shulker_inventory.enable_container_priority.tooltip")))
                                .binding(true, () -> config.enableContainerShulkerPriority, v -> config.enableContainerShulkerPriority = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.enable_inventory_fallback"))
                                .description(val -> OptionDescription.of(
                                        Component.translatable("config.auto_shulker_inventory.enable_inventory_fallback.tooltip")))
                                .binding(true, () -> config.enableInventoryShulkerFallback, v -> config.enableInventoryShulkerFallback = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.preferred_empty_slot"))
                                .description(val -> OptionDescription.of(
                                        Component.translatable("config.auto_shulker_inventory.preferred_empty_slot.tooltip")))
                                .binding(-1, () -> config.preferredEmptySlot, v -> config.preferredEmptySlot = v)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(-1, 35))
                                .build())
                        .build())

                // Notifications & Feedback
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("config.auto_shulker_inventory.category.feedback"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.enable_chat_notifications"))
                                .description(val -> OptionDescription.of(
                                        Component.translatable("config.auto_shulker_inventory.enable_chat_notifications.tooltip")))
                                .binding(true, () -> config.enableChatNotifications, v -> config.enableChatNotifications = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.enable_sound_effects"))
                                .description(val -> OptionDescription.of(
                                        Component.translatable("config.auto_shulker_inventory.enable_sound_effects.tooltip")))
                                .binding(true, () -> config.enableSoundEffects, v -> config.enableSoundEffects = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.enable_visual_indicators"))
                                .description(val -> OptionDescription.of(
                                        Component.translatable("config.auto_shulker_inventory.enable_visual_indicators.tooltip")))
                                .binding(false, () -> config.enableVisualIndicators, v -> config.enableVisualIndicators = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .build())

                // Advanced
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("config.auto_shulker_inventory.category.advanced"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.enable_debug_logging"))
                                .description(val -> OptionDescription.of(
                                        Component.translatable("config.auto_shulker_inventory.enable_debug_logging.tooltip")))
                                .binding(false, () -> config.enableDebugLogging, v -> config.enableDebugLogging = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("config.auto_shulker_inventory.storage_delay_ticks"))
                                .description(val -> OptionDescription.of(
                                        Component.translatable("config.auto_shulker_inventory.storage_delay_ticks.tooltip")))
                                .binding(0, () -> config.storageDelayTicks, v -> config.storageDelayTicks = v)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(0, 20))
                                .build())
                        .build())

                .save(config::save)
                .build()
                .generateScreen(parent);
    }
}
