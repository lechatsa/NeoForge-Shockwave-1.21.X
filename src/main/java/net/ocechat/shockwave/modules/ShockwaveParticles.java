package net.ocechat.shockwave.modules;

import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.ocechat.shockwave.ShockwaveMod;

public class ShockwaveParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ShockwaveMod.MOD_ID);

    // Aucune particule custom pour l'instant
    // Ajoutez-les ici quand vous voudrez remplacer les particules vanilla
}
