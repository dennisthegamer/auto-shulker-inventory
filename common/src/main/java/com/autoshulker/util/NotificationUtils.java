package com.autoshulker.util;

import com.autoshulker.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

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
            player.displayClientMessage(message, true);  // true = actionBar (above hotbar)
        }

        // Play sound effect if enabled
        if (config.enableSoundEffects) {
            player.level().playSound(
                (Entity) null,  // null = play for all nearby players
                player.blockPosition(),
                SoundEvents.SHULKER_BOX_CLOSE,
                SoundSource.BLOCKS,
                0.5f,  // volume
                1.0f   // pitch
            );
        }

        // Visual indicators (particle effects) - future implementation
        if (config.enableVisualIndicators) {
            // TODO: Spawn particle effects around player
            // Example: ParticleTypes.PORTAL, ParticleTypes.ENCHANT
        }
    }
}
