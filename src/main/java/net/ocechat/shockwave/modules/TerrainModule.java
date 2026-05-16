package net.ocechat.shockwave.modules;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ocechat.shockwave.ShockwaveMod;
import net.ocechat.shockwave.utils.BlockCluster;
import net.ocechat.shockwave.utils.PhysicalBehavior;

import java.util.ArrayList;
import java.util.List;

public class TerrainModule {

    public static void reshape(Level level, Vec3 center, float radius) {

        if (level.isClientSide()) return;
        if (!(level instanceof ServerLevel)) return;

        if (ShockwaveMod.DEBUG)
            ShockwaveMod.LOGGER.info("[Shockwave] (TerrainModule) Reshaping sphere at {} r = {} ", center, radius);

        int r = (int) Math.ceil(radius);
        BlockPos origin = BlockPos.containing(center);

        float radiusSq      = radius * radius;

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {

                    float distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq > radiusSq) continue;

                    BlockPos pos = origin.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);


                    // Destroy block — no drops, respects blast resistance
                    if (!state.isAir()) {
                        float blastResistance = state.getBlock().getExplosionResistance(state, level, pos, null);
                        if (blastResistance < 1200f) {
                            level.removeBlock(pos, false);
                        }
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
                                BaseFireBlock.getState(level, pos));


                    }
                }
            }
        }


    }

    public static void doThermalFlash() {






    }

    public static List<BlockCluster> defineCluster(Level level, Vec3 center, float radius) {

        List<BlockCluster> blockClusterList = new ArrayList<>();
        int r = (int) Math.ceil(radius);
        BlockPos origin = BlockPos.containing(center);
        Float radiusSq = radius*radius;


        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {

                    float distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq > radiusSq) continue;

                    BlockPos pos = origin.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    Float blastResistance = state.getBlock().getExplosionResistance(state, level, pos, null);

                    if (blastResistance.isNaN()|| blastResistance == 0) blastResistance = 0.001f;

                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    /////////////////////////////////////// Define the Fragility vector of the block ///////////////////////////////////////
                    float multiplierFragility = Math.max(1/blastResistance,0.001f);
                    float multiplierDistance = Math.max(1/(float) Math.sqrt(distSq), 0.001f);
                    double multiplierTotal = multiplierFragility * multiplierDistance;

                    /////////////////////////////// Define the Pitch and the Yaw of the block ///////////////////////////////
                    double Yaw = Math.atan2(dx, dy);
                    double Pitch = Math.atan2(dx, dz);

                    blockClusterList.add(
                            new BlockCluster(
                                    pos,
                                    origin,
                                    state,
                                    PhysicalBehavior.NULL,
                                    new Vec3(
                                            multiplierTotal*dx,
                                            multiplierTotal*dy,
                                            multiplierTotal*dz
                                    ),
                                    Yaw,
                                    Pitch
                            )
                    );

                }
            }
        }
        return blockClusterList;
    }




}