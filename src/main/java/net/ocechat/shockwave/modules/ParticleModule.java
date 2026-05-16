package net.ocechat.shockwave.modules;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ocechat.shockwave.ShockwaveMod;
import net.ocechat.shockwave.client.particle.DebrisParticleData;
import net.ocechat.shockwave.events.CondensationSphereHandler;
import net.ocechat.shockwave.utils.BlockCluster;
import net.ocechat.shockwave.utils.ShockwaveParticles;

import java.util.List;

public class ParticleModule {

    public static void spawn(Level level, Vec3 pos, float radius) {

        if (!(level instanceof ServerLevel serverLevel)) return;

        if (ShockwaveMod.DEBUG)
            ShockwaveMod.LOGGER.info("[Shockwave] (ParticleModule) Spawning particles at {}", pos);


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
                1.0
        );

        List<BlockCluster> clusterList = TerrainModule.defineCluster(level, pos, radius);


        // Debris — speed and count scale with explosion radius
        int debrisCount = (int) (radius * 6);
        float speedBase = 0.3f + radius * 0.08f;   // small explosion: ~0.5  |  large: ~1.1+
        float speedVar  = radius * 0.05f;

        Pair<Double, Double> pair = BlockCluster.getAngles(clusterList);

        if (ShockwaveMod.DEBUG)
            ShockwaveMod.LOGGER.info("[Shockwave] (ParticleModule) Spawning debris particles with pitch : {}, yaw : {}", pair.getSecond(), pair.getFirst());

        double yaw = pair.getFirst();
        double pitch = pair.getSecond();

        for (int i = 0; i < debrisCount; i++) {

            double yawRandomised = yaw + (serverLevel.random.nextDouble() - 0.5) * (Math.PI / 2);
            double pitchRandomised = pitch + (serverLevel.random.nextDouble() - 0.5) * (Math.PI / 2);

            float speed      = speedBase + serverLevel.random.nextFloat() * speedVar;

            float vx = (float) (Math.cos(yawRandomised) * Math.cos(pitchRandomised) * speed);
            float vy = (float) (Math.sin(pitchRandomised) * speed);
            float vz = (float) (Math.sin(yawRandomised) * Math.cos(pitchRandomised) * speed);

            serverLevel.sendParticles(
                    new DebrisParticleData(vx, vy, vz),
                    pos.x, pos.y, pos.z,
                    1, 0, 0, 0, 0
            );
        }


    }
}