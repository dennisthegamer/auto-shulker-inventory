package com.autoshulker.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils {

    public static boolean isMainInventoryFull(PlayerInventory inventory) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasSpaceInInventory(PlayerInventory inventory) {
        return !isMainInventoryFull(inventory);
    }

    public static int getFirstEmptySlot(PlayerInventory inventory) {
        for (int i = 0; i < 36; i++) {
            if (inventory.getStack(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public static int countEmptySlots(PlayerInventory inventory) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            if (inventory.getStack(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }
}
