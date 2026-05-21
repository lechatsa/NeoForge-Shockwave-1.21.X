package net.ocechat.shockwave.utility.clusters;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ocechat.shockwave.ShockwaveMod;
import com.mojang.datafixers.util.Pair;
import net.ocechat.shockwave.registries.BlockBehaviorRegistry;
import net.ocechat.shockwave.utility.PhysicalBehavior;

import java.util.*;


public record BlockData(BlockPos blockPos, BlockState blockState, EnumSet<PhysicalBehavior> physicalBehavior, Vec3 fragilityVec3, Double Yaw, Double Pitch) {

    ///////////////////////////////////////////// Return the Sum of all vector /////////////////////////////////////////////
    public static Vec3 getResultingVector(BlockCluster blockCluster) {

        Vec3 resulting = new Vec3(0,0,0);

        for (BlockData blockData : blockCluster.asList()) resulting = resulting.add(blockData.fragilityVec3);

        if (ShockwaveMod.DEBUG) ShockwaveMod.LOGGER.info("[Shockwave] (BlockData) Cluster sum resulting : x : {}, y : {}, z : {}", resulting.x, resulting.z, resulting.y);

        return resulting;
    }


    /////////////////////////////////// Return the Pitch and Yaw of the sum of all vector ///////////////////////////////////
    public static Pair<Double, Double> getAnglesVector(Vec3 resulting) {

        double dx = resulting.x;
        double dz = resulting.z;
        double dy = resulting.y;

        double horizontalDistance = getHorizontalDistance(dx, dz);

        double yaw     = Math.atan2(dz, dx);
        double pitch = Math.atan2(dy, horizontalDistance);

        return new Pair<>(yaw, pitch);
    }


    ////////////////////// Return the Horizontal distance of a 3D triangle define by its Yaw and Pitch //////////////////////
    public static double getHorizontalDistance(double dx, double dz) {
        return Math.sqrt(dx * dx + dz * dz);
    }


    ///////////////////////////// Return the Maximal Yaw ( in a trigonometric sense, in Radian) /////////////////////////////
    public static Double calculateMaximalYaw(Double Yaw, int dx, int dy) {

        double MaximalYaw = 0.0;

        double dYaw = Math.toDegrees(Yaw);

        if (0 <= dYaw && dYaw < 90) MaximalYaw = Math.atan2(dy + 0.5, dx - 0.5);
        if (90 <= dYaw && dYaw < 180) MaximalYaw = Math.atan2(dy - 0.5, dx - 0.5);
        if (180 <= dYaw && dYaw < 270) MaximalYaw = Math.atan2(dy - 0.5, dx + 0.5);
        if (270 <= dYaw && dYaw < 360) MaximalYaw = Math.atan2(dy + 0.5, dx + 0.5);

        return MaximalYaw;
    }


    ///////////////////////////// Return the Minimal Yaw ( in a trigonometric sense, in Radian) /////////////////////////////
    public static Double calculateMinimalYaw(Double Yaw, int dx, int dy) {

        double MinimalYaw = 0.0;

        double dYaw = Math.toDegrees(Yaw);

        if (0 < dYaw && dYaw <= 90) MinimalYaw = Math.atan2(dy - 0.5, dx + 0.5);
        if (90 < dYaw && dYaw <= 180) MinimalYaw = Math.atan2(dy + 0.5, dx + 0.5);
        if (180 < dYaw && dYaw <= 270) MinimalYaw = Math.atan2(dy + 0.5, dx - 0.5);
        if (270 < dYaw && dYaw <= 360) MinimalYaw = Math.atan2(dy - 0.5, dx - 0.5);

        return MinimalYaw;
    }


    //////////////////////////// Return the Maximal Pitch ( in a trigonometric sense, in Radian) ////////////////////////////
    public static Double calculateMaximalPitch(Double Pitch, int dx, int dz) {

        double MaximalPitch = 0.0;

        double dPitch = Math.toDegrees(Pitch);

        if (0 <= dPitch && dPitch < 90) MaximalPitch = Math.atan2(dz + 0.5, dx - 0.5);
        if (90 <= dPitch && dPitch < 180) MaximalPitch = Math.atan2(dz - 0.5, dx - 0.5);
        if (180 <= dPitch && dPitch < 270) MaximalPitch = Math.atan2(dz - 0.5, dx + 0.5);
        if (270 <= dPitch && dPitch < 360) MaximalPitch = Math.atan2(dz + 0.5, dx + 0.5);

        return MaximalPitch;
    }


    //////////////////////////// Return the Minimal Pitch ( in a trigonometric sense, in Radian) ////////////////////////////
    public static Double calculateMinimalPitch(Double Pitch, int dx, int dz) {

        double MinimalPitch = 0.0;

        double dPitch = Math.toDegrees(Pitch);

        if (0 < dPitch && dPitch <= 90) MinimalPitch = Math.atan2(dz - 0.5, dx + 0.5);
        if (90 < dPitch && dPitch <= 180) MinimalPitch = Math.atan2(dz + 0.5, dx + 0.5);
        if (180 < dPitch && dPitch <= 270) MinimalPitch = Math.atan2(dz + 0.5, dx - 0.5);
        if (270 < dPitch && dPitch <= 360) MinimalPitch = Math.atan2(dz - 0.5, dx - 0.5);

        return MinimalPitch;
    }


    /// Return a new list of BlockCluster without the BlockCluster hide by other Blocks creating an effective List of block not in the shadow of an emissif source
    public static BlockCluster shadowRemovalProcess(BlockPos center, BlockCluster blockCluster) {

        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();


        /// Sorte the List by distance
        BlockCluster clusterSorted = new BlockCluster(
                blockCluster.asList().stream()
                        .sorted(Comparator.comparingDouble(c -> c.blockPos.distSqr(center)))
                        .toList(), center.getCenter());


        ///  for each block in range, I find its square-based pyramid
        int size = clusterSorted.size();
        boolean[] isShadowed = new boolean[size];

        for (int i = 0; i < size; i++) {

            if (isShadowed[i]) continue;

            BlockData caster = clusterSorted.get(i);

            double Yaw = caster.Yaw;
            double Pitch = caster.Pitch;

            int dx = caster.blockPos.getX() - x;
            int dy = caster.blockPos.getY() - y;
            int dz = caster.blockPos.getZ() - z;

            /// Find the four angles defining the square-based pyramid
            double MaximalYaw = calculateMaximalYaw(Yaw, dx, dy);
            double MinimalYaw = calculateMinimalYaw(Yaw, dx, dy);

            double MaximalPitch = calculateMaximalPitch(Pitch, dx, dz);
            double MinimalPitch = calculateMinimalPitch(Pitch, dx, dz);

            /// For each block in range except the one I'm currently working on, I check if it's within the square-based pyramidal shadow, If it is then removed it
            for (int j = i + 1; j < size; j++){

                ///  If the block is in shadow don't compute anything
                if (isShadowed[j]) continue;

                BlockData candidate = clusterSorted.get(j);

                double candidateYaw = candidate.Yaw;
                double candidatePitch = candidate.Pitch;

                /// Check if the block is in the caster's shadow, If yes then set it to isShadowed = true so it can be ignored later
                if (!( candidateYaw < MinimalYaw || MaximalYaw < candidateYaw || candidatePitch < MinimalPitch || MaximalPitch < candidatePitch )) {
                    isShadowed[j] = true;
                }
            }
        }

        BlockCluster visibleClusters = new BlockCluster(new ArrayList<>(), center.getCenter());
        for (int i = 0; i < size; i++) {
            if (!isShadowed[i]) visibleClusters.asList().add(clusterSorted.get(i));
        }

        return visibleClusters;
    }

}
