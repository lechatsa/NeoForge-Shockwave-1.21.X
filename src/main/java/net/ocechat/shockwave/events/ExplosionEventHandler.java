package net.ocechat.shockwave.events;

import net.ocechat.shockwave.modules.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

// Pas de bus= ici : le bus par défaut est FORGE, celui des événements monde
@EventBusSubscriber(modid = "shockwave")
public class ExplosionEventHandler {

    @SubscribeEvent
    public static void onExplosionStart( ExplosionEvent.Start event ) {

        var explosion = event.getExplosion();
        var level     = event.getLevel();
        var pos       = explosion.center();
        float radius  = explosion.radius();

        ShockwaveModule.apply(level, pos, radius);
        SoundModule.play(level, pos, radius);

    }

    @SubscribeEvent
    public static void onExplosionDetonate( ExplosionEvent.Detonate event ) {

        var explosion = event.getExplosion();
        var level     = event.getLevel();
        var pos       = explosion.center();
        float radius  = explosion.radius();

        TerrainModule.reshape(level, pos, event.getAffectedBlocks());
        ParticleModule.spawn(level, pos, radius);

    }
}