package net.ocechat.shockwave.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebrisParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected DebrisParticle(ClientLevel level, double x, double y, double z,
                             double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;

        this.lifetime   = 20 + level.random.nextInt(20);
        this.quadSize   = 0.1f + (float) level.random.nextFloat() * 0.15f;
        this.alpha      = 1.0f;
        this.hasPhysics = true; // collides with blocks

        // Radial velocity passed via vx/vy/vz from ParticleModule
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;

        // Earthy brown-grey color
        this.rCol = 0.35f + level.random.nextFloat() * 0.2f;
        this.gCol = 0.25f + level.random.nextFloat() * 0.15f;
        this.bCol = 0.15f + level.random.nextFloat() * 0.1f;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        // Gravity
        this.yd -= 0.04;

        // Air resistance
        this.xd *= 0.96;
        this.zd *= 0.96;

        // Fade at end of life
        float progress = (float) this.age / this.lifetime;
        if (progress > 0.7f) {
            this.alpha = 1.0f - (progress - 0.7f) / 0.3f;
        }

        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new DebrisParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}