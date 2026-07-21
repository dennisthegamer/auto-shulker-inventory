package de.dennisthegamer.autoshulkerinventory.neoforge;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import de.dennisthegamer.autoshulkerinventory.client.SlotSelectionHandler;
import de.dennisthegamer.autoshulkerinventory.config.ConfigScreen;
import de.dennisthegamer.autoshulkerinventory.platform.Platforms;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = AutoShulkerInventory.MOD_ID, dist = Dist.CLIENT)
public class AutoShulkerInventoryNeoForgeClient {

	public AutoShulkerInventoryNeoForgeClient(ModContainer container, IEventBus modBus) {
		AutoShulkerInventory.initClient();
		ConfigSyncNeoForgeClient.register();

		// YACL is only optional since the mod stopped needing it for persistence.
		// Registering unconditionally would throw NoClassDefFoundError as soon as
		// someone opens the config without YACL installed.
		if (Platforms.get().isModLoaded("yet_another_config_lib_v3")) {
			container.registerExtensionPoint(IConfigScreenFactory.class,
				(mod, parent) -> ConfigScreen.create(parent));
		}

		modBus.addListener(AutoShulkerInventoryNeoForgeClient::onRegisterKeyMappings);
		// Highlight rendering happens via AbstractContainerScreenRenderMixin in common —
		// after-render events submit too late for the 1.21.9/1.21.10 GUI pipeline.
		NeoForge.EVENT_BUS.addListener(AutoShulkerInventoryNeoForgeClient::onScreenKeyPressed);
	}

	private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
		// Vanilla INVENTORY category instead of a custom one: creating a custom
		// Category needs the Identifier class, whose mojmap name differs between
		// 1.21.10 (ResourceLocation) and 1.21.11 (Identifier) — NeoForge jars run
		// on the runtime version's mojmap names, so any direct reference crashes
		// on 1.21.9/1.21.10. The Category constants keep their names across all three.
		SlotSelectionHandler.selectSlotKey = SlotSelectionHandler.createKeyMapping(KeyMapping.Category.INVENTORY);
		event.register(SlotSelectionHandler.selectSlotKey);
	}

	private static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
		if (event.getScreen() instanceof InventoryScreen inventoryScreen) {
			SlotSelectionHandler.handleKeyPress(inventoryScreen, event.getKeyEvent());
		}
	}
}
