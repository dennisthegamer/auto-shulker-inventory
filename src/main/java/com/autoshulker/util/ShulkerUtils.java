package com.autoshulker.util;

import com.autoshulker.config.ModConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class ShulkerUtils {

    public static boolean isShulkerBox(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        return blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    public static ContainerComponent getShulkerContents(ItemStack shulkerStack) {
        if (!isShulkerBox(shulkerStack)) {
            return ContainerComponent.DEFAULT;
        }

        return shulkerStack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
    }

    public static boolean hasSpace(ItemStack shulkerStack) {
        ContainerComponent contents = getShulkerContents(shulkerStack);

        List<ItemStack> items = contents.stream().toList();

        if (items.size() < 27) {
            return true;
        }

        for (ItemStack item : items) {
            if (item.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public static ItemStack insertItem(ItemStack shulkerStack, ItemStack toInsert) {
        if (!isShulkerBox(shulkerStack) || toInsert.isEmpty()) {
            return toInsert;
        }

        ContainerComponent contents = getShulkerContents(shulkerStack);
        List<ItemStack> items = new ArrayList<>(contents.stream().toList());

        while (items.size() < 27) {
            items.add(ItemStack.EMPTY);
        }

        ItemStack remaining = toInsert.copy();

        for (int i = 0; i < 27 && !remaining.isEmpty(); i++) {
            ItemStack slotStack = items.get(i);

            if (!slotStack.isEmpty() && ItemStack.areItemsAndComponentsEqual(slotStack, remaining)) {
                int maxStackSize = slotStack.getMaxCount();
                int currentCount = slotStack.getCount();
                int spaceLeft = maxStackSize - currentCount;

                if (spaceLeft > 0) {
                    int toTransfer = Math.min(spaceLeft, remaining.getCount());
                    slotStack.setCount(currentCount + toTransfer);
                    remaining.decrement(toTransfer);
                }
            }
        }

        for (int i = 0; i < 27 && !remaining.isEmpty(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, remaining.copy());
                remaining = ItemStack.EMPTY;
                break;
            }
        }

        ContainerComponent newContents = ContainerComponent.fromStacks(items);
        shulkerStack.set(DataComponentTypes.CONTAINER, newContents);

        return remaining;
    }

    public static int findShulkerWithSpace(PlayerInventory inventory) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getStack(i);
            if (isShulkerBox(stack) && hasSpace(stack)) {
                return i;
            }
        }
        return -1;
    }

    public static ItemStack storeInShulker(PlayerInventory inventory, ItemStack itemToStore) {
        if (itemToStore.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (isShulkerBox(itemToStore)) {
            return itemToStore;
        }

        int shulkerSlot = findShulkerWithSpace(inventory);
        if (shulkerSlot == -1) {
            return itemToStore;
        }

        ItemStack shulkerBox = inventory.getStack(shulkerSlot);
        ItemStack remaining = insertItem(shulkerBox, itemToStore);

        inventory.markDirty();

        return remaining;
    }

    public static int countShulkerBoxes(PlayerInventory inventory) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            if (isShulkerBox(inventory.getStack(i))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Finds a shulker box with space in the opened container (ScreenHandler).
     * This allows storing items in shulker boxes that are inside chests, ender chests, etc.
     *
     * @param handler The screen handler (opened container)
     * @return The slot index of the first shulker box with space, or -1 if none found
     */
    public static int findShulkerWithSpaceInContainer(ScreenHandler handler) {
        if (handler == null) {
            return -1;
        }

        // Check all slots in the container (excluding player inventory slots)
        // Most containers have their slots at the beginning
        for (int i = 0; i < handler.slots.size(); i++) {
            Slot slot = handler.slots.get(i);

            // Skip player inventory slots (they start after container slots)
            if (slot.inventory instanceof PlayerInventory) {
                continue;
            }

            ItemStack stack = slot.getStack();
            if (isShulkerBox(stack) && hasSpace(stack)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Stores an item in a shulker box located in a ScreenHandler slot.
     * This works with shulker boxes in chests, ender chests, and other containers.
     *
     * @param handler The screen handler (opened container)
     * @param slotIndex The slot index where the shulker box is located
     * @param itemToStore The item to store
     * @return The remaining stack that couldn't be stored (empty if successful)
     */
    public static ItemStack storeInShulkerAtSlot(ScreenHandler handler, int slotIndex, ItemStack itemToStore) {
        if (handler == null || itemToStore.isEmpty() || slotIndex < 0 || slotIndex >= handler.slots.size()) {
            return itemToStore;
        }

        if (isShulkerBox(itemToStore)) {
            return itemToStore;
        }

        Slot slot = handler.slots.get(slotIndex);
        ItemStack shulkerBox = slot.getStack();

        if (!isShulkerBox(shulkerBox)) {
            return itemToStore;
        }

        ItemStack remaining = insertItem(shulkerBox, itemToStore);

        // Mark the slot as dirty to sync changes
        slot.markDirty();

        return remaining;
    }

    /**
     * Tries to store an item in any available shulker box, checking both the player's
     * inventory and any opened container.
     *
     * @param handler The current screen handler (can be null)
     * @param inventory The player's inventory
     * @param itemToStore The item to store
     * @return The remaining stack that couldn't be stored (empty if successful)
     */
    public static ItemStack storeInAnyShulker(ScreenHandler handler, PlayerInventory inventory, ItemStack itemToStore) {
        return storeInAnyShulker(handler, inventory, itemToStore, ItemStack.EMPTY);
    }

    /**
     * Tries to store an item in any available shulker box, prioritizing the cursor stack if it's a shulker box.
     *
     * @param handler The current screen handler (can be null)
     * @param inventory The player's inventory
     * @param itemToStore The item to store
     * @param cursorStack The item currently held by the cursor (prioritized if it's a shulker box)
     * @return The remaining stack that couldn't be stored (empty if successful)
     */
    public static ItemStack storeInAnyShulker(ScreenHandler handler, PlayerInventory inventory, ItemStack itemToStore, ItemStack cursorStack) {
        if (itemToStore.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (isShulkerBox(itemToStore)) {
            return itemToStore;
        }

        ItemStack remaining = itemToStore.copy();

        // FIRST PRIORITY: Try to store in the shulker box held by cursor
        if (!cursorStack.isEmpty() && isShulkerBox(cursorStack) && hasSpace(cursorStack)) {
            remaining = insertItem(cursorStack, remaining);
            if (remaining.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        // Second priority: Try to store in shulker boxes in the opened container
        if (handler != null) {
            int containerShulkerSlot = findShulkerWithSpaceInContainer(handler);
            if (containerShulkerSlot != -1) {
                remaining = storeInShulkerAtSlot(handler, containerShulkerSlot, remaining);
                if (remaining.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        // Third priority: Try player inventory shulker boxes
        if (!remaining.isEmpty()) {
            remaining = storeInShulker(inventory, remaining);
        }

        return remaining;
    }

    /**
     * Tries to store an item in any available shulker box with priority system awareness.
     * Respects config settings for cursor, container, and inventory priorities.
     *
     * @param handler The current screen handler (can be null)
     * @param inventory The player's inventory
     * @param itemToStore The item to store
     * @param cursorStack The item currently held by the cursor (prioritized if it's a shulker box)
     * @return The remaining stack that couldn't be stored (empty if successful)
     */
    public static ItemStack storeInAnyShulkerWithPriority(ScreenHandler handler, PlayerInventory inventory, ItemStack itemToStore, ItemStack cursorStack) {
        if (itemToStore.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (isShulkerBox(itemToStore)) {
            return itemToStore;
        }

        ItemStack remaining = itemToStore.copy();
        ModConfig config = ModConfig.getInstance();

        // FIRST PRIORITY: Try to store in the shulker box held by cursor (if enabled)
        if (config.enableCursorPriority && !cursorStack.isEmpty() && isShulkerBox(cursorStack) && hasSpace(cursorStack)) {
            remaining = insertItem(cursorStack, remaining);
            if (remaining.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        // Second priority: Try to store in shulker boxes in the opened container (if enabled)
        if (config.enableContainerShulkerPriority && handler != null) {
            int containerShulkerSlot = findShulkerWithSpaceInContainer(handler);
            if (containerShulkerSlot != -1) {
                remaining = storeInShulkerAtSlot(handler, containerShulkerSlot, remaining);
                if (remaining.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        // Third priority: Try player inventory shulker boxes (if enabled)
        if (config.enableInventoryShulkerFallback && !remaining.isEmpty()) {
            remaining = storeInShulker(inventory, remaining);
        }

        return remaining;
    }
}
