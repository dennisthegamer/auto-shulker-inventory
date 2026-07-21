package de.dennisthegamer.autoshulkerinventory.mixin;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import de.dennisthegamer.autoshulkerinventory.util.InventoryUtils;
import de.dennisthegamer.autoshulkerinventory.util.NotificationUtils;
import de.dennisthegamer.autoshulkerinventory.util.ShulkerUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    private void onItemAdded(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Inventory inventory = (Inventory) (Object) this;

        if (inventory.player == null || inventory.player.level().isClientSide()) {
            return;
        }

        // CONFIG CHECK: Only proceed if auto storage is enabled. Read after the
        // side check so the per-player lookup only happens where it applies.
        if (!ModConfig.forPlayer(inventory.player).enableAutoStorage) {
            return;
        }

        if (!InventoryUtils.isMainInventoryFull(inventory)) {
            return;
        }

        tryMoveToShulker(inventory);
    }

    private void tryMoveToShulker(Inventory inventory) {
        int shulkerSlot = ShulkerUtils.findShulkerWithSpace(inventory);
        if (shulkerSlot == -1) {
            return;
        }

        ModConfig config = ModConfig.forPlayer(inventory.player);
        int totalStored = 0;
        boolean preferredEmptied = false;

        int preferredSlot = config.preferredEmptySlot;
        if (preferredSlot >= 0 && preferredSlot < 36) {
            totalStored += tryEmptyPreferredSlot(inventory, preferredSlot);
            preferredEmptied = inventory.getItem(preferredSlot).isEmpty();
            if (!preferredEmptied && config.enableDebugLogging) {
                AutoShulkerInventory.LOGGER.info(
                    "Preferred slot {} could not be emptied (shulker box, unstorable item, or boxes full), falling back to automatic slot selection",
                    preferredSlot);
            }
        }

        if (!preferredEmptied) {
            totalStored += autoMoveToShulker(inventory, shulkerSlot);
        }

        // Send notification if items were stored
        if (totalStored > 0) {
            NotificationUtils.notifyPlayer(inventory.player, totalStored);
        }
    }

    private int tryEmptyPreferredSlot(Inventory inventory, int preferredSlot) {
        ItemStack stack = inventory.getItem(preferredSlot);
        if (stack.isEmpty() || ShulkerUtils.isShulkerBox(stack)) {
            return 0;
        }

        ItemStack remaining = ShulkerUtils.storeInShulker(inventory, stack.copy());
        int stored = stack.getCount() - remaining.getCount();
        if (stored > 0) {
            inventory.setItem(preferredSlot, remaining);

            if (ModConfig.forPlayer(inventory.player).enableDebugLogging) {
                AutoShulkerInventory.LOGGER.info("Auto-stored {} items from preferred slot {}", stored, preferredSlot);
            }
        }
        return stored;
    }

    private int autoMoveToShulker(Inventory inventory, int shulkerSlot) {
        int totalStored = 0;

        for (int i = 35; i >= 0; i--) {
            if (i == shulkerSlot) {
                continue;
            }

            ItemStack itemStack = inventory.getItem(i);
            if (itemStack.isEmpty() || ShulkerUtils.isShulkerBox(itemStack)) {
                continue;
            }

            ItemStack remaining = ShulkerUtils.storeInShulker(inventory, itemStack.copy());

            if (remaining.getCount() < itemStack.getCount()) {
                int storedCount = itemStack.getCount() - remaining.getCount();
                totalStored += storedCount;
                inventory.setItem(i, remaining);

                // CONFIG CHECK: Debug logging
                if (ModConfig.forPlayer(inventory.player).enableDebugLogging) {
                    AutoShulkerInventory.LOGGER.info("Auto-stored {} items in shulker box", storedCount);
                }

                if (remaining.isEmpty()) {
                    break;
                }
            }

            if (!ShulkerUtils.hasSpace(inventory.getItem(shulkerSlot))) {
                break;
            }
        }

        return totalStored;
    }
}
