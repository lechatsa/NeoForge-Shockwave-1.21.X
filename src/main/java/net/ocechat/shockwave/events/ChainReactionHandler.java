package net.ocechat.shockwave.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.ocechat.shockwave.ShockwaveMod;
import net.ocechat.shockwave.modules.*;

import java.util.*;

@EventBusSubscriber(modid = "shockwave")
public class ChainReactionHandler {

    private static final float BASE_RADIUS = 4.0f;

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {

        if (!(event.getEntity() instanceof PrimedTnt tnt)) return;

        Level level = event.getLevel();
        if (level.isClientSide()) return;

        // Cancel the primed TNT entity immediately — no fuse wait
        event.setCanceled(true);

        BlockPos origin = tnt.blockPosition();

        if (ShockwaveMod.DEBUG)
            ShockwaveMod.LOGGER.info("[Shockwave] (ChainReaction) PrimedTnt intercepted at {}", origin);

        // --- BFS: collect all adjacent TNT blocks ---
        Set<BlockPos> visited   = new HashSet<>();
        Queue<BlockPos> queue   = new LinkedList<>();
        List<BlockPos> tntFound = new ArrayList<>();

        queue.add(origin);
        visited.add(origin);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        BlockPos neighbor = current.offset(dx, dy, dz);
                        if (visited.contains(neighbor)) continue;
                        visited.add(neighbor);

                        if (level.getBlockState(neighbor).getBlock() instanceof TntBlock) {
                            tntFound.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        int total = 1 + tntFound.size();

        if (ShockwaveMod.DEBUG)
            ShockwaveMod.LOGGER.info("[Shockwave] (ChainReaction) {} TNT total (chain of {})",
                    total, tntFound.size());

        // --- Remove all chained TNT blocks ---
        for (BlockPos pos : tntFound) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }

        // --- Compute weighted center ---
        double sumX = origin.getX() + 0.5;
        double sumY = origin.getY() + 0.5;
        double sumZ = origin.getZ() + 0.5;

        for (BlockPos pos : tntFound) {
            sumX += pos.getX() + 0.5;
            sumY += pos.getY() + 0.5;
            sumZ += pos.getZ() + 0.5;
        }

        Vec3 center = new Vec3(sumX / total, sumY / total, sumZ / total);

        // --- Logarithmic power scaling ---
        // 1 TNT → radius ×1.0 | 10 TNT → ×3.3 | 100 TNT → ×5.6
        float consolidatedRadius = BASE_RADIUS * (1.0f + (float) Math.log(total));

        if (ShockwaveMod.DEBUG)
            ShockwaveMod.LOGGER.info("[Shockwave] (ChainReaction) Center={} Radius={} (scale={}x)",
                    center, consolidatedRadius, 1.0f + (float) Math.log(total));

        // --- Trigger the full explosion pipeline ---
        ShockwaveModule.apply(level, center, consolidatedRadius, total);
        SoundModule.play(level, center, consolidatedRadius);
        ParticleModule.spawn(level, center, consolidatedRadius);
        TerrainModule.reshape(level, center, consolidatedRadius);


        if (level instanceof ServerLevel serverLevel) {
            CondensationSphereHandler.spawn(serverLevel, center, tntFound.size());
        }
    }
}