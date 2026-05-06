package net.ocechat.shockwave.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.ocechat.shockwave.ShockwaveMod;
import net.ocechat.shockwave.utils.ShockwaveParticles;
import net.ocechat.shockwave.client.particle.*;

@EventBusSubscriber(modid = ShockwaveMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ShockwaveParticleRegistry {

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ShockwaveParticles.FIREBALL.get(),       FireballParticle.Provider::new);
        event.registerSpriteSet(ShockwaveParticles.SHOCKWAVE_RING.get(), ShockwaveRingParticle.Provider::new);
        event.registerSpriteSet(ShockwaveParticles.EXPLOSION_SMOKE.get(),ExplosionSmokeParticle.Provider::new);
        event.registerSpriteSet(ShockwaveParticles.DEBRIS.get(),         DebrisParticle.Provider::new);
        event.registerSpriteSet(ShockwaveParticles.FLASH.get(),          FlashParticle.Provider::new);
    }
}