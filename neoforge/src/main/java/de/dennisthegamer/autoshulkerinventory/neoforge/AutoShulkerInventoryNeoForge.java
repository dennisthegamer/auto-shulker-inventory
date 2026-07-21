package de.dennisthegamer.autoshulkerinventory.neoforge;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(AutoShulkerInventory.MOD_ID)
public class AutoShulkerInventoryNeoForge {

	public AutoShulkerInventoryNeoForge(IEventBus modBus) {
		AutoShulkerInventory.init();
		ConfigSyncNeoForge.register(modBus);
	}
}
