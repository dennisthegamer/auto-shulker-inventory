package com.autoshulker.mixin;

import com.autoshulker.AutoShulkerInventory;
import com.autoshulker.util.InventoryUtils;
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

    @Inject(method = "internalOnSlotClick", at = @At("RETURN"))
    private void onQuickMove(int slotIndex, int button, SlotActionType actionType,
                             PlayerEntity player, CallbackInfo ci) {
        if (actionType != SlotActionType.QUICK_MOVE) {
            return;
        }

        if (player.getEntityWorld().isClient()) {
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

        // If the item is still in the slot (QUICK_MOVE failed) and it's not a shulker box
        if (!slotStack.isEmpty() && !ShulkerUtils.isShulkerBox(slotStack)) {
            // Try to store in shulker boxes (container first, then inventory)
            ItemStack remaining = ShulkerUtils.storeInAnyShulker(handler, inventory, slotStack);

            if (remaining.getCount() < slotStack.getCount()) {
                // Update the slot with the remaining items
                clickedSlot.setStack(remaining);

                int storedCount = slotStack.getCount() - remaining.getCount();
                AutoShulkerInventory.LOGGER.info("Auto-stored {} items in shulker box (inventory full)",
                        storedCount);
            }
        }
    }
}
