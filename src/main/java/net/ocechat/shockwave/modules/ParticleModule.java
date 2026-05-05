package net.ocechat.shockwave.modules;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleModule {

    public static void spawn(
            Level level, Vec3 pos, float radius) {

        // Les particules serveur sont envoyées via ServerLevel
        if (!(level instanceof ServerLevel serverLevel))
            return;

        int count = (int) (radius * 12);

        // Colonne de fumée centrale
        serverLevel.sendParticles(
                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                pos.x, pos.y + 1, pos.z,
                count,
                radius * 0.3, 0.5, radius * 0.3, // spread
                0.05                                   // vitesse
        );

        // Débris explosifs radiaux
        serverLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                pos.x, pos.y, pos.z,
                (int) (radius * 4),
                radius * 0.5, radius * 0.2, radius * 0.5,
                0.2
        );

        // Flash lumineux instantané
        serverLevel.sendParticles(
                ParticleTypes.FLASH,
                pos.x, pos.y, pos.z,
                1, 0, 0, 0, 0
        );
    }
}