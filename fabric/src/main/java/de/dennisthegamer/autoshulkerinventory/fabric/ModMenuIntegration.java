package de.dennisthegamer.autoshulkerinventory.fabric;

import de.dennisthegamer.autoshulkerinventory.config.ConfigScreen;
import de.dennisthegamer.autoshulkerinventory.platform.Platforms;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // YACL is only suggested since the mod stopped needing it for persistence.
        // Without this guard, opening the config with ModMenu present but YACL
        // missing would throw NoClassDefFoundError. parent -> null is ModMenu's
        // own "this mod has no config screen" signal.
        if (Platforms.get().isModLoaded("yet_another_config_lib_v3")) {
            return ConfigScreen::create;
        }
        return parent -> null;
    }
}
