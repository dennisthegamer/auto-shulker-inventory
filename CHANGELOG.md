# Changelog

All notable changes to Auto Shulker Inventory will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-12-19

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

## [1.0.0] - 2025-12-18

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
