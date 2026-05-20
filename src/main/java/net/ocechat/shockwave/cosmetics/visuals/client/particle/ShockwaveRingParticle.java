package net.ocechat.shockwave.cosmetics.visuals.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShockwaveRingParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected ShockwaveRingParticle(ClientLevel level, double x, double y, double z,
                                    double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        this.sprites = sprites;

        this.lifetime   = 12;
        this.quadSize   = 0.1f;
        this.alpha      = 0.9f;
        this.hasPhysics = false;

        // Greyish-white shockwave
        this.rCol = 0.9f;
        this.gCol = 0.9f;
        this.bCol = 0.9f;

        // vx carries the target max radius passed from ParticleModule
        this.yd = 0;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        float progress = (float) this.age / this.lifetime;

        // Ring expands outward fast then decelerates
        this.quadSize = progress * 16.0f * (1.0f - progress * 0.5f);

        // Fade out quickly
        this.alpha = 1.0f - progress;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new ShockwaveRingParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}