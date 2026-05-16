package net.ocechat.shockwave.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ocechat.shockwave.ShockwaveMod;
import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public record BlockCluster(BlockPos blockPos, BlockPos center, BlockState blockState, PhysicalBehavior physicalBehavior, Vec3 fragilityVec3, Double Yaw, Double Pitch) {

    ///////////////////////////////////////////// Return the Sum of all vector /////////////////////////////////////////////
    public static Vec3 sum(List<BlockCluster> blockClusterList) {

        Vec3 resulting = new Vec3(0,0,0);

        for (BlockCluster blockCluster : blockClusterList) resulting = resulting.add(blockCluster.fragilityVec3);

        if (ShockwaveMod.DEBUG) ShockwaveMod.LOGGER.info("[Shockwave] (BlockCluster) Cluster sum resulting : x : {}, y : {}, z : {}", resulting.x, resulting.z, resulting.y);

        return resulting;
    }


    /////////////////////////////////// Return the Pitch and Yaw of the sum of all vector ///////////////////////////////////
    public static Pair<Double, Double> getAngles(List<BlockCluster> clusterList) {

        Vec3 resulting = BlockCluster.sum(clusterList);

        double dx = resulting.x;
        double dz = resulting.z;
        double dy = resulting.y;

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        double yaw     = Math.atan2(dz, dx);
        double pitch = Math.atan2(dy, horizontalDistance);

        return new Pair<>(yaw, pitch);
    }


    ////////////////////// Return the Horizontal distance of a 3D triangle define by its Yaw and Pitch //////////////////////
    private static double getHorizontalDistance(double dx, double dz) {
        return Math.sqrt(dx * dx + dz * dz);
    }


    ///////////////////////////// Return the Maximal Yaw ( in a trigonometric sense, in Radian) /////////////////////////////
    public static Double calculateMaximalYaw(Double Yaw, int dx, int dy, int dz) {

        double MaximalYaw = 0.0;

        if (0 <= Yaw && Yaw < 90) MaximalYaw = Math.atan2(dy + 0.5, dx - 0.5);
        if (90 <= Yaw && Yaw < 180) MaximalYaw = Math.atan2(dy - 0.5, dx - 0.5);
        if (180 <= Yaw && Yaw < 270) MaximalYaw = Math.atan2(dy - 0.5, dx + 0.5);
        if (270 <= Yaw && Yaw < 360) MaximalYaw = Math.atan2(dy + 0.5, dx + 0.5);

        return MaximalYaw;
    }


    ///////////////////////////// Return the Minimal Yaw ( in a trigonometric sense, in Radian) /////////////////////////////
    public static Double calculateMinimalYaw(Double Yaw, int dx, int dy, int dz) {

        double MinimalYaw = 0.0;

        if (0 < Yaw && Yaw <= 90) MinimalYaw = Math.atan2(dy - 0.5, dx + 0.5);
        if (90 < Yaw && Yaw <= 180) MinimalYaw = Math.atan2(dy + 0.5, dx + 0.5);
        if (180 < Yaw && Yaw <= 270) MinimalYaw = Math.atan2(dy + 0.5, dx - 0.5);
        if (270 < Yaw && Yaw <= 360) MinimalYaw = Math.atan2(dy - 0.5, dx - 0.5);

        return MinimalYaw;
    }


    //////////////////////////// Return the Maximal Pitch ( in a trigonometric sense, in Radian) ////////////////////////////
    public static Double calculateMaximalPitch(Double Pitch, int dx, int dz) {

        double MaximalPitch = 0.0;

        if (0 <= Pitch && Pitch < 90) MaximalPitch = Math.atan2(dz + 0.5, dx - 0.5);
        if (90 <= Pitch && Pitch < 180) MaximalPitch = Math.atan2(dz - 0.5, dx - 0.5);
        if (180 <= Pitch && Pitch < 270) MaximalPitch = Math.atan2(dz - 0.5, dx + 0.5);
        if (270 <= Pitch && Pitch < 360) MaximalPitch = Math.atan2(dz + 0.5, dx + 0.5);

        return MaximalPitch;
    }


    //////////////////////////// Return the Minimal Pitch ( in a trigonometric sense, in Radian) ////////////////////////////
    public static Double calculateMinimalPitch(Double Pitch, int dx, int dz) {

        double MinimalPitch = 0.0;

        if (0 < Pitch && Pitch <= 90) MinimalPitch = Math.atan2(dz - 0.5, dx + 0.5);
        if (90 < Pitch && Pitch <= 180) MinimalPitch = Math.atan2(dz + 0.5, dx + 0.5);
        if (180 < Pitch && Pitch <= 270) MinimalPitch = Math.atan2(dz + 0.5, dx - 0.5);
        if (270 < Pitch && Pitch <= 360) MinimalPitch = Math.atan2(dz - 0.5, dx - 0.5);

        return MinimalPitch;
    }


    /// Return a new list of BlockCluster without the BlockCluster hide by other Blocks creating an effective List of block not in the shadow of an emissif source
    public static List<BlockCluster> shadowRemovalProcess(BlockPos center, List<BlockCluster> clusterList) {

        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();

        /// Sorte the List by distance
        List<BlockCluster> clusterListSorted = new ArrayList<>(clusterList.stream()
                .sorted(Comparator.comparingDouble(c -> c.blockPos.distSqr(center)))
                .toList());

        List<BlockCluster> toRemove = new ArrayList<>();

        ///  for each block in range, I find its square-based pyramid
        Iterator<BlockCluster> iterator = clusterListSorted.iterator();
        while (iterator.hasNext()) {

            BlockCluster caster = iterator.next();

            double Yaw = caster.Yaw;
            double Pitch = caster.Pitch;

            int dx = caster.blockPos.getX() - x;
            int dy = caster.blockPos.getY() - y;
            int dz = caster.blockPos.getZ() - z;

            /// Find the four angles defining the square-based pyramid
            double MaximalYaw = calculateMaximalYaw(Yaw, dx, dy, dz);
            double MinimalYaw = calculateMinimalYaw(Yaw, dx, dy, dz);

            double MaximalPitch = calculateMaximalPitch(Pitch, dx, dz);
            double MinimalPitch = calculateMinimalPitch(Pitch, dx, dz);

            /// For each block in range except the one I'm currently working on, I check if it's within the square-based pyramidal shadow, If it is then removed it
            for (BlockCluster candidate : clusterListSorted){
                if (candidate.equals(caster)) continue;

                double candidateYaw = candidate.Yaw;
                double candidatePitch = candidate.Pitch;

                if (!( candidateYaw < MinimalYaw || MaximalYaw < candidateYaw || candidatePitch < MinimalPitch || MaximalPitch < candidatePitch )) {
                    toRemove.add(candidate);
                    iterator.remove();
                }

            }

            clusterListSorted.removeAll(toRemove);

        }

        return clusterListSorted;
    }


}
//clusterListReduced = clusterListSortedDistance.removeIf(blocker -> {
//
//double blockerPitch = blocker.Pitch;
//double blockerYaw = blocker.Yaw;
//
//                return (blockerYaw < MinimalYaw || MaximalYaw < blockerYaw || blockerPitch < MinimalPitch || MaximalPitch < blockerPitch);
//        });