package de.dennisthegamer.autoshulkerinventory.util;

import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;

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

    public static ItemContainerContents getShulkerContents(ItemStack shulkerStack) {
        if (!isShulkerBox(shulkerStack)) {
            return ItemContainerContents.EMPTY;
        }

        return shulkerStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
    }

    public static boolean hasSpace(ItemStack shulkerStack) {
        ItemContainerContents contents = getShulkerContents(shulkerStack);

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

        ItemContainerContents contents = getShulkerContents(shulkerStack);
        List<ItemStack> items = new ArrayList<>(contents.stream().toList());

        while (items.size() < 27) {
            items.add(ItemStack.EMPTY);
        }

        ItemStack remaining = toInsert.copy();

        for (int i = 0; i < 27 && !remaining.isEmpty(); i++) {
            ItemStack slotStack = items.get(i);

            if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, remaining)) {
                int maxStackSize = slotStack.getMaxStackSize();
                int currentCount = slotStack.getCount();
                int spaceLeft = maxStackSize - currentCount;

                if (spaceLeft > 0) {
                    int toTransfer = Math.min(spaceLeft, remaining.getCount());
                    slotStack.setCount(currentCount + toTransfer);
                    remaining.shrink(toTransfer);
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

        ItemContainerContents newContents = ItemContainerContents.fromItems(items);
        shulkerStack.set(DataComponents.CONTAINER, newContents);

        return remaining;
    }

    public static int findShulkerWithSpace(Inventory inventory) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getItem(i);
            if (isShulkerBox(stack) && hasSpace(stack)) {
                return i;
            }
        }
        return -1;
    }

    public static ItemStack storeInShulker(Inventory inventory, ItemStack itemToStore) {
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

        ItemStack shulkerBox = inventory.getItem(shulkerSlot);
        ItemStack remaining = insertItem(shulkerBox, itemToStore);

        inventory.setChanged();

        return remaining;
    }

    public static int countShulkerBoxes(Inventory inventory) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            if (isShulkerBox(inventory.getItem(i))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Finds a shulker box with space in the opened container (AbstractContainerMenu).
     * This allows storing items in shulker boxes that are inside chests, ender chests, etc.
     *
     * @param handler The container menu (opened container)
     * @return The slot index of the first shulker box with space, or -1 if none found
     */
    public static int findShulkerWithSpaceInContainer(AbstractContainerMenu handler) {
        if (handler == null) {
            return -1;
        }

        // Check all slots in the container (excluding player inventory slots)
        // Most containers have their slots at the beginning
        for (int i = 0; i < handler.slots.size(); i++) {
            Slot slot = handler.slots.get(i);

            // Skip player inventory slots (they start after container slots)
            if (slot.container instanceof Inventory) {
                continue;
            }

            ItemStack stack = slot.getItem();
            if (isShulkerBox(stack) && hasSpace(stack)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Stores an item in a shulker box located in an AbstractContainerMenu slot.
     * This works with shulker boxes in chests, ender chests, and other containers.
     *
     * @param handler The container menu (opened container)
     * @param slotIndex The slot index where the shulker box is located
     * @param itemToStore The item to store
     * @return The remaining stack that couldn't be stored (empty if successful)
     */
    public static ItemStack storeInShulkerAtSlot(AbstractContainerMenu handler, int slotIndex, ItemStack itemToStore) {
        if (handler == null || itemToStore.isEmpty() || slotIndex < 0 || slotIndex >= handler.slots.size()) {
            return itemToStore;
        }

        if (isShulkerBox(itemToStore)) {
            return itemToStore;
        }

        Slot slot = handler.slots.get(slotIndex);
        ItemStack shulkerBox = slot.getItem();

        if (!isShulkerBox(shulkerBox)) {
            return itemToStore;
        }

        ItemStack remaining = insertItem(shulkerBox, itemToStore);

        // Mark the slot as dirty to sync changes
        slot.setChanged();

        return remaining;
    }

    /**
     * Tries to store an item in any available shulker box, checking both the player's
     * inventory and any opened container.
     *
     * @param handler The current container menu (can be null)
     * @param inventory The player's inventory
     * @param itemToStore The item to store
     * @return The remaining stack that couldn't be stored (empty if successful)
     */
    public static ItemStack storeInAnyShulker(AbstractContainerMenu handler, Inventory inventory, ItemStack itemToStore) {
        return storeInAnyShulker(handler, inventory, itemToStore, ItemStack.EMPTY);
    }

    /**
     * Tries to store an item in any available shulker box, prioritizing the cursor stack if it's a shulker box.
     *
     * @param handler The current container menu (can be null)
     * @param inventory The player's inventory
     * @param itemToStore The item to store
     * @param cursorStack The item currently held by the cursor (prioritized if it's a shulker box)
     * @return The remaining stack that couldn't be stored (empty if successful)
     */
    public static ItemStack storeInAnyShulker(AbstractContainerMenu handler, Inventory inventory, ItemStack itemToStore, ItemStack cursorStack) {
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
     * @param handler The current container menu (can be null)
     * @param inventory The player's inventory
     * @param itemToStore The item to store
     * @param cursorStack The item currently held by the cursor (prioritized if it's a shulker box)
     * @return The remaining stack that couldn't be stored (empty if successful)
     */
    public static ItemStack storeInAnyShulkerWithPriority(AbstractContainerMenu handler, Inventory inventory, ItemStack itemToStore, ItemStack cursorStack) {
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
