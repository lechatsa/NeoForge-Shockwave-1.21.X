package net.ocechat.shockwave;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.ocechat.shockwave.events.ExplosionEventHandler;
import net.ocechat.shockwave.modules.ShockwaveParticles;
import org.slf4j.Logger;

@Mod(ShockwaveMod.MOD_ID)
public class ShockwaveMod {

    public static final String MOD_ID = "shockwave";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ShockwaveMod(IEventBus modEventBus) {
        // Enregistrement des sons custom
        ShockwaveSounds.SOUNDS.register(modEventBus);

        // Enregistrement des particules custom
        ShockwaveParticles.PARTICLES.register(modEventBus);

        // Enregistrement du handler d'événements sur le bus FORGE
        NeoForge.EVENT_BUS.register(ExplosionEventHandler.class);

        LOGGER.info( "Shockwave mod chargé !" );
    }
}