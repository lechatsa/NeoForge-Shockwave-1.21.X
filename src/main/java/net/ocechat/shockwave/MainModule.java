package net.ocechat.shockwave;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ocechat.shockwave.cosmetics.sounds.SoundModule;
import net.ocechat.shockwave.cosmetics.visuals.ParticleModule;
import net.ocechat.shockwave.events.CondensationSphereHandler;
import net.ocechat.shockwave.transformations.crater.TerrainModule;
import net.ocechat.shockwave.transformations.shockwave.ShockwaveModule;

public class MainModule {

    public static void applyExplosion(Level level, Vec3 center, float consolidatedRadius, int total) {

        ShockwaveModule.apply(level, center, consolidatedRadius, total);
        SoundModule.play(level, center, consolidatedRadius);
        ParticleModule.spawn(level, center, consolidatedRadius);
        TerrainModule.reshape(level, center, consolidatedRadius);

        if (level instanceof ServerLevel serverLevel) {
            CondensationSphereHandler.spawn(serverLevel, center, total);
        }


    }




}
