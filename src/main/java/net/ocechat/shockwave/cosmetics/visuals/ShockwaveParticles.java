package net.ocechat.shockwave.cosmetics.visuals;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ocechat.shockwave.ShockwaveMod;
import net.ocechat.shockwave.cosmetics.visuals.client.particle.DebrisParticleData;

public class ShockwaveParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ShockwaveMod.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIREBALL =
            PARTICLES.register("fireball", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHOCKWAVE_RING =
            PARTICLES.register("shockwave_ring", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EXPLOSION_SMOKE =
            PARTICLES.register("explosion_smoke", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CONDENSATION_SPHERE =
            PARTICLES.register("condensation_sphere", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, ParticleType<DebrisParticleData>> DEBRIS =
            PARTICLES.register("debris", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<DebrisParticleData> codec() {
                    return DebrisParticleData.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, DebrisParticleData> streamCodec() {
                    return DebrisParticleData.STREAM_CODEC;
                }
            });

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLASH =
            PARTICLES.register("flash", () -> new SimpleParticleType(false));
}