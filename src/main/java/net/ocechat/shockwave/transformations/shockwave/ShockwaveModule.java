package net.ocechat.shockwave.transformations.shockwave;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.ocechat.shockwave.ShockwaveMod;

import static net.ocechat.shockwave.ShockwaveConfig.*;
import static net.ocechat.shockwave.utility.ExplosionFormula.calculateDamage;


public class ShockwaveModule {

    // Base lethal radius for 1 TNT in Minecraft blocks

    public static void apply(Level level, Vec3 center, float power, int numberOfTNT) {

        double r = power * SHOCKWAVE_MULTIPLIER.get();

        level.getEntitiesOfClass(Entity.class,
                AABB.ofSize(center, r * 2, r * 2, r * 2)
        ).forEach(entity -> {

            // Physics values — kept for debug/logging only
            long   E = calculateEnergy(numberOfTNT);
            double d = calculateDistance(entity, center);
            double Z = calculateDestructionFactor(d, E);
            double P = calculatePressure(d, E);
            double v = calculateVelocity(P);

            // Damage and impulse — calibrated in Minecraft units
            float damage = calculateDamage(d, numberOfTNT);

            applyImpulse(entity, center, d, r, v);

            if (damage >= 1f && entity instanceof LivingEntity living) {
                living.hurt(living.level().damageSources().explosion(null, null), damage);

                if (ShockwaveMod.DEBUG) ShockwaveMod.LOGGER.info("[Shockwave] (ShockwaveModule) {} | damage={} Z={} P={}Pa v={}m/s d={}m E={}J", entity.getName().getString(), damage, Z, P, v, d, E);

            } else if (ShockwaveMod.DEBUG) {

                ShockwaveMod.LOGGER.info("[Shockwave] (ShockwaveModule) Skipped {} (damage = {} < 1 or not living)", entity.getName().getString(), damage);

            }
        });
    }

    // -- E (J) : informational only
    public static long calculateEnergy(int numberOfTNT) {
        return (long) numberOfTNT * ENERGIE_TNT.get();
    }

    // -- d (blocks) : clamped to prevent division by zero
    public static double calculateDistance(Entity entity, Vec3 center) {
        return Math.max(entity.position().distanceTo(center), 0.1);
    }

    // -- Z : Hopkinson-Cranz scaled distance, for logging
    public static double calculateDestructionFactor(double d, long E) {
        return d / Math.pow(E, 1.0 / 3.0);
    }

    // -- P (Pa) : peak overpressure, for logging
    public static double calculatePressure(double d, long E) {
        return Math.pow(E, 1.0 / 3.0) / d;
    }

    // -- v (m/s) : shockwave velocity, used for impulse scaling
    public static double calculateVelocity(double P) {
        double gamma = GAMMA.get();
        double P0    = PRESSURE.get();
        double c0    = SOUND_SPEED.get();
        return c0 * Math.sqrt(1.0 + ((gamma + 1.0) / (2.0 * gamma)) * (P / P0));
    }

    // -- Impulse : radial push, v used as a relative strength indicator
    private static void applyImpulse(Entity entity, Vec3 center, double d, double r, double v) {
        if (d >= r) return;

        Vec3 delta = entity.position().subtract(center);
        if (delta.lengthSqr() < 1e-6) return;

        double forceFactor = (1.0 - d / r) * (v / 400.0);
        forceFactor = Math.min(forceFactor, 3.0);

        Vec3 impulse = delta.normalize().scale(forceFactor)
                .add(0, forceFactor * 0.35, 0);
        entity.setDeltaMovement(entity.getDeltaMovement().add(impulse));
        entity.hurtMarked = true;
    }
}