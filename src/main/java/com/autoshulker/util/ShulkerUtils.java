package com.autoshulker.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.block.ShulkerBoxBlock;

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
}
