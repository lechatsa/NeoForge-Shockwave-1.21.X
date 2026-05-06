package net.ocechat.shockwave.events;

import net.ocechat.shockwave.modules.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

@EventBusSubscriber(modid = "shockwave")
public class ExplosionEventHandler {

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Start event) {

        // Cancel the vanilla explosion entirely —
        // blocks, entity damage, and portal side-effects included
        event.setCanceled(true);

        var level  = event.getLevel();
        var pos    = event.getExplosion().center();
        float radius = event.getExplosion().radius();



        // Our replacement pipeline
        ShockwaveModule.apply(level, pos, radius);
        SoundModule.play(level, pos, radius);
        TerrainModule.reshape(level, pos, radius);
        ParticleModule.spawn(level, pos, radius);
    }

    // onExplosionDetonate is no longer needed:
    // canceling Start prevents Detonate from firing at all
}