package net.ocechat.shockwave.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ocechat.shockwave.ShockwaveMod;
import oshi.util.tuples.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public record BlockCluster(BlockPos blockPos, BlockPos center, BlockState blockState, PhysicalBehavior physicalBehavior, Vec3 vec3, Double Alpha, Double Delta) {

    public static Vec3 sum(List<BlockCluster> blockClusterList) {

        Vec3 resulting = new Vec3(0,0,0);

        for (BlockCluster blockCluster : blockClusterList) {
            //if (ShockwaveMod.DEBUG) ShockwaveMod.LOGGER.info("[Shockwave] (BlockCluster) ClusterList : {}", blockCluster.vec3);
            resulting = resulting.add(blockCluster.vec3); //
        }

        if (ShockwaveMod.DEBUG) ShockwaveMod.LOGGER.info("[Shockwave] (BlockCluster) Cluster sum resulting : x : {}, y : {}, z : {}", resulting.x, resulting.z, resulting.y);

        return resulting;
    }

    /**
    Return the Pitch and Yaw of the sum of all vector
     **/

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

    // On utilise une petite fonction utilitaire pour la distance horizontale
    private static double getHorizontalDist(double dx, double dz) {
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static Double calculateGamma(Double alpha, int dx, int dy, int dz) {
        double hDist = getHorizontalDist(dx, dz);
        // On veut le coin supérieur du bloc (dy + 0.5)
        // et le point le plus proche horizontalement (hDist - 0.5)
        return Math.toDegrees(Math.atan2(dy + 0.5, hDist - 0.5));
    }

    public static Double calculateBeta(Double alpha, int dx, int dy, int dz) {
        double hDist = getHorizontalDist(dx, dz);
        // On veut le coin inférieur du bloc (dy - 0.5)
        // et le point le plus loin horizontalement (hDist + 0.5)
        return Math.toDegrees(Math.atan2(dy - 0.5, hDist + 0.5));
    }

    public static Double calculatePhi(Double delta, int dx, int dz) {
        // Pour l'angle horizontal (Yaw), on utilise dz et dx
        // On cherche l'angle le plus "à gauche" du bloc
        return Math.toDegrees(Math.atan2(dz + 0.5, dx - 0.5));
    }

    public static Double calculateOmega(Double delta, int dx, int dz) {
        // On cherche l'angle le plus "à droite" du bloc
        return Math.toDegrees(Math.atan2(dz - 0.5, dx + 0.5));
    }



    public static List<BlockCluster> processShadow(BlockPos center, List<BlockCluster> clusterList) {
        // 1. TRI : On trie les clusters du plus proche au plus loin du centre.
        // C'est indispensable pour que les premiers "bloqueurs" soient les plus proches.
        clusterList.sort(Comparator.comparingDouble(c -> c.blockPos.distSqr(center)));

        // 2. Liste qui contiendra uniquement les blocs visibles (non cachés)
        List<BlockCluster> visibleClusters = new ArrayList<>();

        for (BlockCluster current : clusterList) {
            boolean hidden = false;

            // On récupère les coordonnées relatives du cluster actuel pour les calculs
            double dx = current.blockPos.getX() - center.getX();
            double dy = current.blockPos.getY() - center.getY();
            double dz = current.blockPos.getZ() - center.getZ();

            // 3. VÉRIFICATION : Est-ce que ce cluster est dans l'ombre d'un bloc déjà validé ?
            for (BlockCluster blocker : visibleClusters) {

                // On calcule les limites de l'ombre projetée par le "blocker"
                // On passe les coordonnées relatives du BLOQUEUR pour savoir quelle surface il occupe
                double bDx = blocker.blockPos.getX() - center.getX();
                double bDy = blocker.blockPos.getY() - center.getY();
                double bDz = blocker.blockPos.getZ() - center.getZ();

                // Tes formules pour définir le cône d'ombre du bloqueur
                double gamma = calculateGamma(blocker.Alpha, bDx, bDy, bDz);
                double beta  = calculateBeta(blocker.Alpha, bDx, bDy, bDz);
                double phi   = calculatePhi(blocker.Delta, bDx, bDz);
                double omega = calculateOmega(blocker.Delta, bDx, bDz);

                // Si les angles du cluster actuel tombent dans le "rectangle" d'ombre du bloqueur
                if (current.Alpha >= beta && current.Alpha <= gamma &&
                        current.Delta >= omega && current.Delta <= phi) {
                    hidden = true;
                    break; // Inutile de vérifier les autres bloqueurs, celui-ci suffit
                }
            }

            // 4. ADMISSION : Si aucun bloqueur ne l'a caché, on l'ajoute à la liste visible
            if (!hidden) {
                visibleClusters.add(current);
            }
        }

        return visibleClusters;
    }
}
