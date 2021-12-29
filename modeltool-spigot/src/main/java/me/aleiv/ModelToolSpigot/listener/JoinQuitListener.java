package me.aleiv.core.paper.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final me.aleiv.core.paper.ModelToolSpigot plugin;

    public JoinQuitListener(me.aleiv.core.paper.ModelToolSpigot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (plugin.getEntityModelManager().isPlayerDisguised(e.getPlayer())) {
            this.plugin.getEntityModelManager().undisguisePlayer(e.getPlayer());
        }
    }
}
