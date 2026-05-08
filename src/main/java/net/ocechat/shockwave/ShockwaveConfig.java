package net.ocechat.shockwave;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ShockwaveConfig {

    public static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();

    public static final ModConfigSpec SPEC;

    /// --- Shockwave
    public static final ModConfigSpec.DoubleValue SHOCKWAVE_MULTIPLIER;
    public static final ModConfigSpec.IntValue ENERGIE_TNT;
    public static final ModConfigSpec.DoubleValue SOUND_SPEED;
    public static final ModConfigSpec.DoubleValue GAMMA;
    public static final ModConfigSpec.DoubleValue PHI;
    public static final ModConfigSpec.IntValue PRESSURE;

    /// --- Sound
    public static final ModConfigSpec.DoubleValue SOUND_RANGE_MULTIPLIER;

    /// --- Terrain
    public static final ModConfigSpec.BooleanValue ENABLE_FIRE_SPREAD;
    public static final ModConfigSpec.BooleanValue ENABLE_CRATER;
    public static final ModConfigSpec.DoubleValue BASE_VALUE_ZERO;
    public static final ModConfigSpec.DoubleValue BASE_VALUE_MAX;
    static {
        BUILDER.push("shockwave");

        SHOCKWAVE_MULTIPLIER = BUILDER
                .comment("Shockwave radius multiplier")
                .defineInRange("shockwaveMultiplier", 1.8, 0.5, 5.0);

        SOUND_RANGE_MULTIPLIER = BUILDER
                .comment("Sound distance multiplier")
                .defineInRange("soundRangeMultiplier", 2.0, 1.0, 8.0);

        ENABLE_FIRE_SPREAD = BUILDER
                .comment("Enable fire spreading around an explosion")
                .define("enableFireSpread", true);

        ENABLE_CRATER = BUILDER
                .comment("Enable the crater formation")
                .define("enableCrater", true);

        // Energy per TNT (J) — real TNT is ~4 184 000 J per kg
        ENERGIE_TNT = BUILDER
                .comment("Energy released by one TNT in Joules")
                .defineInRange("energyTNT", 4_184_000, 1_000, 100_000_000);

        // PHI — scaled distance threshold for lethal zone
        // Calibrated so that 1 TNT kills at d=0 and deals 2HP at d=5m
        PHI = BUILDER
                .comment("Scaled distance lethality threshold (Hopkinson-Cranz). "
                        + "Lower = larger lethal zone. Default calibrated for 5m kill radius per TNT.")
                .defineInRange("phi", 0.071, 0.001, 1.0);

        // Atmospheric pressure (Pa)
        PRESSURE = BUILDER
                .comment("Ambient atmospheric pressure in Pa")
                .defineInRange("ambientPressure", 101_325, 50_000, 200_000);

        // Speed of sound (m/s)
        SOUND_SPEED = BUILDER
                .comment("Speed of sound in m/s")
                .defineInRange("soundSpeed", 343.0, 100.0, 2000.0);

        // Gamma — heat capacity ratio of air
        GAMMA = BUILDER
                .comment("Heat capacity ratio of air (gamma)")
                .defineInRange("gamma", 1.4, 1.0, 2.0);

        BASE_VALUE_ZERO = BUILDER
                .comment("The distance at which one kilogram of TNT will no longer cause any damage")
                .defineInRange("baseValueZero", 50.0, 1.0, 200.0);

        BASE_VALUE_MAX  = BUILDER
                .comment("The maximum damage inflicted by one kilogram of TNT at a theoretical distance of 0")
                .defineInRange("baseValueMax", 50.0, 1.0, 200.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}