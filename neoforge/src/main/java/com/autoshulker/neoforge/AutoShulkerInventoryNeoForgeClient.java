package com.autoshulker.neoforge;

import com.autoshulker.AutoShulkerInventory;
import com.autoshulker.client.SlotSelectionHandler;
import com.autoshulker.config.ConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.Identifier;
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

	private static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(
		Identifier.fromNamespaceAndPath(AutoShulkerInventory.MOD_ID, "main"));

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
		event.registerCategory(KEY_CATEGORY);
		SlotSelectionHandler.selectSlotKey = SlotSelectionHandler.createKeyMapping(KEY_CATEGORY);
		event.register(SlotSelectionHandler.selectSlotKey);
	}

	private static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
		if (event.getScreen() instanceof InventoryScreen inventoryScreen) {
			SlotSelectionHandler.handleKeyPress(inventoryScreen, event.getKeyEvent());
		}
	}
}
