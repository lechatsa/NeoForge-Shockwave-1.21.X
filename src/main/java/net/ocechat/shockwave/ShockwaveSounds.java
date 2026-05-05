package net.ocechat.shockwave;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ocechat.shockwave.ShockwaveMod;

public class ShockwaveSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ShockwaveMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> EXPLOSION_LARGE =
            SOUNDS.register("explosion_large", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ShockwaveMod.MOD_ID, "explosion_large")
                    )
            );
}