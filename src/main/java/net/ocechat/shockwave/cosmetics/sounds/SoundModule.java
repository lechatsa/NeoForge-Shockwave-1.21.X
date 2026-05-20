package net.ocechat.shockwave.cosmetics.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoundModule {

    /** Portée du son : rayon × multiplicateur */
    private static final float RANGE_MULT = 2.0f;

    public static void play(
            Level level, Vec3 pos, float radius) {

        // Côté serveur uniquement — le client reçoit via le réseau
        if (level.isClientSide()) return;

        float volume = Math.min(1.0f + radius * 0.1f, 4.0f);
        float range  = radius * RANGE_MULT;

        // Joue le SoundEvent custom avec portée étendue
        level.playSound(
                null,                           // null = diffusé à tous
                pos.x, pos.y, pos.z,
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.BLOCKS,
                volume,
                0.8f + level.random.nextFloat() * 0.4f // pitch aléatoire
        );
    }
}
