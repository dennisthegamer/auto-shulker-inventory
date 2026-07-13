package de.dennisthegamer.autoshulkerinventory.fabric;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import de.dennisthegamer.autoshulkerinventory.client.SlotSelectionHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class AutoShulkerInventoryFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AutoShulkerInventory.initClient();

		SlotSelectionHandler.selectSlotKey =
			KeyBindingHelper.registerKeyBinding(SlotSelectionHandler.createKeyMapping());

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof InventoryScreen inventoryScreen) {
				ScreenEvents.afterRender(screen).register((s, graphics, mouseX, mouseY, tickDelta) ->
					SlotSelectionHandler.renderHighlight(inventoryScreen, graphics));
				ScreenKeyboardEvents.afterKeyPress(screen).register((s, key, scancode, modifiers) ->
					SlotSelectionHandler.handleKeyPress(inventoryScreen, key, scancode));
			}
		});
	}
}
