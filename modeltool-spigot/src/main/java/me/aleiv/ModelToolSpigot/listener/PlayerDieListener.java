package me.aleiv.core.paper.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDieListener implements Listener {

    private final me.aleiv.core.paper.ModelToolSpigot plugin;

    public PlayerDieListener(me.aleiv.core.paper.ModelToolSpigot plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDie(PlayerDeathEvent e) {
        if (plugin.getEntityModelManager().isPlayerDisguised(e.getEntity())) e.setCancelled(true);
    }

}
