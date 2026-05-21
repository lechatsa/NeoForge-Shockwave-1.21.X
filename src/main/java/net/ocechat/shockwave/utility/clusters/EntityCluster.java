package net.ocechat.shockwave.utility.clusters;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public record EntityCluster(List<Entity> entityList, List<Player> playerList) {

    public EntityCluster() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public static EntityCluster find(Level level, Vec3 center, float radius) {

        List<Entity> entityList = level.getEntitiesOfClass(Entity.class, AABB.ofSize(center, radius, radius, radius));
        List<Player> playerList = entityList.stream().filter(Player.class::isInstance).map(Player.class::cast).toList();

        return new EntityCluster(entityList, playerList);
    }

    public List<Player> getPlayers() {
        return this.entityList.stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .toList();
    }

    public List<Entity> getNonPlayers() {
        return this.entityList.stream()
                .filter(entity -> !(entity instanceof Player))
                .toList();
    }

    public <T extends Entity> List<T> getEntity(Class<T> targetClass) {
        return this.entityList.stream()
                .filter(targetClass::isInstance)
                .map(targetClass::cast)
                .toList();
    }

}
