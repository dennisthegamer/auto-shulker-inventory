package com.autoshulker.mixin;

import com.autoshulker.AutoShulkerInventory;
import com.autoshulker.config.ModConfig;
import com.autoshulker.util.InventoryUtils;
import com.autoshulker.util.NotificationUtils;
import com.autoshulker.util.ShulkerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Shadow
    public abstract Slot getSlot(int slotId);

    @Shadow
    public abstract ItemStack getCursorStack();

    @Inject(method = "internalOnSlotClick", at = @At("RETURN"))
    private void onSlotClick(int slotIndex, int button, SlotActionType actionType,
                             PlayerEntity player, CallbackInfo ci) {
        // CONFIG CHECK: Only proceed if shift-click storage is enabled
        if (!ModConfig.getInstance().enableShiftClickStorage) {
            return;
        }

        // Only support Shift+Click operations (QUICK_MOVE)
        if (actionType != SlotActionType.QUICK_MOVE) {
            return;
        }

        if (player.getWorld().isClient()) {
            return;
        }

        if (slotIndex < 0) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        if (!InventoryUtils.isMainInventoryFull(inventory)) {
            return;
        }

        // Get the slot that was clicked
        ScreenHandler handler = player.currentScreenHandler;
        if (slotIndex >= handler.slots.size()) {
            return;
        }

        Slot clickedSlot = handler.slots.get(slotIndex);
        ItemStack slotStack = clickedSlot.getStack();

        // Get the cursor stack (item held by mouse) to prioritize it if it's a shulker box
        ItemStack cursorStack = getCursorStack();

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
                clickedSlot.setStack(remaining);

                int storedCount = slotStack.getCount() - remaining.getCount();

                // CONFIG CHECK: Debug logging
                if (ModConfig.getInstance().enableDebugLogging) {
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
