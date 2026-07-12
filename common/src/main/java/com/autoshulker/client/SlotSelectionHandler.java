package com.autoshulker.client;

import com.autoshulker.config.ModConfig;
import com.autoshulker.mixin.client.AbstractContainerScreenAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

// Client-only by structure: referenced solely from the loader client entrypoints —
// never annotate with @Environment/@OnlyIn (see ConfigScreen for rationale).
// 1.21-1.21.8 API notes vs the mc1.21.9-1.21.11 branch: keybind categories are
// plain translation-key strings (KeyMapping.Category exists only from 1.21.9),
// and key events arrive as (key, scancode) ints instead of a KeyEvent object.
public class SlotSelectionHandler {

    private static final int HIGHLIGHT_COLOR = 0xFF00FF00; // opaque green
    private static final int PLAYER_INVENTORY_SIZE = 36;

    public static final String KEY_CATEGORY = "key.categories.auto_shulker_inventory";

    // Assigned by the loader-specific client init after registering the mapping
    public static KeyMapping selectSlotKey;

    public static KeyMapping createKeyMapping() {
        return new KeyMapping(
            "key.auto_shulker_inventory.select_target_slot",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            KEY_CATEGORY
        );
    }

    public static void handleKeyPress(InventoryScreen screen, int key, int scancode) {
        if (selectSlotKey == null || !selectSlotKey.matches(key, scancode)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        Slot hovered = ((AbstractContainerScreenAccessor) screen).autoshulker$getHoveredSlot();
        if (hovered == null || hovered.container != minecraft.player.getInventory()) {
            return;
        }

        int slotIndex = hovered.getContainerSlot();
        if (slotIndex < 0 || slotIndex >= PLAYER_INVENTORY_SIZE) {
            return;
        }

        ModConfig config = ModConfig.getInstance();
        if (config.preferredEmptySlot == slotIndex) {
            config.preferredEmptySlot = -1;
            config.save();
            minecraft.player.displayClientMessage(
                Component.translatable("message.auto_shulker_inventory.target_slot_reset"), false);
        } else {
            config.preferredEmptySlot = slotIndex;
            config.save();
            minecraft.player.displayClientMessage(
                Component.translatable("message.auto_shulker_inventory.target_slot_set", slotIndex), false);
        }
    }

    // Called from the loader after-render screen events. Unlike 1.21.9/1.21.10,
    // the 1.21-1.21.8 GUI pipelines still display submissions made after the
    // screen render pass (verified in-game on 1.21.8), so no render mixin is
    // needed here — and no single mixin target exists across 1.21-1.21.8 anyway.
    public static void renderHighlight(InventoryScreen screen, GuiGraphics graphics) {
        int targetSlot = ModConfig.getInstance().preferredEmptySlot;
        if (targetSlot < 0 || targetSlot >= PLAYER_INVENTORY_SIZE) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        for (Slot slot : screen.getMenu().slots) {
            if (slot.container == minecraft.player.getInventory() && slot.getContainerSlot() == targetSlot) {
                int x = accessor.autoshulker$getLeftPos() + slot.x;
                int y = accessor.autoshulker$getTopPos() + slot.y;
                graphics.renderOutline(x - 1, y - 1, 18, 18, HIGHLIGHT_COLOR);
                return;
            }
        }
    }
}
