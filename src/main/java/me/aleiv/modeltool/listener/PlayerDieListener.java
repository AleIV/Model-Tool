package me.aleiv.modeltool.listener;

import me.aleiv.modeltool.ModelTool;
import me.aleiv.modeltool.core.EntityModelManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDieListener implements Listener {

    private final EntityModelManager manager;

    public PlayerDieListener(EntityModelManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDie(PlayerDeathEvent e) {
        if (manager.isPlayerDisguised(e.getEntity())) e.setCancelled(true);
    }

}
