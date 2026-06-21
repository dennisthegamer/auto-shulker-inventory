package de.dennisthegamer.autoshulkerinventory.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class InventoryUtils {

    public static boolean isMainInventoryFull(Inventory inventory) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasSpaceInInventory(Inventory inventory) {
        return !isMainInventoryFull(inventory);
    }

    public static int getFirstEmptySlot(Inventory inventory) {
        for (int i = 0; i < 36; i++) {
            if (inventory.getItem(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public static int countEmptySlots(Inventory inventory) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            if (inventory.getItem(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }
}
