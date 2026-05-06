package net.ocechat.shockwave.modules;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ocechat.shockwave.ShockwaveMod;

import java.util.ArrayList;
import java.util.List;

public class TerrainModule {

    public static void reshape(Level level, Vec3 center, float radius) {

        if (level.isClientSide()) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (ShockwaveMod.DEBUG)
            ShockwaveMod.LOGGER.info("[Shockwave] (TerrainModule) Reshaping sphere at {} r = {} ", center, radius);

        int r = (int) Math.ceil(radius);
        BlockPos origin = BlockPos.containing(center);

        float radiusSq      = radius * radius;
        float innerRadiusSq = (radius - 1) * (radius - 1);

        // Collect fire positions BEFORE destroying anything
        List<BlockPos> firePositions = new ArrayList<>();

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {

                    float distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq > radiusSq) continue;

                    BlockPos pos = origin.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);

                    // Collect fire candidates on the outer shell before destruction
                    if (distSq >= innerRadiusSq) {
                        BlockPos above = pos.above();
                        if (!state.isAir()
                                && level.getBlockState(above).isAir()
                                && level.random.nextFloat() < 0.25f) {
                            firePositions.add(above);
                        }
                    }

                    // Destroy block — no drops
                    if (!state.isAir()) {
                        level.removeBlock(pos, false);
                    }
                }
            }
        }

        // Place fire after destruction — mirror vanilla fire logic
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {

                    if (dx * dx + dy * dy + dz * dz > radiusSq) continue;

                    BlockPos pos = origin.offset(dx, dy, dz);

                    if (level.random.nextInt(3) == 0
                            && level.getBlockState(pos).isAir()
                            && level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {

                        level.setBlockAndUpdate(pos,
                                net.minecraft.world.level.block.BaseFireBlock.getState(level, pos));

                        // if (ShockwaveMod.DEBUG) ShockwaveMod.LOGGER.info("[Shockwave] (TerrainModule) Fire placed at {}", pos);
                    }
                }
            }
        }

        // Entity damage proportional to distance
        List<Entity> entities = level.getEntitiesOfClass(
                Entity.class,
                AABB.ofSize(center, radius * 2, radius * 2, radius * 2)
        );

        for (Entity entity : entities) {
            double dist = entity.position().distanceTo(center);
            if (dist > radius) continue;

            float damageFactor = (float) (1.0 - dist / radius);
            float damage = damageFactor * radius * 2.0f;

            if (entity instanceof LivingEntity living) {
                living.hurt(
                        serverLevel.damageSources().explosion(null, null),
                        damage
                );

                if (ShockwaveMod.DEBUG)
                    ShockwaveMod.LOGGER.info("[Shockwave] (TerrainModule) Dealt {} dmg to {} (dist = {})",
                            damage, entity.getName().getString(), dist);
            }
        }
    }
}