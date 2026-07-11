# Configurable Target Slot Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let the player configure which inventory slot gets emptied by auto-storage (config value + hover-and-press keybind), with a green outline marking that slot in the survival inventory screen.

**Architecture:** A new `preferredEmptySlot` config value (−1 = automatic) is consumed by `InventoryMixin` (try the configured slot first, fall back to the existing auto loop). Client-side, a shared `SlotSelectionHandler` in `common/` implements keybind handling and outline rendering; Fabric and NeoForge each wire it up with their own event APIs. A client accessor mixin exposes `hoveredSlot`/`leftPos`/`topPos` of `AbstractContainerScreen`.

**Tech Stack:** Minecraft 1.21.11, Architectury Loom multiloader (common/fabric/neoforge), Mixin, Cloth Config, Fabric API (screen + key-binding APIs), NeoForge 21.11.42.

**Spec:** `docs/superpowers/specs/2026-07-11-configurable-target-slot-design.md`

## Global Constraints

- Branch: `multiloader-test`. Minecraft `1.21.11`, Java 21, mod version `1.3.0`, `MOD_ID = "inventory_shulker"`.
- **Mapping quirk:** These 1.21.11 mappings name the resource-id class `net.minecraft.resources.Identifier` (NOT `ResourceLocation`). Key input uses `net.minecraft.client.input.KeyEvent`; `KeyMapping.matches(KeyEvent)` — there is no `matches(int, int)` overload. Keybind categories are `KeyMapping.Category` objects (NOT strings).
- The repo has **no unit-test infrastructure** (Minecraft mod, mixins + client rendering). Each task's test cycle is: `./gradlew <compile/build task>` must succeed with zero errors; final task is a manual in-game checklist mirroring spec section 7. Do not add a test framework.
- All shared code goes in `common/`; loader modules only contain thin wiring. Never annotate common classes with `@Environment`/`@OnlyIn` (see comment in `ConfigScreen.java`).
- Existing translation-key convention: config/message keys use prefix `auto_shulker_inventory`; asset folder/namespace is `inventory_shulker`. Follow this (yes, it is inconsistent — keep it).
- Run gradle from repo root `C:\Users\mager\AppData\Local\Temp\isl-ml`. In Git Bash use `./gradlew`, in PowerShell use `.\gradlew.bat`. First build after a clean can take minutes; do not abort.

---

### Task 1: Config field `preferredEmptySlot` + config screen entry + lang keys

**Files:**
- Modify: `common/src/main/java/com/autoshulker/config/ModConfig.java`
- Modify: `common/src/main/java/com/autoshulker/config/ConfigScreen.java`
- Modify: `common/src/main/resources/assets/inventory_shulker/lang/en_us.json`

**Interfaces:**
- Consumes: existing `ModConfig` singleton (`getInstance()`, `save()`, gson `load()`).
- Produces: `public int preferredEmptySlot` on `ModConfig` (−1 = automatic, valid range −1..35, out-of-range values sanitized to −1 on load). Tasks 2 and 4 read/write this field.

- [ ] **Step 1: Add the field to `ModConfig`**

In `common/src/main/java/com/autoshulker/config/ModConfig.java`, directly under the "Priority System" field block (after `enableInventoryShulkerFallback`, line ~30), add:

```java
    // Preferred slot to empty during auto-storage (-1 = automatic, 0-8 hotbar, 9-35 main inventory)
    public int preferredEmptySlot = -1;
```

- [ ] **Step 2: Sanitize the value on load**

In the same file, inside `load()`, the successful-load branch currently reads:

```java
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config != null) {
                    return config;
                }
```

Replace it with:

```java
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config != null) {
                    if (config.preferredEmptySlot < -1 || config.preferredEmptySlot > 35) {
                        config.preferredEmptySlot = -1;
                    }
                    return config;
                }
```

- [ ] **Step 3: Add the config screen entry**

In `common/src/main/java/com/autoshulker/config/ConfigScreen.java`, inside the "PRIORITY SYSTEM CATEGORY" section, after the `enable_inventory_fallback` entry (`prioritySystem.addEntry(...)` block ending around line 80), add:

```java
        prioritySystem.addEntry(entryBuilder.startIntField(
            Component.translatable("config.auto_shulker_inventory.preferred_empty_slot"),
            config.preferredEmptySlot
        )
            .setDefaultValue(-1)
            .setMin(-1)
            .setMax(35)
            .setTooltip(Component.translatable("config.auto_shulker_inventory.preferred_empty_slot.tooltip"))
            .setSaveConsumer(value -> config.preferredEmptySlot = value)
            .build());
```

- [ ] **Step 4: Add lang entries**

In `common/src/main/resources/assets/inventory_shulker/lang/en_us.json`, after the `enable_inventory_fallback.tooltip` line, add:

```json
  "config.auto_shulker_inventory.preferred_empty_slot": "Preferred Target Slot",
  "config.auto_shulker_inventory.preferred_empty_slot.tooltip": "Which inventory slot to empty when auto-storing. -1 = automatic, 0-8 = hotbar, 9-35 = main inventory",
```

(Keep the JSON valid — the file is a flat object, entries are comma-separated.)

- [ ] **Step 5: Verify it compiles**

Run: `./gradlew :common:compileJava`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 6: Commit**

```bash
git add common/src/main/java/com/autoshulker/config/ModConfig.java common/src/main/java/com/autoshulker/config/ConfigScreen.java common/src/main/resources/assets/inventory_shulker/lang/en_us.json
git commit -m "feat: add preferredEmptySlot config option"
```

---

### Task 2: Preferred-slot-first storage logic in `InventoryMixin`

**Files:**
- Modify: `common/src/main/java/com/autoshulker/mixin/InventoryMixin.java`

**Interfaces:**
- Consumes: `ModConfig.getInstance().preferredEmptySlot` (Task 1), existing `ShulkerUtils.storeInShulker(Inventory, ItemStack)`, `ShulkerUtils.isShulkerBox(ItemStack)`, `ShulkerUtils.findShulkerWithSpace(Inventory)`, `ShulkerUtils.hasSpace(ItemStack)`, `NotificationUtils.notifyPlayer(Player, int)`.
- Produces: no new public API. Behavior contract for Task 7: when `preferredEmptySlot` is 0–35 and that slot holds a storable stack, exactly that slot is emptied; otherwise the pre-existing auto loop (slot 35 downwards) runs as fallback, with a debug log explaining why.

- [ ] **Step 1: Replace `tryMoveToShulker` with preferred-slot-aware version**

In `common/src/main/java/com/autoshulker/mixin/InventoryMixin.java`, replace the entire `tryMoveToShulker` method (currently lines 38–82) with the following three methods:

```java
    private void tryMoveToShulker(Inventory inventory) {
        int shulkerSlot = ShulkerUtils.findShulkerWithSpace(inventory);
        if (shulkerSlot == -1) {
            return;
        }

        ModConfig config = ModConfig.getInstance();
        int totalStored = 0;
        boolean preferredEmptied = false;

        int preferredSlot = config.preferredEmptySlot;
        if (preferredSlot >= 0 && preferredSlot < 36) {
            totalStored += tryEmptyPreferredSlot(inventory, preferredSlot);
            preferredEmptied = inventory.getItem(preferredSlot).isEmpty();
            if (!preferredEmptied && config.enableDebugLogging) {
                AutoShulkerInventory.LOGGER.info(
                    "Preferred slot {} could not be emptied (shulker box, unstorable item, or boxes full), falling back to automatic slot selection",
                    preferredSlot);
            }
        }

        if (!preferredEmptied) {
            totalStored += autoMoveToShulker(inventory, shulkerSlot);
        }

        // Send notification if items were stored
        if (totalStored > 0) {
            NotificationUtils.notifyPlayer(inventory.player, totalStored);
        }
    }

    private int tryEmptyPreferredSlot(Inventory inventory, int preferredSlot) {
        ItemStack stack = inventory.getItem(preferredSlot);
        if (stack.isEmpty() || ShulkerUtils.isShulkerBox(stack)) {
            return 0;
        }

        ItemStack remaining = ShulkerUtils.storeInShulker(inventory, stack.copy());
        int stored = stack.getCount() - remaining.getCount();
        if (stored > 0) {
            inventory.setItem(preferredSlot, remaining);

            if (ModConfig.getInstance().enableDebugLogging) {
                AutoShulkerInventory.LOGGER.info("Auto-stored {} items from preferred slot {}", stored, preferredSlot);
            }
        }
        return stored;
    }

    private int autoMoveToShulker(Inventory inventory, int shulkerSlot) {
        int totalStored = 0;

        for (int i = 35; i >= 0; i--) {
            if (i == shulkerSlot) {
                continue;
            }

            ItemStack itemStack = inventory.getItem(i);
            if (itemStack.isEmpty() || ShulkerUtils.isShulkerBox(itemStack)) {
                continue;
            }

            ItemStack remaining = ShulkerUtils.storeInShulker(inventory, itemStack.copy());

            if (remaining.getCount() < itemStack.getCount()) {
                int storedCount = itemStack.getCount() - remaining.getCount();
                totalStored += storedCount;
                inventory.setItem(i, remaining);

                // CONFIG CHECK: Debug logging
                if (ModConfig.getInstance().enableDebugLogging) {
                    AutoShulkerInventory.LOGGER.info("Auto-stored {} items in shulker box", storedCount);
                }

                if (remaining.isEmpty()) {
                    break;
                }
            }

            if (!ShulkerUtils.hasSpace(inventory.getItem(shulkerSlot))) {
                break;
            }
        }

        return totalStored;
    }
```

Notes for the implementer:
- `autoMoveToShulker` is the old loop body verbatim, just extracted and returning the count instead of mutating a local.
- The `isShulkerBox` guard in `tryEmptyPreferredSlot` also covers "configured slot is the target box's slot" from the spec's error table — that slot always contains a shulker box.
- All imports already exist in the file; no import changes needed.

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew :common:compileJava`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add common/src/main/java/com/autoshulker/mixin/InventoryMixin.java
git commit -m "feat: empty the configured preferred slot first during auto-storage"
```

---

### Task 3: Client accessor mixin for `AbstractContainerScreen`

**Files:**
- Create: `common/src/main/java/com/autoshulker/mixin/client/AbstractContainerScreenAccessor.java`
- Modify: `common/src/main/resources/inventory_shulker.mixins.json`

**Interfaces:**
- Consumes: vanilla `AbstractContainerScreen` protected members `hoveredSlot` (`Slot`), `leftPos` (`int`), `topPos` (`int`) — names verified against the 1.21.11 mappings.
- Produces: interface `com.autoshulker.mixin.client.AbstractContainerScreenAccessor` with methods `autoshulker$getHoveredSlot()`, `autoshulker$getLeftPos()`, `autoshulker$getTopPos()`. Task 4 casts an `InventoryScreen` to this interface.

- [ ] **Step 1: Create the accessor mixin**

Create `common/src/main/java/com/autoshulker/mixin/client/AbstractContainerScreenAccessor.java`:

```java
package com.autoshulker.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

    @Accessor("hoveredSlot")
    Slot autoshulker$getHoveredSlot();

    @Accessor("leftPos")
    int autoshulker$getLeftPos();

    @Accessor("topPos")
    int autoshulker$getTopPos();
}
```

- [ ] **Step 2: Register it as a client mixin**

In `common/src/main/resources/inventory_shulker.mixins.json`, add a `client` array after the existing `mixins` array:

```json
{
	"required": true,
	"package": "com.autoshulker.mixin",
	"compatibilityLevel": "JAVA_21",
	"mixins": [
		"InventoryMixin",
		"AbstractContainerMenuMixin"
	],
	"client": [
		"client.AbstractContainerScreenAccessor"
	],
	"injectors": {
		"defaultRequire": 1
	},
	"overwrites": {
		"requireAnnotations": true
	}
}
```

(This is the complete new file content — replace the file with it.)

- [ ] **Step 3: Verify both loaders still build**

Run: `./gradlew build`
Expected: `BUILD SUCCESSFUL` (compiles common, fabric, neoforge; mixin annotation processing runs here)

- [ ] **Step 4: Commit**

```bash
git add common/src/main/java/com/autoshulker/mixin/client/AbstractContainerScreenAccessor.java common/src/main/resources/inventory_shulker.mixins.json
git commit -m "feat: add client accessor mixin for AbstractContainerScreen"
```

---

### Task 4: Shared client logic `SlotSelectionHandler` + lang keys

**Files:**
- Create: `common/src/main/java/com/autoshulker/client/SlotSelectionHandler.java`
- Modify: `common/src/main/resources/assets/inventory_shulker/lang/en_us.json`

**Interfaces:**
- Consumes: `ModConfig.preferredEmptySlot` (Task 1), `AbstractContainerScreenAccessor` (Task 3), vanilla `KeyMapping`, `KeyMapping.Category`, `KeyEvent`, `GuiGraphics.renderOutline(int, int, int, int, int)`.
- Produces (used by Tasks 5 and 6):
  - `public static KeyMapping selectSlotKey` — assigned by each loader after registration.
  - `public static KeyMapping createKeyMapping(KeyMapping.Category category)` — returns the unregistered mapping (translation key `key.auto_shulker_inventory.select_target_slot`, default key B).
  - `public static void handleKeyPress(InventoryScreen screen, KeyEvent keyEvent)` — sets/clears the preferred slot from the hovered slot and saves the config.
  - `public static void renderHighlight(InventoryScreen screen, GuiGraphics graphics)` — draws the green outline on the configured slot.

- [ ] **Step 1: Create `SlotSelectionHandler`**

Create `common/src/main/java/com/autoshulker/client/SlotSelectionHandler.java`:

```java
package com.autoshulker.client;

import com.autoshulker.config.ModConfig;
import com.autoshulker.mixin.client.AbstractContainerScreenAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

// Client-only by structure: referenced solely from the loader client entrypoints —
// never annotate with @Environment/@OnlyIn (see ConfigScreen for rationale).
public class SlotSelectionHandler {

    private static final int HIGHLIGHT_COLOR = 0xFF00FF00; // opaque green
    private static final int PLAYER_INVENTORY_SIZE = 36;

    // Assigned by the loader-specific client init after registering the mapping
    public static KeyMapping selectSlotKey;

    public static KeyMapping createKeyMapping(KeyMapping.Category category) {
        return new KeyMapping(
            "key.auto_shulker_inventory.select_target_slot",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            category
        );
    }

    public static void handleKeyPress(InventoryScreen screen, KeyEvent keyEvent) {
        if (selectSlotKey == null || !selectSlotKey.matches(keyEvent)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        Slot hovered = ((AbstractContainerScreenAccessor) screen).autoshulker$getHoveredSlot();
        if (hovered == null || hovered.container != minecraft.player.getInventory()) {
            return;
        }

        int slotIndex = hovered.getContainerSlot();
        if (slotIndex < 0 || slotIndex >= PLAYER_INVENTORY_SIZE) {
            return;
        }

        ModConfig config = ModConfig.getInstance();
        if (config.preferredEmptySlot == slotIndex) {
            config.preferredEmptySlot = -1;
            config.save();
            minecraft.player.displayClientMessage(
                Component.translatable("message.auto_shulker_inventory.target_slot_reset"), false);
        } else {
            config.preferredEmptySlot = slotIndex;
            config.save();
            minecraft.player.displayClientMessage(
                Component.translatable("message.auto_shulker_inventory.target_slot_set", slotIndex), false);
        }
    }

    public static void renderHighlight(InventoryScreen screen, GuiGraphics graphics) {
        int targetSlot = ModConfig.getInstance().preferredEmptySlot;
        if (targetSlot < 0 || targetSlot >= PLAYER_INVENTORY_SIZE) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        for (Slot slot : screen.getMenu().slots) {
            if (slot.container == minecraft.player.getInventory() && slot.getContainerSlot() == targetSlot) {
                int x = accessor.autoshulker$getLeftPos() + slot.x;
                int y = accessor.autoshulker$getTopPos() + slot.y;
                graphics.renderOutline(x - 1, y - 1, 18, 18, HIGHLIGHT_COLOR);
                return;
            }
        }
    }
}
```

- [ ] **Step 2: Add lang entries**

In `common/src/main/resources/assets/inventory_shulker/lang/en_us.json`, before the final `"message.auto_shulker_inventory.no_space"` line, add:

```json
  "key.auto_shulker_inventory.select_target_slot": "Select Target Slot",
  "key.category.inventory_shulker.main": "Auto Shulker Inventory",

  "message.auto_shulker_inventory.target_slot_set": "Target slot set to %s",
  "message.auto_shulker_inventory.target_slot_reset": "Target slot reset to automatic",
```

(`key.category.inventory_shulker.main` is the label of the `KeyMapping.Category` with id `inventory_shulker:main` — vanilla derives the key as `"key.category" + id.toLanguageKey()`.)

**IMPORTANT — use `%s`, not `%d`:** vanilla `TranslatableContents` only supports `%s` and `%%`; any other specifier throws `TranslatableFormatException` at render time (verified against the 1.21.11 jar).

- [ ] **Step 2b: Drive-by fix for the existing `%d` message**

In the same file, the existing line

```json
  "message.auto_shulker_inventory.stored_items": "Stored %d items in shulker box",
```

uses `%d` and would crash rendering for the same reason. Change it to:

```json
  "message.auto_shulker_inventory.stored_items": "Stored %s items in shulker box",
```

- [ ] **Step 3: Verify it compiles**

Run: `./gradlew :common:compileJava`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add common/src/main/java/com/autoshulker/client/SlotSelectionHandler.java common/src/main/resources/assets/inventory_shulker/lang/en_us.json
git commit -m "feat: add shared client handler for target slot selection and highlight"
```

---

### Task 5: Fabric wiring (keybind + screen events)

**Files:**
- Modify: `fabric/src/main/java/com/autoshulker/fabric/AutoShulkerInventoryFabricClient.java`

**Interfaces:**
- Consumes: `SlotSelectionHandler.createKeyMapping(KeyMapping.Category)`, `SlotSelectionHandler.selectSlotKey`, `SlotSelectionHandler.renderHighlight(InventoryScreen, GuiGraphics)`, `SlotSelectionHandler.handleKeyPress(InventoryScreen, KeyEvent)` (Task 4). Fabric API: `KeyBindingHelper.registerKeyBinding(KeyMapping)`, `ScreenEvents.AFTER_INIT` (`afterInit(Minecraft, Screen, int, int)`), `ScreenEvents.afterRender(Screen)` (`afterRender(Screen, GuiGraphics, int, int, float)`), `ScreenKeyboardEvents.afterKeyPress(Screen)` (`afterKeyPress(Screen, KeyEvent)`). Vanilla: `KeyMapping.Category.register(Identifier)`.
- Produces: nothing consumed by later tasks.

- [ ] **Step 1: Replace the Fabric client entrypoint**

Replace the full content of `fabric/src/main/java/com/autoshulker/fabric/AutoShulkerInventoryFabricClient.java` with:

```java
package com.autoshulker.fabric;

import com.autoshulker.AutoShulkerInventory;
import com.autoshulker.client.SlotSelectionHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.Identifier;

public class AutoShulkerInventoryFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AutoShulkerInventory.initClient();

		KeyMapping.Category category = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(AutoShulkerInventory.MOD_ID, "main"));
		SlotSelectionHandler.selectSlotKey =
			KeyBindingHelper.registerKeyBinding(SlotSelectionHandler.createKeyMapping(category));

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof InventoryScreen inventoryScreen) {
				ScreenEvents.afterRender(screen).register((s, graphics, mouseX, mouseY, tickDelta) ->
					SlotSelectionHandler.renderHighlight(inventoryScreen, graphics));
				ScreenKeyboardEvents.afterKeyPress(screen).register((s, keyEvent) ->
					SlotSelectionHandler.handleKeyPress(inventoryScreen, keyEvent));
			}
		});
	}
}
```

(File uses tabs for indentation — match the existing file.)

- [ ] **Step 2: Verify the Fabric module builds**

Run: `./gradlew :fabric:build`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add fabric/src/main/java/com/autoshulker/fabric/AutoShulkerInventoryFabricClient.java
git commit -m "feat(fabric): wire up target slot keybind and inventory highlight"
```

---

### Task 6: NeoForge wiring (keybind + screen events)

**Files:**
- Modify: `neoforge/src/main/java/com/autoshulker/neoforge/AutoShulkerInventoryNeoForgeClient.java`

**Interfaces:**
- Consumes: `SlotSelectionHandler` (Task 4). NeoForge: `RegisterKeyMappingsEvent` (mod bus; has `register(KeyMapping)` and `registerCategory(KeyMapping.Category)`), `ScreenEvent.Render.Post` / `ScreenEvent.KeyPressed.Pre` (game bus `NeoForge.EVENT_BUS`; provide `getScreen()`, `getGuiGraphics()`, `getKeyEvent()`). `@Mod` client constructors may take `(ModContainer, IEventBus)` — the `IEventBus` parameter is the mod event bus.
- Produces: nothing consumed by later tasks.

- [ ] **Step 1: Replace the NeoForge client mod class**

Replace the full content of `neoforge/src/main/java/com/autoshulker/neoforge/AutoShulkerInventoryNeoForgeClient.java` with:

```java
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
		NeoForge.EVENT_BUS.addListener(AutoShulkerInventoryNeoForgeClient::onScreenRenderPost);
		NeoForge.EVENT_BUS.addListener(AutoShulkerInventoryNeoForgeClient::onScreenKeyPressed);
	}

	private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
		event.registerCategory(KEY_CATEGORY);
		SlotSelectionHandler.selectSlotKey = SlotSelectionHandler.createKeyMapping(KEY_CATEGORY);
		event.register(SlotSelectionHandler.selectSlotKey);
	}

	private static void onScreenRenderPost(ScreenEvent.Render.Post event) {
		if (event.getScreen() instanceof InventoryScreen inventoryScreen) {
			SlotSelectionHandler.renderHighlight(inventoryScreen, event.getGuiGraphics());
		}
	}

	private static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
		if (event.getScreen() instanceof InventoryScreen inventoryScreen) {
			SlotSelectionHandler.handleKeyPress(inventoryScreen, event.getKeyEvent());
		}
	}
}
```

(File uses tabs for indentation — match the existing file. Note: on NeoForge the category is created with `new KeyMapping.Category(...)` and registered via `event.registerCategory(...)` — do NOT call the vanilla static `KeyMapping.Category.register` here; that is the Fabric path.)

- [ ] **Step 2: Verify the NeoForge module builds**

Run: `./gradlew :neoforge:build`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add neoforge/src/main/java/com/autoshulker/neoforge/AutoShulkerInventoryNeoForgeClient.java
git commit -m "feat(neoforge): wire up target slot keybind and inventory highlight"
```

---

### Task 7: Full build + manual in-game verification

**Files:** none (verification only)

**Interfaces:**
- Consumes: everything from Tasks 1–6.
- Produces: verified feature; jars in `fabric/build/libs/` and `neoforge/build/libs/`.

- [ ] **Step 1: Full clean build**

Run: `./gradlew clean build`
Expected: `BUILD SUCCESSFUL`, jars produced under `fabric/build/libs/` and `neoforge/build/libs/`.

- [ ] **Step 2: Launch the Fabric dev client**

Run: `./gradlew :fabric:runClient` (interactive — the game window opens; this step is for the human tester).

- [ ] **Step 3: Manual checklist (Fabric)**

Mirrors spec section 7 — all six checks:

1. Config screen (ModMenu → Auto Shulker Inventory → Priority System): set "Preferred Target Slot" to e.g. 31 → open inventory (E) → green outline on the correct slot (row 3, column 5 of the main inventory for 31).
2. Fill the inventory completely, keep one shulker box with space in it, pick up items → exactly slot 31 is emptied.
3. Put a shulker box into slot 31, enable Debug Logging, pick up items with a full inventory → a different slot is emptied; the log explains the fallback.
4. Hover another inventory slot, press B → chat message "Target slot set to N", outline moves; press B on the same slot again → "Target slot reset to automatic", outline gone. Verify `config/auto_shulker_inventory.json` contains the new value.
5. Open a chest → no outline there, B does nothing there.
6. Set the option to −1 → behavior identical to before the feature (regression check).

- [ ] **Step 4: Launch the NeoForge dev client and repeat**

Run: `./gradlew :neoforge:runClient`
Repeat the checklist (config screen is reachable via the Mods list → Auto Shulker Inventory → Config). Also verify the keybind appears in Options → Controls under category "Auto Shulker Inventory".

- [ ] **Step 5: Commit any fixes found during verification**

If verification uncovered fixes, commit them individually with descriptive messages. If nothing was found, no commit is needed.

---

## Known limitation (accepted)

While the recipe-book search field in the inventory screen is focused, pressing B both types the letter and (if the mouse hovers an inventory slot) triggers slot selection. Rare in practice; accepted for v1 — same behavior class as other inventory-tweak mods.
