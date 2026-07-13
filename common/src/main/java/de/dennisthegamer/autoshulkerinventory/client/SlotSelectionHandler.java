package de.dennisthegamer.autoshulkerinventory.client;

import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import de.dennisthegamer.autoshulkerinventory.mixin.client.AbstractContainerScreenAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

// Client-only by structure: referenced solely from the loader client entrypoints —
// never annotate with @Environment/@OnlyIn (see ConfigScreen for rationale).
public class SlotSelectionHandler {

    private static final int HIGHLIGHT_COLOR = 0xFF00FF00; // opaque green
    private static final int PLAYER_INVENTORY_SIZE = 36;

    // Assigned by the loader-specific client init after registering the mapping
    public static KeyMapping selectSlotKey;

    public static KeyMapping createKeyMapping(KeyMapping.Category category) {
        return new KeyMapping(
            "key.auto_shulker_inventory.select_target_slot",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            category
        );
    }

    public static void handleKeyPress(InventoryScreen screen, KeyEvent keyEvent) {
        if (selectSlotKey == null || !selectSlotKey.matches(keyEvent)) {
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
                int x0 = accessor.autoshulker$getLeftPos() + slot.x - 1;
                int y0 = accessor.autoshulker$getTopPos() + slot.y - 1;
                int x1 = x0 + 18;
                int y1 = y0 + 18;
                // Drawn with fill() instead of renderOutline(): NeoForge jars run on
                // mojmap names of the RUNTIME version, and Mojang renamed the method
                // between 1.21.10 (submitOutline) and 1.21.11 (renderOutline) —
                // fill() keeps its name across 1.21.9-1.21.11.
                graphics.fill(x0, y0, x1, y0 + 1, HIGHLIGHT_COLOR);
                graphics.fill(x0, y1 - 1, x1, y1, HIGHLIGHT_COLOR);
                graphics.fill(x0, y0 + 1, x0 + 1, y1 - 1, HIGHLIGHT_COLOR);
                graphics.fill(x1 - 1, y0 + 1, x1, y1 - 1, HIGHLIGHT_COLOR);
                return;
            }
        }
    }
}
