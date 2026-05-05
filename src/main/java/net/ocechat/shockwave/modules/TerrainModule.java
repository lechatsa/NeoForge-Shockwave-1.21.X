package net.ocechat.shockwave.modules;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class TerrainModule {

    public static void reshape(Level level, Vec3 center, List<BlockPos> affectedBlocks) {

        if (level.isClientSide()) return;

        for (BlockPos pos : affectedBlocks) {
            BlockState state = level.getBlockState(pos);

            // Ne pas toucher l'air ou les blocs déjà détruits
            if (state.isAir()) continue;

            double dist = Math.sqrt(
                    center.distanceToSqr(
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5));

            // Dépôt de feu sur les bords du cratère
            if (dist > 1.5 && level.random.nextFloat() < 0.15f) {
                BlockPos above = pos.above();
                if (level.getBlockState(above).isAir()) {
                    level.setBlock(above,
                            Blocks.FIRE.defaultBlockState(),
                            3);
                }
            }
        }
    }
}
