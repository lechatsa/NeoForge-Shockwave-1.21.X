package net.ocechat.shockwave.modules;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;

public class ShockwaveModule {

    /** Rayon de l'onde de choc (config) */
    private static final float SHOCKWAVE_MULT = 1.8f;

    public static void apply ( Level level, Vec3 center, float radius ) {

        double r = radius * SHOCKWAVE_MULT;
        // Récupère toutes les entités dans le rayon
        level.getEntitiesOfClass( Entity.class, net.minecraft.world.phys.AABB.ofSize(center, r*2, r*2, r*2) ).forEach(entity -> {

            Vec3 delta = entity.position()
                    .subtract(center);

            double dist = delta.length();

            if (dist > 0 && dist < r) {
                // Vecteur radial atténué par distance
                double force = (1.0 - dist / r) * 2.5;
                Vec3 impulse = delta
                        .normalize()
                        .scale(force)
                        .add(0, force * 0.4, 0);

                entity.setDeltaMovement(
                        entity.getDeltaMovement()
                                .add(impulse));

            }
        });
    }
}
