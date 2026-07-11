package de.dennisthegamer.autoshulkerinventory.fabric;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import net.fabricmc.api.ModInitializer;

public class AutoShulkerInventoryFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		AutoShulkerInventory.init();
	}
}
