package net.ocechat.shockwave.cosmetics.visuals.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.ocechat.shockwave.cosmetics.visuals.ShockwaveParticles;

public record DebrisParticleData(float vx, float vy, float vz)
        implements ParticleOptions {

    public static final MapCodec<DebrisParticleData> CODEC =
            RecordCodecBuilder.mapCodec(inst -> inst.group(
                    Codec.FLOAT.fieldOf("vx").forGetter(DebrisParticleData::vx),
                    Codec.FLOAT.fieldOf("vy").forGetter(DebrisParticleData::vy),
                    Codec.FLOAT.fieldOf("vz").forGetter(DebrisParticleData::vz)
            ).apply(inst, DebrisParticleData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DebrisParticleData> STREAM_CODEC =
            StreamCodec.composite(
                    net.minecraft.network.codec.ByteBufCodecs.FLOAT, DebrisParticleData::vx,
                    net.minecraft.network.codec.ByteBufCodecs.FLOAT, DebrisParticleData::vy,
                    net.minecraft.network.codec.ByteBufCodecs.FLOAT, DebrisParticleData::vz,
                    DebrisParticleData::new
            );

    @Override
    public ParticleType<DebrisParticleData> getType() {
        return ShockwaveParticles.DEBRIS.get();
    }
}