package com.autoshulker.fabric;

import com.autoshulker.AutoShulkerInventory;
import net.fabricmc.api.ModInitializer;

public class AutoShulkerInventoryFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		AutoShulkerInventory.init();
	}
}
