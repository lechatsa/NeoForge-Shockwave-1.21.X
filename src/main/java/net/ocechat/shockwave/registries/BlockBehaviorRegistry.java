package net.ocechat.shockwave.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.ocechat.shockwave.utility.PhysicalBehavior;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockBehaviorRegistry {

    // --- Block ID → behaviors (checked first) ---
    private static final Map<ResourceLocation, EnumSet<PhysicalBehavior>> BLOCK_MAP = new HashMap<>();

    // --- Tag → behaviors (checked if no ID match) ---
    // LinkedHashMap preserves insertion order — more specific tags should be added first
    private static final Map<TagKey<Block>, EnumSet<PhysicalBehavior>> TAG_MAP = new LinkedHashMap<>();

    static {

        // ---- Tags ----

        // All logs and planks — smoky and flammable
        registerTag(BlockTags.LOGS_THAT_BURN,
                PhysicalBehavior.FLAMMABLE,
                PhysicalBehavior.SMOKY);

        registerTag(BlockTags.PLANKS,
                PhysicalBehavior.FLAMMABLE,
                PhysicalBehavior.SMOKY);

        registerTag(BlockTags.LEAVES,
                PhysicalBehavior.FLAMMABLE,
                PhysicalBehavior.SMOKY);

        registerTag(BlockTags.WOOL,
                PhysicalBehavior.FLAMMABLE,
                PhysicalBehavior.SMOKY);

        // Loose blocks — launchable by shockwave
        registerTag(BlockTags.SAND,
                PhysicalBehavior.LAUNCHABLE);

        registerTag(BlockTags.DIRT,
                PhysicalBehavior.LAUNCHABLE);

        // Glass — shatters
        registerTag(BlockTags.ICE,
                PhysicalBehavior.SHATTERABLE,
                PhysicalBehavior.EVAPORABLE);

        registerTag(BlockTags.SNOW,
                PhysicalBehavior.EVAPORABLE);

        registerTag(BlockTags.IRON_ORES,
                PhysicalBehavior.MELTABLE);

        // ---- Specific block IDs (override tags) ----

        // Gravel — launchable
        registerBlock("minecraft:gravel",
                PhysicalBehavior.LAUNCHABLE);

        // Ice — shatters, not flammable
        registerBlock("minecraft:ice",
                PhysicalBehavior.SHATTERABLE);

        registerBlock("minecraft:packed_ice",
                PhysicalBehavior.SHATTERABLE);

        // Bookshelf — flammable and smoky
        registerBlock("minecraft:bookshelf",
                PhysicalBehavior.FLAMMABLE,
                PhysicalBehavior.SMOKY);

        // Hay bale — very flammable
        registerBlock("minecraft:hay_block",
                PhysicalBehavior.FLAMMABLE,
                PhysicalBehavior.SMOKY);

        // TNT — launchable (caught in shockwave before detonating)
        registerBlock("minecraft:tnt",
                PhysicalBehavior.EXPLOSIVE);

        registerBlock("minecraft:coal_block",
                PhysicalBehavior.TOXIC);
    }

    // --- Registration helpers ---

    private static void registerBlock(String id, PhysicalBehavior... behaviors) {
        BLOCK_MAP.put(
                ResourceLocation.parse(id),
                EnumSet.copyOf(java.util.Arrays.asList(behaviors))
        );
    }

    private static void registerTag(TagKey<Block> tag, PhysicalBehavior... behaviors) {
        TAG_MAP.put(
                tag,
                EnumSet.copyOf(java.util.Arrays.asList(behaviors))
        );
    }

    // --- Lookup ---

    /**
     * Returns the set of behaviors for a given BlockState.
     * Block ID takes priority over tags.
     * Returns EnumSet containing only NULL if no match found.
     */
    public static EnumSet<PhysicalBehavior> getBehaviors(BlockState state) {

        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());

        // 1. Check specific block ID first
        if (BLOCK_MAP.containsKey(id)) {
            return BLOCK_MAP.get(id);
        }

        // 2. Check tags in insertion order — first match wins
        for (Map.Entry<TagKey<Block>, EnumSet<PhysicalBehavior>> entry : TAG_MAP.entrySet()) {
            if (state.is(entry.getKey())) {
                return entry.getValue();
            }
        }

        // 3. No match — null behavior
        return EnumSet.of(PhysicalBehavior.NULL);
    }

    /**
     * Convenience method — check if a block has a specific behavior.
     */
    public static boolean has(BlockState state, PhysicalBehavior behavior) {
        return getBehaviors(state).contains(behavior);
    }
}