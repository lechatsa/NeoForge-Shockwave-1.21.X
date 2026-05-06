package net.ocechat.shockwave.modules;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ocechat.shockwave.ShockwaveMod;
import net.ocechat.shockwave.utils.ShockwaveParticles;

public class ParticleModule {

    public static void spawn(Level level, Vec3 pos, float radius) {

        if (!(level instanceof ServerLevel serverLevel)) return;

        if (ShockwaveMod.DEBUG)
            ShockwaveMod.LOGGER.info("[Shockwave] (ParticleModule) Spawning particles at {}", pos);

        // Flash — single centered, visible at range
        serverLevel.sendParticles(
                ShockwaveParticles.FLASH.get(),
                pos.x, pos.y, pos.z,
                1, 0, 0, 0, 0
        );

        // Fireball — single centered, expands via quadSize in tick()
        serverLevel.sendParticles(
                ShockwaveParticles.FIREBALL.get(),
                pos.x, pos.y, pos.z,
                1, 0, 0, 0, 0
        );

        // Shockwave ring — at ground level
        serverLevel.sendParticles(
                ShockwaveParticles.SHOCKWAVE_RING.get(),
                pos.x, pos.y - radius * 0.3, pos.z,
                1, 0, 0, 0, 0
        );

        // Smoke column — multiple spread around center, rise upward
        int smokeCount = (int) (radius * 3);
        serverLevel.sendParticles(
                ShockwaveParticles.EXPLOSION_SMOKE.get(),
                pos.x, pos.y, pos.z,
                smokeCount,
                radius * 0.4, 0.2, radius * 0.4,
                0.01
        );

        // Debris — radial burst in all directions
        int debrisCount = (int) (radius * 5);
        for (int i = 0; i < debrisCount; i++) {
            double angle  = serverLevel.random.nextDouble() * Math.PI * 2;
            double elevation = (serverLevel.random.nextDouble() - 0.2) * Math.PI;
            float speed   = 0.3f + serverLevel.random.nextFloat() * 0.4f;

            double vx = Math.cos(angle) * Math.cos(elevation) * speed;
            double vy = Math.sin(elevation) * speed + 0.2;
            double vz = Math.sin(angle) * Math.cos(elevation) * speed;

            serverLevel.sendParticles(
                    ShockwaveParticles.DEBRIS.get(),
                    pos.x, pos.y, pos.z,
                    1, vx, vy, vz, 0
            );
        }
    }
}