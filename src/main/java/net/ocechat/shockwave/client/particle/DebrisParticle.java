package net.ocechat.shockwave.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebrisParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private int smokeTimer = 0;

    protected DebrisParticle(ClientLevel level, double x, double y, double z,
                             DebrisParticleData data, SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        this.sprites = sprites;

        this.lifetime   = 25 + level.random.nextInt(20);
        this.quadSize   = 0.12f + level.random.nextFloat() * 0.1f;
        this.alpha      = 1.0f;
        this.hasPhysics = true;

        // Velocity comes from DebrisParticleData — reliable radial direction
        this.xd = data.vx();
        this.yd = data.vy();
        this.zd = data.vz();

        this.rCol = 0.35f + level.random.nextFloat() * 0.2f;
        this.gCol = 0.25f + level.random.nextFloat() * 0.15f;
        this.bCol = 0.15f + level.random.nextFloat() * 0.1f;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        // Save position before physics update for trail
        double prevX = this.x;
        double prevY = this.y;
        double prevZ = this.z;

        super.tick();

        // Gravity
        this.yd -= 0.035;

        // Air resistance
        this.xd *= 0.97;
        this.zd *= 0.97;

        // Campfire smoke trail every 2 ticks while moving fast enough
        smokeTimer++;
        double speed = Math.sqrt(xd * xd + yd * yd + zd * zd);
        if (smokeTimer % 2 == 0 && speed > 0.05) {
            this.level.addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    prevX, prevY, prevZ,
                    0, 0.02, 0   // slight upward drift
            );
        }

        // Fade at end of life
        float progress = (float) this.age / this.lifetime;
        if (progress > 0.65f) {
            this.alpha = 1.0f - (progress - 0.65f) / 0.35f;
        }

        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<DebrisParticleData> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }

        @Override
        public Particle createParticle(DebrisParticleData data, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new DebrisParticle(level, x, y, z, data, sprites);
        }
    }
}