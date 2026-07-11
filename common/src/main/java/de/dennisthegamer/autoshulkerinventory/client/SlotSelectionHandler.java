package de.dennisthegamer.autoshulkerinventory.client;

import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import de.dennisthegamer.autoshulkerinventory.mixin.client.AbstractContainerScreenAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

// Client-only by structure: referenced solely from the loader client entrypoints Ã¢â‚¬â€
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
            minecraft.player.sendSystemMessage(
                Component.translatable("message.auto_shulker_inventory.target_slot_reset"));
        } else {
            config.preferredEmptySlot = slotIndex;
            config.save();
            minecraft.player.sendSystemMessage(
                Component.translatable("message.auto_shulker_inventory.target_slot_set", slotIndex));
        }
    }

    public static void renderHighlight(InventoryScreen screen, GuiGraphicsExtractor graphics) {
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
                graphics.outline(x - 1, y - 1, 18, 18, HIGHLIGHT_COLOR);
                return;
            }
        }
    }
}
