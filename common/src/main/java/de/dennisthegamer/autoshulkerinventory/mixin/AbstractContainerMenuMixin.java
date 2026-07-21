package de.dennisthegamer.autoshulkerinventory.mixin;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import de.dennisthegamer.autoshulkerinventory.util.InventoryUtils;
import de.dennisthegamer.autoshulkerinventory.util.NotificationUtils;
import de.dennisthegamer.autoshulkerinventory.util.ShulkerUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Shadow
    public abstract Slot getSlot(int slotId);

    @Shadow
    public abstract ItemStack getCarried();

    @Inject(method = "doClick", at = @At("RETURN"))
    private void onSlotClick(int slotIndex, int button, ContainerInput actionType,
                             Player player, CallbackInfo ci) {
        // Check if this is a QUICK_MOVE action (shift-click)
        if (actionType == null || !actionType.toString().contains("QUICK_MOVE")) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        // CONFIG CHECK: Only proceed if shift-click storage is enabled. Read after the
        // side check so the per-player lookup only happens where it applies.
        if (!ModConfig.forPlayer(player).enableShiftClickStorage) {
            return;
        }

        if (slotIndex < 0) {
            return;
        }

        Inventory inventory = player.getInventory();

        if (!InventoryUtils.isMainInventoryFull(inventory)) {
            return;
        }

        // Get the slot that was clicked
        AbstractContainerMenu handler = player.containerMenu;
        if (slotIndex >= handler.slots.size()) {
            return;
        }

        Slot clickedSlot = handler.slots.get(slotIndex);
        ItemStack slotStack = clickedSlot.getItem();

        // Get the cursor stack (item held by mouse) to prioritize it if it's a shulker box
        ItemStack cursorStack = getCarried();

        // If the item is still in the slot and it's not a shulker box
        if (!slotStack.isEmpty() && !ShulkerUtils.isShulkerBox(slotStack)) {
            // CONFIG CHECK: Build priority-aware storage call
            ItemStack remaining = ShulkerUtils.storeInAnyShulkerWithPriority(
                handler,
                inventory,
                slotStack,
                cursorStack
            );

            if (remaining.getCount() < slotStack.getCount()) {
                // Update the slot with the remaining items
                clickedSlot.set(remaining);

                int storedCount = slotStack.getCount() - remaining.getCount();

                // CONFIG CHECK: Debug logging
                if (ModConfig.forPlayer(player).enableDebugLogging) {
                    AutoShulkerInventory.LOGGER.info(
                        "Auto-stored {} items in shulker box (inventory full, actionType: {}, button: {})",
                        storedCount, actionType, button
                    );
                }

                // Send notification
                NotificationUtils.notifyPlayer(player, storedCount);
            }
        }
    }
}
