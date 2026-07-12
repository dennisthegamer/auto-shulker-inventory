package com.autoshulker.neoforge;

import com.autoshulker.AutoShulkerInventory;
import com.autoshulker.config.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = AutoShulkerInventory.MOD_ID, dist = Dist.CLIENT)
public class AutoShulkerInventoryNeoForgeClient {

	public AutoShulkerInventoryNeoForgeClient(ModContainer container) {
		AutoShulkerInventory.initClient();

		container.registerExtensionPoint(IConfigScreenFactory.class,
			(mod, parent) -> ConfigScreen.create(parent));
	}
}
