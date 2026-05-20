package net.ocechat.shockwave.cosmetics.visuals.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CondensationSphereParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected CondensationSphereParticle(ClientLevel level, double x, double y, double z,
                                         SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        this.sprites    = sprites;
        this.lifetime   = 3;
        this.quadSize   = 0.18f;
        this.alpha      = 0.75f;
        this.hasPhysics = false;
        this.xd = 0; this.yd = 0; this.zd = 0;

        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        // Fast fade — visible for exactly 3 ticks
        this.alpha = 0.75f * (1.0f - (float) this.age / this.lifetime);
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
            return new CondensationSphereParticle(level, x, y, z, sprites);
        }
    }
}