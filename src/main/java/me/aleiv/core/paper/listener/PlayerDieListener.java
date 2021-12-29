package me.aleiv.core.paper.listener;

import me.aleiv.core.paper.ModelTool;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDieListener implements Listener {

    private final ModelTool plugin;

    public PlayerDieListener(ModelTool plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDie(PlayerDeathEvent e) {
        if (plugin.getEntityModelManager().isPlayerDisguised(e.getEntity())) e.setCancelled(true);
    }

}
