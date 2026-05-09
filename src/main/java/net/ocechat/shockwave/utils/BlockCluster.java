package net.ocechat.shockwave.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public record BlockCluster(BlockPos blockPos, BlockPos center, BlockState blockState, PhysicalBehavior physicalBehavior, Vec3 vec3) {

        public static Vec3 sum(List<BlockCluster> blockClusterList) {

            Vec3 resulting = new Vec3(0,0,0);

            for (BlockCluster blockCluster : blockClusterList) {
                resulting.add(blockCluster.vec3);
            }
            return resulting;
        }


}
