package me.aleiv.core.paper.listener;

import me.aleiv.core.paper.ModelTool;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final ModelTool plugin;

    public JoinQuitListener(ModelTool plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (plugin.getEntityModelManager().isPlayerDisguised(e.getPlayer())) {
            this.plugin.getEntityModelManager().undisguisePlayer(e.getPlayer());
        }
    }
}
