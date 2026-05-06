package net.ocechat.shockwave.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireballParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected FireballParticle(ClientLevel level, double x, double y, double z,
                               double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;

        this.lifetime  = 14;
        this.quadSize  = 0.5f;
        this.alpha     = 1.0f;
        this.hasPhysics = false;

        // Warm orange-red color
        this.rCol = 1.0f;
        this.gCol = 0.4f;
        this.bCol = 0.05f;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        // Expand rapidly then slow down
        float progress = (float) this.age / this.lifetime;
        this.quadSize = 0.5f + progress * 6.0f * (1.0f - progress * 0.6f);

        // Fade out in the second half
        if (progress > 0.4f) {
            this.alpha = 1.0f - (progress - 0.4f) / 0.6f;
        }

        // Shift from orange to grey as it cools
        this.gCol = 0.4f - progress * 0.35f;
        this.bCol = 0.05f + progress * 0.15f;

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
            return new FireballParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}