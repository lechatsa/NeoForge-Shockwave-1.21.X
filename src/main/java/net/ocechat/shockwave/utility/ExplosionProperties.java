package net.ocechat.shockwave.utility;

import net.minecraft.core.BlockPos;
import net.ocechat.shockwave.utility.clusters.BlockCluster;

public record ExplosionProperties(BlockPos center, float radius, BlockCluster clusterList, float maxDamage, float minDamage, float power, int numberTNT, ExplosionFormula formula) {

}
