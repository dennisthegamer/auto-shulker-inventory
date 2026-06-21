package de.dennisthegamer.autoshulkerinventory;

import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;

public class AutoShulkerInventoryClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModConfig.HANDLER.load();

		AutoShulkerInventory.LOGGER.info("Auto Shulker Inventory client initialized!");
	}
}
