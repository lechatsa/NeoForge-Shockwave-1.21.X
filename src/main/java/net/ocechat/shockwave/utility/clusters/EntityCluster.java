package net.ocechat.shockwave.utility.clusters;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public record EntityCluster(List<Entity> entityList, List<Player> playerList) {

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
