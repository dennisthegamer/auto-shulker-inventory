package com.autoshulker;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoShulkerInventory implements ModInitializer {
	public static final String MOD_ID = "inventory_shulker";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Auto Shulker Inventory initialized!");
		LOGGER.info("Items will automatically be stored in shulker boxes when inventory is full.");
	}
}
