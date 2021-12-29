package me.aleiv.modeltool.listener;

import lombok.Getter;
import me.aleiv.modeltool.ModelTool;
import me.aleiv.modeltool.core.EntityModelManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

public class MountUnmountListener implements Listener {

    private final EntityModelManager manager;

    public MountUnmountListener(EntityModelManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerMountEntity(EntityMountEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER && manager.isPlayerDisguised((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

}
