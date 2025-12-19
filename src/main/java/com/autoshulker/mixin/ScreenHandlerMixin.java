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

        if (player.getWorld().isClient()) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        if (!InventoryUtils.isMainInventoryFull(inventory)) {
            return;
        }

        ItemStack cursorStack = player.currentScreenHandler.getCursorStack();

        if (!cursorStack.isEmpty() && !ShulkerUtils.isShulkerBox(cursorStack)) {
            ItemStack remaining = ShulkerUtils.storeInShulker(inventory, cursorStack);

            if (remaining.getCount() < cursorStack.getCount()) {
                player.currentScreenHandler.setCursorStack(remaining);

                AutoShulkerInventory.LOGGER.info("Auto-stored {} items from cursor in shulker box",
                        cursorStack.getCount() - remaining.getCount());
            }
        }
    }
}
