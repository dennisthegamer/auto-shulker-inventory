package de.dennisthegamer.autoshulkerinventory.mixin.client;

import de.dennisthegamer.autoshulkerinventory.client.SlotSelectionHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// The highlight must be submitted DURING the screen render: on 1.21.9/1.21.10 the
// deferred GUI pipeline ignores submissions made after the screen render pass
// (e.g. from loader after-render events). Target renderContents, NOT render —
// AbstractRecipeBookScreen.render (InventoryScreen's parent) bypasses
// AbstractContainerScreen.render and invokes renderContents directly.
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenRenderMixin {

    @Inject(method = "renderContents(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("TAIL"))
    private void autoshulker$renderTargetSlotHighlight(GuiGraphics graphics, int mouseX, int mouseY,
                                                       float partialTick, CallbackInfo ci) {
        if ((Object) this instanceof InventoryScreen inventoryScreen) {
            SlotSelectionHandler.renderHighlight(inventoryScreen, graphics);
        }
    }
}
