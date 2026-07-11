package com.autoshulker.fabric;

import com.autoshulker.AutoShulkerInventory;
import com.autoshulker.client.SlotSelectionHandler;
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

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof InventoryScreen inventoryScreen) {
				ScreenEvents.afterRender(screen).register((s, graphics, mouseX, mouseY, tickDelta) ->
					SlotSelectionHandler.renderHighlight(inventoryScreen, graphics));
				ScreenKeyboardEvents.afterKeyPress(screen).register((s, keyEvent) ->
					SlotSelectionHandler.handleKeyPress(inventoryScreen, keyEvent));
			}
		});
	}
}
