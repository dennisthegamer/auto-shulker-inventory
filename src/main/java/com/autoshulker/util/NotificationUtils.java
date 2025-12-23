package com.autoshulker.util;

import com.autoshulker.config.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class NotificationUtils {

    /**
     * Notifies the player that items were stored in a shulker box.
     * Handles chat messages, sounds, and visual indicators based on config.
     *
     * @param player The player to notify
     * @param count The number of items stored
     */
    public static void notifyPlayer(PlayerEntity player, int count) {
        if (player == null || count <= 0) {
            return;
        }

        ModConfig config = ModConfig.getInstance();

        // Send chat notification if enabled
        if (config.enableChatNotifications) {
            Text message = Text.translatable("message.auto_shulker_inventory.stored_items", count);
            player.sendMessage(message, true);  // true = actionBar (above hotbar)
        }

        // Play sound effect if enabled
        if (config.enableSoundEffects) {
            player.getEntityWorld().playSound(
                null,  // null = play for all nearby players
                player.getBlockPos(),
                SoundEvents.BLOCK_SHULKER_BOX_CLOSE,
                SoundCategory.BLOCKS,
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
