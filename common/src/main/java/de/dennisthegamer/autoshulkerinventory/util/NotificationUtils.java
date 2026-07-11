package de.dennisthegamer.autoshulkerinventory.util;

import de.dennisthegamer.autoshulkerinventory.config.ModConfig;
import de.dennisthegamer.autoshulkerinventory.config.ParticleStyle;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;

public class NotificationUtils {

    /**
     * Notifies the player that items were stored in a shulker box.
     * Handles chat messages, sounds, and visual indicators based on config.
     *
     * @param player The player to notify
     * @param count The number of items stored
     */
    public static void notifyPlayer(Player player, int count) {
        if (player == null || count <= 0) {
            return;
        }

        ModConfig config = ModConfig.getInstance();

        // Send chat notification if enabled
        if (config.enableChatNotifications) {
            Component message = Component.translatable("message.auto_shulker_inventory.stored_items", count);
            player.sendOverlayMessage(message);  // actionBar (above hotbar)
        }

        // Play sound effect if enabled
        if (config.enableSoundEffects) {
            player.level().playSound(
                null,  // null = play for all nearby players
                player.blockPosition(),
                SoundEvents.SHULKER_BOX_CLOSE,
                SoundSource.BLOCKS,
                0.5f,  // volume
                1.0f   // pitch
            );
        }

        // Visual indicators (particle effects)
        if (config.enableVisualIndicators && player.level() instanceof ServerLevel level) {
            double x = player.getX();
            double y = player.getY() + 1.0;
            double z = player.getZ();
            ParticleStyle style = config.particleStyle != null ? config.particleStyle : ParticleStyle.ENCHANT;
            level.sendParticles(style.getParticle(),
                    x, y, z,
                    8,       // count
                    0.3,     // xDist
                    0.5,     // yDist
                    0.3,     // zDist
                    0.1      // speed
            );
        }
    }
}
