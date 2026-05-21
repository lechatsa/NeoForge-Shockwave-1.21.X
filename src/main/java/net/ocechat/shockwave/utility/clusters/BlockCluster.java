package net.ocechat.shockwave.utility.clusters;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public record BlockCluster(List<BlockData> blockDataList, Vec3 center) {

    public static BlockCluster empty(Vec3 center) {
        return new BlockCluster(new ArrayList<>(), center);
    }

    public List<BlockData> asList() {
        return this.blockDataList;
    }

    public BlockData get(int index) {
        return blockDataList.get(index);
    }

    public int size() {
        return blockDataList.size();
    }

    public void add(BlockData blockData) {
        blockDataList.add(blockData);
    }


}
