package net.ocechat.shockwave;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ocechat.shockwave.cosmetics.sounds.SoundModule;
import net.ocechat.shockwave.cosmetics.visuals.ParticleModule;
import net.ocechat.shockwave.events.CondensationSphereHandler;
import net.ocechat.shockwave.registries.BlockBehaviorRegistry;
import net.ocechat.shockwave.transformations.crater.TerrainModule;
import net.ocechat.shockwave.transformations.shockwave.ShockwaveModule;
import net.ocechat.shockwave.utility.PhysicalBehavior;
import net.ocechat.shockwave.utility.clusters.BlockCluster;
import net.ocechat.shockwave.utility.clusters.BlockData;
import net.ocechat.shockwave.utility.clusters.EntityCluster;

import java.util.EnumSet;
import java.util.List;


public class MainModule {

    public static void applyExplosion(Level level, Vec3 center, int numberTNT) {

        float power = calculatePower(numberTNT);

        EntityCluster entityCluster = EntityCluster.find(level, center, power);
        BlockCluster clusterList = defineCluster(level, center, power);

        ShockwaveModule.apply(level, center, power, numberTNT);
        SoundModule.play(level, center, power);
        ParticleModule.spawn(level, center, power);
        TerrainModule.reshape(level, center, power);

        if (level instanceof ServerLevel serverLevel) {
            CondensationSphereHandler.spawn(serverLevel, center, numberTNT);
        }


    }

    private static float calculatePower(int numberTNT) {

        double BASE_RADIUS = ShockwaveConfig.BASE_EXPLOSION_RADIUS.get();

        // --- Logarithmic power scaling ---
        // 1 TNT → radius ×1.0 | 10 TNT → ×3.3 | 100 TNT → ×5.6
        float power = (float) (BASE_RADIUS * (1.0 + Math.log(numberTNT)));

        return power;
    }

    public static BlockCluster defineCluster(Level level, Vec3 center, float radius) {

        BlockCluster blockDataList = BlockCluster.empty(center);
        int r = (int) Math.ceil(radius);
        BlockPos origin = BlockPos.containing(center);
        float radiusSq = radius*radius;


        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {

                    float distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq > radiusSq) continue;

                    BlockPos pos = origin.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    float blastResistance = state.getBlock().getExplosionResistance(state, level, pos, null);

                    if (Float.isNaN(blastResistance) || blastResistance == 0) blastResistance = 0.001f;

                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    /////////////////////////////////////// Define the Fragility vector of the block ///////////////////////////////////////
                    float multiplierFragility = Math.max(1/blastResistance,0.001f);
                    float multiplierDistance = Math.max(1/(float) Math.sqrt(distSq), 0.001f);
                    double multiplierTotal = multiplierFragility * multiplierDistance;

                    /////////////////////////////// Define the Pitch and the Yaw of the block ///////////////////////////////
                    double Yaw = Math.atan2(dx, dz);
                    double Pitch = Math.atan2(BlockData.getHorizontalDistance(dx, dz), dy);

                    ///////////////////////////////////////// Define the different PhysicalBehavior /////////////////////////////////////////
                    EnumSet<PhysicalBehavior> behaviors = BlockBehaviorRegistry.getBehaviors(state);

                    blockDataList.add(
                            new BlockData(
                                    pos,
                                    state,
                                    behaviors,
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
        return blockDataList;
    }
}
