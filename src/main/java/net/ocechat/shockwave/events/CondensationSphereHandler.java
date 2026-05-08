package net.ocechat.shockwave.events;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.ocechat.shockwave.ShockwaveMod;
import net.ocechat.shockwave.utils.ShockwaveParticles;
import net.ocechat.shockwave.modules.ShockwaveModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.ocechat.shockwave.ShockwaveConfig.*;

@EventBusSubscriber(modid = ShockwaveMod.MOD_ID)
public class CondensationSphereHandler {

    // Number of Fibonacci points per tick — exposed to config later
    private static final int POINTS_PER_TICK = 2560;

    // Active spheres being simulated
    private static final List<CondensationSphere> activeSpheres = new ArrayList<>();

    // Register a new condensation sphere when an explosion occurs
    public static void spawn(ServerLevel level, Vec3 center, int numberOfTNT) {
        activeSpheres.add(new CondensationSphere(level, center, numberOfTNT));

        if (ShockwaveMod.DEBUG)
            ShockwaveMod.LOGGER.info("[Shockwave] (CondensationSphere) Spawned at {} for {} TNT",
                    center, numberOfTNT);
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        Iterator<CondensationSphere> it = activeSpheres.iterator();
        while (it.hasNext()) {
            CondensationSphere sphere = it.next();

            // Only tick spheres belonging to this level
            if (sphere.level != serverLevel) continue;

            boolean alive = sphere.tick();
            if (!alive) {
                it.remove();
                if (ShockwaveMod.DEBUG)
                    ShockwaveMod.LOGGER.info("[Shockwave] (CondensationSphere) Expired at {}",
                            sphere.center);
            }
        }
    }

    // --- Inner class representing one expanding condensation sphere ---
    private static class CondensationSphere {

        final ServerLevel level;
        final Vec3        center;
        final int         numberOfTNT;
        final long        E;

        double currentRadius = 0.5; // starts just outside center

        CondensationSphere(ServerLevel level, Vec3 center, int numberOfTNT) {
            this.level       = level;
            this.center      = center;
            this.numberOfTNT = numberOfTNT;
            this.E           = ShockwaveModule.calculateEnergy(numberOfTNT);
        }

        // Returns true if still alive, false when the wave becomes subsonic
        boolean tick() {

            // Advance radius first
            currentRadius += 5.0; // temporary fixed speed — 5 blocks/tick

            // Safety cap
            if (currentRadius > 200.0) return false;

            // Spawn particles at current radius
            spawnFibonacciSphere(currentRadius);

            if (ShockwaveMod.DEBUG)
                ShockwaveMod.LOGGER.info(
                        "[Shockwave] (CondensationSphere) r={}", currentRadius);

            // Stop after ~3 seconds (60 ticks)
            return currentRadius < 100.0;
        }

        private void spawnFibonacciSphere(double r) {
            double goldenAngle = Math.PI * (1.0 + Math.sqrt(5.0)); // ≈ 2π * φ

            for (int i = 0; i < POINTS_PER_TICK; i++) {
                // Fibonacci sphere distribution — uniform point spread
                double phi   = Math.acos(1.0 - 2.0 * (i + 0.5) / POINTS_PER_TICK);
                double theta = goldenAngle * i;

                double x = center.x + r * Math.sin(phi) * Math.cos(theta);
                double y = center.y + r * Math.cos(phi);
                double z = center.z + r * Math.sin(phi) * Math.sin(theta);

                level.sendParticles(
                        ParticleTypes.LARGE_SMOKE,
                        x, y, z,
                        1, 0, 0, 0, 0
                );

                // ShockwaveParticles.CONDENSATION_SPHERE.get();

            }
        }
    }
}