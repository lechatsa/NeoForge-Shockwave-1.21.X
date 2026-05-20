package net.ocechat.shockwave.cosmetics.visuals.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExplosionSmokeParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected ExplosionSmokeParticle(ClientLevel level, double x, double y, double z,
                                     double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;

        this.lifetime   = 80 + level.random.nextInt(40);
        this.quadSize   = 0.6f + (float) level.random.nextFloat() * 0.4f;
        this.alpha      = 0.85f;
        this.hasPhysics = false;

        // Slight random horizontal drift
        this.xd = vx + (level.random.nextDouble() - 0.5) * 0.05;
        this.yd = 0.04 + level.random.nextDouble() * 0.03; // rises slowly
        this.zd = vz + (level.random.nextDouble() - 0.5) * 0.05;

        // Dark grey smoke
        float grey = 0.2f + level.random.nextFloat() * 0.15f;
        this.rCol = grey;
        this.gCol = grey;
        this.bCol = grey;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        float progress = (float) this.age / this.lifetime;

        // Slowly grows as it rises
        this.quadSize += 0.015f;

        // Drag on horizontal movement
        this.xd *= 0.98;
        this.zd *= 0.98;

        // Fade out in the last 40%
        if (progress > 0.6f) {
            this.alpha = 0.85f * (1.0f - (progress - 0.6f) / 0.4f);
        }

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
            return new ExplosionSmokeParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}