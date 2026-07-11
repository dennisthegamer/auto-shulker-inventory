# Design: Konfigurierbarer Ziel-Slot (Preferred Empty Slot)

**Datum:** 2026-07-11
**Branch:** `multiloader-test`
**Status:** Entwurf zur Review

## Ziel

Der Spieler kann festlegen, welcher Inventar-Slot beim Auto-Storage geleert wird.
Bisher wählt die Mod den Slot automatisch (Iteration von Slot 35 abwärts); der frei
werdende Slot ist damit unvorhersehbar. Künftig kann ein fester Slot konfiguriert
werden — per Config-Wert und per Keybind im Inventar-Screen, mit grüner Umrandung
als visueller Markierung.

**Nicht-Ziele:** Am Auslöser ändert sich nichts (Auto-Storage greift weiterhin nur
bei komplett vollem Inventar). Die Shift-Click-Logik (`AbstractContainerMenuMixin`)
bleibt unberührt. Keine Markierung/Auswahl in fremden Container-Screens (Kisten,
Shulker etc.) — nur im eigenen Inventar-Screen.

## 1. Config-Erweiterung

Neues Feld in `common/.../config/ModConfig.java`:

```java
public int preferredEmptySlot = -1; // -1 = automatisch (bisheriges Verhalten)
```

- Gültiger Bereich: −1 bis 35 (0–8 = Hotbar, 9–35 = Hauptinventar).
- Validierung beim Laden: Werte außerhalb des Bereichs werden auf −1 zurückgesetzt.
- `ConfigScreen`: Zahlenfeld (IntField, Bounds −1..35) in der Kategorie
  „Priority System", mit Tooltip, der die Slot-Nummerierung und −1 = auto erklärt.
- Neue Lang-Einträge in `en_us.json` (Option-Titel + Tooltip).

## 2. Speicher-Logik (`InventoryMixin.tryMoveToShulker`)

Ablauf bei vollem Inventar und gefundener Shulker-Box mit Platz:

1. **Konfigurierter Slot zuerst:** Ist `preferredEmptySlot` im Bereich 0–35, nicht
   der Slot der Ziel-Shulker-Box selbst, und enthält er weder eine Shulker-Box noch
   einen leeren Stack, wird versucht, diesen Stack in die Box zu verschieben.
2. **Fallback (Variante A):** Kann der konfigurierte Slot nicht (vollständig)
   geleert werden — Slot enthält eine Shulker-Box, Item passt in keine Box, Box
   läuft voll — greift die bisherige Auto-Schleife (Slot 35 abwärts) als Fallback.
   Bei aktiviertem `enableDebugLogging` wird geloggt, warum ausgewichen wurde.
3. Die Benachrichtigung (`NotificationUtils`) zählt wie bisher die insgesamt
   verschobenen Items über beide Schritte hinweg.

## 3. Keybind (Ingame-Auswahl)

- Neues KeyMapping, Standard-Taste **B**, Kategorie „Auto Shulker Inventory",
  änderbar über die Minecraft-Steuerungsoptionen.
- Aktiv **nur im eigenen Inventar-Screen** (`InventoryScreen`, Überlebensmodus).
- Verhalten bei Tastendruck, während ein Slot gehovert wird, der zum
  Spieler-Inventar gehört (Container-Slot 0–35):
  - Slot ist nicht der konfigurierte → `preferredEmptySlot` = dieser Slot,
    Config wird sofort gespeichert.
  - Slot ist bereits der konfigurierte → zurück auf −1 (automatisch),
    Config wird sofort gespeichert.
- Beide Aktionen geben eine kurze, lokalisierte Chat-Nachricht aus
  (z. B. „Target slot set to 31" / „Target slot reset to automatic").
- Hovert der Spieler keinen eigenen Inventar-Slot (z. B. Crafting-Feld,
  Rüstungs-Slot), passiert nichts.

## 4. Grüne Umrandung

- Der konfigurierte Slot erhält eine grüne Umrandung (`GuiGraphics.renderOutline`,
  Farbe `0xFF00FF00`), gezeichnet nach dem Slot-Rendering.
- Sichtbar **nur im eigenen Inventar-Screen**, und nur wenn
  `preferredEmptySlot != -1`.
- Kein eigener Config-Toggle: Die Umrandung ist an die Slot-Konfiguration
  gekoppelt (−1 = keine Umrandung).

## 5. Multiloader-Umsetzung

Gemeinsame Logik in `common/`, dünne loader-spezifische Hooks:

- **Common:** neue Client-Hilfsklasse `com.autoshulker.client.SlotSelectionHandler`
  mit statischen Methoden:
  - `handleKeyPress(InventoryScreen screen)` — ermittelt den gehoverten Slot,
    setzt/löscht die Config, sendet die Chat-Nachricht.
  - `renderSlotHighlight(GuiGraphics gfx, InventoryScreen screen)` — zeichnet
    die Umrandung am konfigurierten Slot.
- **Fabric** (`fabric/`-Client-Entrypoint): Keybind über `KeyBindingHelper`
  registrieren; Screen-Hooks über `ScreenEvents.afterRender` bzw. Key-Events
  (`ScreenKeyboardEvents`) nur für `InventoryScreen` anmelden.
- **NeoForge** (`neoforge/`-Client-Events): Keybind über
  `RegisterKeyMappingsEvent`; Rendering über `ScreenEvent.Render.Post`,
  Tastendruck über `ScreenEvent.KeyPressed.Pre` — jeweils mit
  `instanceof InventoryScreen`-Filter.
- Slot-Zuordnung einheitlich über `slot.container == player.getInventory()`
  und `slot.getContainerSlot()` (liefert 0–35).

## 6. Fehlerfälle

| Fall | Verhalten |
|---|---|
| Config-Wert außerhalb −1..35 (manuell editierte JSON) | Beim Laden auf −1 zurückgesetzt |
| Konfigurierter Slot enthält Shulker-Box | Fallback auf Auto-Schleife, Debug-Log |
| Konfigurierter Slot ist der Ziel-Box-Slot | Fallback auf Auto-Schleife, Debug-Log |
| Item im konfigurierten Slot passt in keine Box | Fallback auf Auto-Schleife, Debug-Log |
| Keybind-Druck ohne gehoverten eigenen Inventar-Slot | Keine Aktion |

## 7. Tests / Verifikation

Manuelle Verifikation ingame, auf **beiden Loadern** (Fabric + NeoForge, via
`runClient`):

1. Slot per Config-Screen setzen → grüne Umrandung erscheint am richtigen Slot.
2. Inventar füllen, Items aufsammeln → genau der konfigurierte Slot wird geleert.
3. Shulker-Box in den konfigurierten Slot legen, Items aufsammeln → Fallback
   leert einen anderen Slot; Debug-Log erklärt den Grund.
4. Keybind auf gehovertem Slot → Config ändert sich, Umrandung wandert,
   Chat-Nachricht erscheint; erneuter Druck auf denselben Slot → zurück auf auto.
5. Kisten-Screen öffnen → keine Umrandung, Keybind ohne Wirkung.
6. `preferredEmptySlot = -1` → Verhalten identisch zu vorher (Regression).
