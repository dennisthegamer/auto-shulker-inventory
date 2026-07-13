package de.dennisthegamer.autoshulkerinventory.fabric;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import de.dennisthegamer.autoshulkerinventory.client.SlotSelectionHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.Identifier;

public class AutoShulkerInventoryFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AutoShulkerInventory.initClient();

		KeyMapping.Category category = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(AutoShulkerInventory.MOD_ID, "main"));
		SlotSelectionHandler.selectSlotKey =
			KeyBindingHelper.registerKeyBinding(SlotSelectionHandler.createKeyMapping(category));

		// Highlight rendering happens via AbstractContainerScreenRenderMixin in common —
		// after-render events submit too late for the 1.21.9/1.21.10 GUI pipeline.
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof InventoryScreen inventoryScreen) {
				ScreenKeyboardEvents.afterKeyPress(screen).register((s, keyEvent) ->
					SlotSelectionHandler.handleKeyPress(inventoryScreen, keyEvent));
			}
		});
	}
}
