package net.ocechat.shockwave;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.ocechat.shockwave.events.ExplosionEventHandler;
import net.ocechat.shockwave.events.ChainReactionHandler;
import net.ocechat.shockwave.utils.ShockwaveParticleRegistry;
import net.ocechat.shockwave.utils.ShockwaveParticles;
import net.ocechat.shockwave.utils.ShockwaveSounds;
import org.slf4j.Logger;

@Mod(ShockwaveMod.MOD_ID)
public class ShockwaveMod {

    public static final Boolean DEBUG = Boolean.TRUE;
    public static final String MOD_ID = "shockwave";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ShockwaveMod(IEventBus modEventBus) {

        ShockwaveSounds.SOUNDS.register(modEventBus);
        ShockwaveParticles.PARTICLES.register(modEventBus);

        NeoForge.EVENT_BUS.register(ExplosionEventHandler.class);
        NeoForge.EVENT_BUS.register(ChainReactionHandler.class);

        // Register particle providers on the mod bus, client-side only
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ShockwaveParticleRegistry::register);
        }

        if (DEBUG) LOGGER.info("[Shockwave] Mod loaded.");
    }
}