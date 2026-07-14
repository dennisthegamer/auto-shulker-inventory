# Auto Shulker Inventory

A Minecraft mod for **Fabric and NeoForge** that automatically stores items in shulker boxes when your inventory is full.

## Features

- Automatically transfers items to shulker boxes when inventory becomes full
- Works with shift-click operations, including shulker boxes held by the cursor or inside opened containers
- Configurable storage priority system (cursor → container → inventory)
- **Configurable target slot**: choose which inventory slot gets emptied — via config, or by hovering a slot in your inventory and pressing the keybind (default **B**); the selected slot is marked with a green outline
- In-game config screen (YetAnotherConfigLib; opened via ModMenu on Fabric or the Mods list on NeoForge)
- Chat/action-bar notifications and sound effects (each can be toggled)
- Supports all shulker box variants (colored + undyed)

## Compatibility

This branch (`mc1.21.9-1.21.11`) supports **Minecraft 1.21.9 through 1.21.11** on both loaders:

### Fabric
- Fabric Loader 0.18.3+
- Fabric API required
- YetAnotherConfigLib (YACL) required
- ModMenu (optional, recommended — adds the config button)
- Java 21+

### NeoForge
- NeoForge 21.9+
- YetAnotherConfigLib (YACL) required
- Java 21+

Other Minecraft versions live on their own branches:

| Branch | Minecraft | Loaders |
|---|---|---|
| `mc1.21-1.21.8` | 1.21 – 1.21.8 | Fabric + NeoForge |
| `mc1.21.9-1.21.11` | 1.21.9 – 1.21.11 | Fabric + NeoForge |
| `mc26.1` | 26.1 – 26.1.2 | Fabric + NeoForge |
| `mc26.2` | 26.2 | Fabric + NeoForge |

## Download

Download the latest release from [Modrinth](https://modrinth.com/mod/auto-shulker-inventory) or [GitHub Releases](https://github.com/DennisTheGamer/auto-shulker-inventory/releases).

Pick the JAR that matches your Minecraft version **and** your mod loader (`fabric` or `neoforge`):

| Minecraft | Fabric | NeoForge |
|---|---|---|
| 1.21 – 1.21.8 | `auto_shulker_inventory-fabric-1.5.0+mc1.21-1.21.8.jar` | `auto_shulker_inventory-neoforge-1.5.0+mc1.21-1.21.8.jar` |
| 1.21.9 – 1.21.11 | `auto_shulker_inventory-fabric-1.5.0+mc1.21.9-1.21.11.jar` | `auto_shulker_inventory-neoforge-1.5.0+mc1.21.9-1.21.11.jar` |
| 26.1 – 26.1.2 | `auto_shulker_inventory-fabric-1.5.0+mc26.1-26.1.2.jar` | `auto_shulker_inventory-neoforge-1.5.0+mc26.1-26.1.2.jar` |
| 26.2 | `auto_shulker_inventory-fabric-1.5.0+mc26.2.jar` | `auto_shulker_inventory-neoforge-1.5.0+mc26.2.jar` |

## Installation

### Fabric
1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) and [YetAnotherConfigLib](https://modrinth.com/mod/yacl)
3. Download Auto Shulker Inventory (the `-fabric` JAR)
4. Place the JAR files in your `mods` folder
5. Launch Minecraft

### NeoForge
1. Install [NeoForge](https://neoforged.net/)
2. Download [YetAnotherConfigLib](https://modrinth.com/mod/yacl)
3. Download Auto Shulker Inventory (the `-neoforge` JAR)
4. Place the JAR files in your `mods` folder
5. Launch Minecraft

## How It Works

When your inventory is full and you pick up items or shift-click items:
1. The mod checks if there are any shulker boxes with available space (cursor, opened container, then your inventory — each priority can be toggled in the config)
2. If found, it automatically stores the overflow items in the shulker box
3. Items are stacked efficiently before being stored
4. You'll see an action-bar message indicating how many items were auto-stored

### Target slot

By default the mod picks the slot to empty automatically. To always keep a specific slot free, either set "Preferred Target Slot" in the config (-1 = automatic, 0-8 = hotbar, 9-35 = main inventory), or hover the slot in your inventory screen and press **B** (rebindable in Options → Controls, category "Auto Shulker Inventory"). Press **B** on the same slot again to switch back to automatic. The configured slot is marked with a green outline. If the configured slot can't be emptied (it holds a shulker box, or its item fits in no box), the mod falls back to automatic selection.

## Configuration

Settings are stored in `config/auto_shulker_inventory.json` and can be edited in-game:
- **Fabric:** ModMenu → Auto Shulker Inventory → Config
- **NeoForge:** Mods → Auto Shulker Inventory → Config

## Building from Source

```bash
# Clone the repository
git clone https://github.com/DennisTheGamer/auto-shulker-inventory.git
cd auto-shulker-inventory

# For Minecraft 1.21.9-1.21.11 (Fabric + NeoForge)
git checkout mc1.21.9-1.21.11
./gradlew build
```

The compiled JARs will be in `fabric/build/libs/` and `neoforge/build/libs/`.

For any other Minecraft version, check out its branch (see the compatibility table above) and run `./gradlew build`; both loader JARs are produced the same way.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

- **Author**: Dennis_thegamer
- **Built with**: Architectury Loom, Fabric API, NeoForge, YetAnotherConfigLib
- **Icon**: Custom design

## Support

- Report bugs on [GitHub Issues](https://github.com/DennisTheGamer/auto-shulker-inventory/issues)
- Visit the [Modrinth page](https://modrinth.com/mod/auto-shulker-inventory) for more information
