package net.ocechat.shockwave.registries;

import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.ocechat.shockwave.cosmetics.visuals.ShockwaveParticles;
import net.ocechat.shockwave.cosmetics.visuals.client.particle.*;

public class ShockwaveParticleRegistry {

    public static void register(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ShockwaveParticles.FIREBALL.get(),        FireballParticle.Provider::new);
        event.registerSpriteSet(ShockwaveParticles.SHOCKWAVE_RING.get(),  ShockwaveRingParticle.Provider::new);
        event.registerSpriteSet(ShockwaveParticles.EXPLOSION_SMOKE.get(), ExplosionSmokeParticle.Provider::new);
        event.registerSpriteSet(ShockwaveParticles.DEBRIS.get(),          DebrisParticle.Provider::new);
        event.registerSpriteSet(ShockwaveParticles.FLASH.get(),           FlashParticle.Provider::new);
        event.registerSpriteSet(ShockwaveParticles.CONDENSATION_SPHERE.get(), CondensationSphereParticle.Provider::new);
    }
}