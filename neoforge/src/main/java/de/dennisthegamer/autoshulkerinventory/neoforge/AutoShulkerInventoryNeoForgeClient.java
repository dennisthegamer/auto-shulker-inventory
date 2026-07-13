package de.dennisthegamer.autoshulkerinventory.neoforge;

import de.dennisthegamer.autoshulkerinventory.AutoShulkerInventory;
import de.dennisthegamer.autoshulkerinventory.client.SlotSelectionHandler;
import de.dennisthegamer.autoshulkerinventory.config.ConfigScreen;
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
		NeoForge.EVENT_BUS.addListener(AutoShulkerInventoryNeoForgeClient::onScreenRenderPost);
		NeoForge.EVENT_BUS.addListener(AutoShulkerInventoryNeoForgeClient::onScreenKeyPressed);
	}

	private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
		SlotSelectionHandler.selectSlotKey = SlotSelectionHandler.createKeyMapping();
		event.register(SlotSelectionHandler.selectSlotKey);
	}

	private static void onScreenRenderPost(ScreenEvent.Render.Post event) {
		if (event.getScreen() instanceof InventoryScreen inventoryScreen) {
			SlotSelectionHandler.renderHighlight(inventoryScreen, event.getGuiGraphics());
		}
	}

	private static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
		if (event.getScreen() instanceof InventoryScreen inventoryScreen) {
			SlotSelectionHandler.handleKeyPress(inventoryScreen, event.getKeyCode(), event.getScanCode());
		}
	}
}
