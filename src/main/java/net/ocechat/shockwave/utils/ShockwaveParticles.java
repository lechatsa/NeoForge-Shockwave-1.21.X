package net.ocechat.shockwave.utils;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ocechat.shockwave.ShockwaveMod;

public class ShockwaveParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ShockwaveMod.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIREBALL =
            PARTICLES.register("fireball", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHOCKWAVE_RING =
            PARTICLES.register("shockwave_ring", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EXPLOSION_SMOKE =
            PARTICLES.register("explosion_smoke", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DEBRIS =
            PARTICLES.register("debris", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLASH =
            PARTICLES.register("flash", () -> new SimpleParticleType(false));
}