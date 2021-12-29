package me.aleiv.core.paper.listener;

import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

public class MountUnmountListener implements Listener {

    @Getter private final me.aleiv.core.paper.ModelToolSpigot plugin;

    public MountUnmountListener(me.aleiv.core.paper.ModelToolSpigot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMountEntity(EntityMountEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER && plugin.getEntityModelManager().isPlayerDisguised((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

}
