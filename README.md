# Auto Shulker Inventory

A Minecraft Fabric mod that automatically stores items in shulker boxes when your inventory is full.

## Features

- Automatically transfers items to shulker boxes when inventory becomes full
- Works with shift-click operations
- Server-side processing for multiplayer compatibility
- Supports all shulker box variants (colored + undyed)
- No configuration needed - works out of the box

## Compatibility

This mod has two branches for different Minecraft versions:

### Branch: `mc1.21-1.21.8`
- Minecraft 1.21 through 1.21.8
- Fabric Loader 0.18.3+
- Fabric API required
- Java 21+

### Branch: `mc1.21.9-1.21.11`
- Minecraft 1.21.9 through 1.21.11
- Fabric Loader 0.18.3+
- Fabric API required
- ModMenu 17.0.0-alpha.1+ (optional, recommended)
- Java 21+

## Download

Download the latest release from [Modrinth](https://modrinth.com/mod/auto-shulker-inventory) or [GitHub Releases](https://github.com/DennisTheGamer/auto-shulker-inventory/releases).

Make sure to download the correct version for your Minecraft version:
- `auto_shulker_inventory-1.1.0-mc1.21-1.21.8.jar` for Minecraft 1.21-1.21.8
- `auto_shulker_inventory-1.1.0-mc1.21.9-1.21.11.jar` for Minecraft 1.21.9-1.21.11

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download Auto Shulker Inventory (this mod)
4. Place both JAR files in your `mods` folder
5. Launch Minecraft

## How It Works

When your inventory is full and you pick up items or shift-click items:
1. The mod checks if there are any shulker boxes in your inventory with available space
2. If found, it automatically stores the overflow items in the shulker box
3. Items are stacked efficiently before being stored
4. You'll see a log message indicating items were auto-stored

## Building from Source

```bash
# Clone the repository
git clone https://github.com/DennisTheGamer/auto-shulker-inventory.git
cd auto-shulker-inventory

# For Minecraft 1.21-1.21.8
git checkout mc1.21-1.21.8
./gradlew build

# For Minecraft 1.21.9-1.21.11
git checkout mc1.21.9-1.21.11
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

- **Author**: DennisTheGamer
- **Built with**: Fabric, Fabric API
- **Icon**: Custom design

## Support

- Report bugs on [GitHub Issues](https://github.com/DennisTheGamer/auto-shulker-inventory/issues)
- Visit the [Modrinth page](https://modrinth.com/mod/auto-shulker-inventory) for more information
