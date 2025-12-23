package com.autoshulker.mixin;

import com.autoshulker.AutoShulkerInventory;
import com.autoshulker.config.ModConfig;
import com.autoshulker.util.InventoryUtils;
import com.autoshulker.util.NotificationUtils;
import com.autoshulker.util.ShulkerUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"))
    private void onItemAdded(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // CONFIG CHECK: Only proceed if auto storage is enabled
        if (!ModConfig.getInstance().enableAutoStorage) {
            return;
        }

        PlayerInventory inventory = (PlayerInventory) (Object) this;

        if (inventory.player == null || inventory.player.getEntityWorld().isClient()) {
            return;
        }

        if (!InventoryUtils.isMainInventoryFull(inventory)) {
            return;
        }

        tryMoveToShulker(inventory);
    }

    private void tryMoveToShulker(PlayerInventory inventory) {
        int shulkerSlot = ShulkerUtils.findShulkerWithSpace(inventory);
        if (shulkerSlot == -1) {
            return;
        }

        int totalStored = 0;

        for (int i = 35; i >= 0; i--) {
            if (i == shulkerSlot) {
                continue;
            }

            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.isEmpty() || ShulkerUtils.isShulkerBox(itemStack)) {
                continue;
            }

            ItemStack remaining = ShulkerUtils.storeInShulker(inventory, itemStack.copy());

            if (remaining.getCount() < itemStack.getCount()) {
                int storedCount = itemStack.getCount() - remaining.getCount();
                totalStored += storedCount;
                inventory.setStack(i, remaining);

                // CONFIG CHECK: Debug logging
                if (ModConfig.getInstance().enableDebugLogging) {
                    AutoShulkerInventory.LOGGER.info("Auto-stored {} items in shulker box", storedCount);
                }

                if (remaining.isEmpty()) {
                    break;
                }
            }

            if (!ShulkerUtils.hasSpace(inventory.getStack(shulkerSlot))) {
                break;
            }
        }

        // Send notification if items were stored
        if (totalStored > 0) {
            NotificationUtils.notifyPlayer(inventory.player, totalStored);
        }
    }
}
