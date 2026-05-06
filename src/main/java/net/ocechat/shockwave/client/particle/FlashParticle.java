package net.ocechat.shockwave.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlashParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected FlashParticle(ClientLevel level, double x, double y, double z,
                            double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        this.sprites = sprites;

        this.lifetime   = 4;
        this.quadSize   = 12.0f; // large enough to fill screen at close range
        this.alpha      = 1.0f;
        this.hasPhysics = false;

        this.rCol = 1.0f;
        this.gCol = 0.95f;
        this.bCol = 0.8f;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        // Very fast fade — gone in 4 ticks
        float progress = (float) this.age / this.lifetime;
        this.alpha = 1.0f - progress;
        this.quadSize = 12.0f * (1.0f - progress * 0.3f);

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
            return new FlashParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}