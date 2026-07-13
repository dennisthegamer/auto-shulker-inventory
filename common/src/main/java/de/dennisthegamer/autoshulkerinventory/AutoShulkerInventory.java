package de.dennisthegamer.autoshulkerinventory;

import de.dennisthegamer.autoshulkerinventory.config.ModConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoShulkerInventory {
	public static final String MOD_ID = "inventory_shulker";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void init() {
		LOGGER.info("Auto Shulker Inventory initialized!");
		LOGGER.info("Items will automatically be stored in shulker boxes when inventory is full.");
	}

	public static void initClient() {
		// Load config on client initialization
		ModConfig.getInstance();

		LOGGER.info("Auto Shulker Inventory client initialized!");
	}
}
