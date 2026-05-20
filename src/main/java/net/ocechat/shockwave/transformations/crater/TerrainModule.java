package net.ocechat.shockwave.transformations.crater;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ocechat.shockwave.ShockwaveMod;
import net.ocechat.shockwave.utility.*;
import net.ocechat.shockwave.utility.clusters.BlockCluster;

import java.util.EnumSet;
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


    public static void doThermalFlash(Level level, BlockPos center, List<BlockCluster> clusterList, ExplosionProperties explosionProperties) {

        List<BlockCluster> visibleBlockCluster = BlockCluster.shadowRemovalProcess(center, clusterList);

        for (BlockCluster cluster : visibleBlockCluster) {

            BlockState state = cluster.blockState();
            BlockPos pos = cluster.blockPos();
            EnumSet<PhysicalBehavior> behaviors = cluster.physicalBehavior();

            double Yaw = cluster.Yaw();
            double Pitch = cluster.Pitch();

            if (behaviors.contains(PhysicalBehavior.FLAMMABLE)) {

                float absoluteValueFlammability = state.getFlammability(level, pos, Direction.UP);


                if (state.getFlammability(level, pos, Direction.UP) >= 60) {
                    level.removeBlock(pos, false);
                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX(), pos.getY(), pos.getZ(), 0,5,0);
                }



                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 2);
            }


        }
    }

    public static void doCrater(Level level, BlockPos center, List<BlockCluster> clusterList) {

    }



    public static void doTerrainEffects(Level level, ExplosionProperties explosionProperties) {

        List<BlockCluster> clusterList = explosionProperties.clusterList();
        BlockPos center = explosionProperties.center();
        ExplosionFormula formula = explosionProperties.formula();

        float radius = explosionProperties.radius();
        float maxDamage = explosionProperties.maxDamage();
        float minDamage = explosionProperties.minDamage();
        float power = explosionProperties.power();
        int numberTNT = explosionProperties.numberTNT();





        doCrater(level, center, clusterList);
        doThermalFlash(level, center, clusterList, explosionProperties);
    }


}