package com.autoshulker.fabric;

import com.autoshulker.AutoShulkerInventory;
import net.fabricmc.api.ClientModInitializer;

public class AutoShulkerInventoryFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AutoShulkerInventory.initClient();
	}
}
