package net.ocechat.shockwave;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ShockwaveConfig {

    public static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();

    public static final ModConfigSpec SPEC;

    // Beta
    public static final ModConfigSpec.BooleanValue LOG;

    // Onde de choc
    public static final ModConfigSpec.DoubleValue
            SHOCKWAVE_MULTIPLIER;

    // Son
    public static final ModConfigSpec.DoubleValue
            SOUND_RANGE_MULTIPLIER;

    // Terrain
    public static final ModConfigSpec.BooleanValue
            ENABLE_FIRE_SPREAD;
    public static final ModConfigSpec.BooleanValue
            ENABLE_CRATER;

    static {
        BUILDER.push("shockwave");

        SHOCKWAVE_MULTIPLIER = BUILDER
                .comment("Multiplicateur rayon onde de choc")
                .defineInRange("shockwaveMultiplier",
                        1.8, 0.5, 5.0);

        SOUND_RANGE_MULTIPLIER = BUILDER
                .comment("Multiplicateur portée son")
                .defineInRange("soundRangeMultiplier",
                        2.0, 1.0, 8.0);

        ENABLE_FIRE_SPREAD = BUILDER
                .comment("Active les brûlures au sol")
                .define("enableFireSpread", true);

        ENABLE_CRATER = BUILDER
                .comment("Active la formation de cratère")
                .define("enableCrater", true);

        LOG = BUILDER
                .comment("LOG On Mode")
                .define("enableLOG", true);


        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}