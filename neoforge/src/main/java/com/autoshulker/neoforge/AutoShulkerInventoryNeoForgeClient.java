package com.autoshulker.neoforge;

import com.autoshulker.AutoShulkerInventory;
import com.autoshulker.client.SlotSelectionHandler;
import com.autoshulker.config.ConfigScreen;
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

		container.registerExtensionPoint(IConfigScreenFactory.class,
			(mod, parent) -> ConfigScreen.create(parent));

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
