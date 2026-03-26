package com.autoshulker.config;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

public enum ParticleStyle {
    ENCHANT("enchant", ParticleTypes.ENCHANT),
    PORTAL("portal", ParticleTypes.PORTAL),
    HAPPY_VILLAGER("happy_villager", ParticleTypes.HAPPY_VILLAGER),
    COMPOSTER("composter", ParticleTypes.COMPOSTER),
    END_ROD("end_rod", ParticleTypes.END_ROD),
    WITCH("witch", ParticleTypes.WITCH),
    CHERRY_LEAVES("cherry_leaves", ParticleTypes.CHERRY_LEAVES);

    private final String id;
    private final ParticleOptions particle;

    ParticleStyle(String id, ParticleOptions particle) {
        this.id = id;
        this.particle = particle;
    }

    public String getId() {
        return id;
    }

    public ParticleOptions getParticle() {
        return particle;
    }

    public String getTranslationKey() {
        return "config.auto_shulker_inventory.particle_style." + id;
    }
}
