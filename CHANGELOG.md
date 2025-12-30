# Changelog

All notable changes to Auto Shulker Inventory will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.1] - 2025-12-30

### Added
- **Configuration System**: Full in-game configuration via ModMenu
  - Cloth Config integration for GUI settings screen
  - JSON-based config file (`config/auto_shulker_inventory.json`)
  - Four configuration categories:
    1. **Main Features**: Toggle auto-storage and shift-click storage independently
    2. **Priority System**: Configure storage priority order (cursor, container, inventory)
    3. **Notifications & Feedback**: Enable/disable chat notifications, sound effects, and visual indicators
    4. **Advanced Options**: Debug logging and storage delay settings
- **Cursor Stack Priority**: When using Shift+Click, items are now preferentially stored in shulker boxes held by the cursor (in hand)
- Storage priority order for Shift+Click operations:
  1. Shulker box held by cursor (highest priority)
  2. Shulker boxes in opened containers
  3. Shulker boxes in player inventory
- ModMenu integration with config button
- Localization support for all config options (en_us language file)

### Changed
- `storeInAnyShulker()` now has an overloaded version that accepts a cursor stack parameter
- Improved logging to show action type and button information
- All features can now be toggled individually via the config screen

### Fixed
- Shift+Right-Click now properly stores items in shulker boxes
- Items now go into the shulker box you're holding (with Shift+Click) instead of a random one in your inventory
- Added explicit button parameter validation to support both left-click (button 0) and right-click (button 1) shift operations
- Environment metadata corrected from `*` (both) to `client` for proper Modrinth compatibility

## [1.2.0] - 2025-12-19

### Added
- Container shulker box support: Items can now be automatically stored in shulker boxes located inside opened containers (chests, ender chests, etc.)
- New utility methods in ShulkerUtils:
  - `findShulkerWithSpaceInContainer()`: Detects shulker boxes in opened containers
  - `storeInShulkerAtSlot()`: Stores items in shulker boxes at specific container slots
  - `storeInAnyShulker()`: Unified storage method that prioritizes container shulker boxes over inventory shulker boxes

### Changed
- ScreenHandlerMixin now uses `storeInAnyShulker()` to check both container and inventory shulker boxes
- Storage priority: Container shulker boxes are checked first, then player inventory shulker boxes

### Fixed
- Shift-click logic now properly checks the clicked slot instead of cursor stack
- Items can now be stored in shulker boxes even when they're not in the player's inventory

## [1.1.0] - 2024-12-19

### Added
- ModMenu integration for Minecraft 1.21.9-1.21.11
- Optimized build strategy with version-specific compatibility ranges
- Comprehensive README with installation and usage instructions
- GitHub repository with separate branches for different Minecraft versions

### Changed
- **Breaking**: Refactored package structure from `com.example.dennisthegamer` to `com.autoshulker`
- **Breaking**: Changed license from CC0-1.0 to MIT
- Updated API calls for Minecraft 1.21.9+ (getWorld() → getEntityWorld())
- Consolidated builds into 2 version-specific JARs instead of 12 individual versions
  - `mc1.21-1.21.8.jar`: Compatible with Minecraft 1.21 through 1.21.8
  - `mc1.21.9-1.21.11.jar`: Compatible with Minecraft 1.21.9 through 1.21.11
- Updated Fabric API dependencies to latest versions for each Minecraft version
- Improved icon packaging (512x512 PNG, properly embedded)

### Removed
- All example code and placeholder mixins
- Unused client-side mixin configuration
- Development build artifacts from repository

### Fixed
- Icon not displaying correctly in mod menu
- Compatibility issues with newer Minecraft versions (1.21.9+)
- Improved code organization and maintainability

## [1.0.0] - 2024-12-18

### Added
- Initial release
- Automatic item storage in shulker boxes when inventory is full
- Support for shift-click operations
- Server-side processing for multiplayer compatibility
- Support for all 17 shulker box variants (colored + undyed)
- Efficient item stacking before storage
- Compatibility with Minecraft 1.21 through 1.21.11
- Fabric Loader 0.18.3+ support
- Fabric API integration

### Features
- Zero configuration required
- Works seamlessly in both singleplayer and multiplayer
- Automatic detection of available shulker boxes
- Smart item distribution across multiple shulker boxes
- Console logging for debugging

---

## Version Comparison

| Version | Minecraft Support | ModMenu | License | Package Name |
|---------|------------------|---------|---------|--------------|
| 1.0.0   | 1.21 - 1.21.11   | ❌      | CC0-1.0 | com.example.dennisthegamer |
| 1.1.0   | 1.21 - 1.21.11   | ✅*     | MIT     | com.autoshulker |

*ModMenu only included in the 1.21.9-1.21.11 build

## Migration Guide: 1.0.0 → 1.1.0

Due to the package name change, you **must** remove version 1.0.0 before installing 1.1.0:

1. Stop your Minecraft client/server
2. Remove `auto_shulker_inventory-1.0.0.jar` from your mods folder
3. Download the correct 1.1.0 version for your Minecraft version:
   - For MC 1.21-1.21.8: `auto_shulker_inventory-1.1.0-mc1.21-1.21.8.jar`
   - For MC 1.21.9-1.21.11: `auto_shulker_inventory-1.1.0-mc1.21.9-1.21.11.jar`
4. Place the new JAR in your mods folder
5. Start Minecraft

**Note**: Your shulker boxes and items will not be affected by this update. The mod simply has a new internal package structure.
