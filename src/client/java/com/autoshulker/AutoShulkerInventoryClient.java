package com.autoshulker;

import com.autoshulker.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;

public class AutoShulkerInventoryClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Load config on client initialization
		ModConfig.getInstance();

		AutoShulkerInventory.LOGGER.info("Auto Shulker Inventory client initialized!");
	}
}
