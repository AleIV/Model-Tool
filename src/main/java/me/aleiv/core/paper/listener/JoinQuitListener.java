package me.aleiv.core.paper.listener;

import me.aleiv.core.paper.ModelTool;
import me.aleiv.core.paper.core.EntityModel;
import me.aleiv.core.paper.events.EntityModelForceDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class JoinQuitListener implements Listener {

    private final ModelTool plugin;
    private HashMap<UUID, EntityModel> playerCache;

    public JoinQuitListener(ModelTool plugin) {
        this.plugin = plugin;
        this.playerCache = new HashMap<>();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        EntityModel entityModel = this.plugin.getEntityModelManager().getEntityModel(e.getPlayer().getUniqueId());
        if (entityModel != null && entityModel.isDisguised()) {
            this.playerCache.put(e.getPlayer().getUniqueId(), entityModel);
            entityModel.undisguise(); // Already setting gamemode to spectator
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        EntityModel entityModel = this.playerCache.get(e.getPlayer().getUniqueId());
        if (entityModel != null) {
            this.playerCache.remove(e.getPlayer().getUniqueId());
            if (!entityModel.isDisguised()) {
                entityModel.disguise(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onEntityModelForceKill(EntityModelForceDeathEvent e) {
        if (this.playerCache.containsValue(e.getEntityModel())) {
            // Remove the player from the cache
            for (UUID uuid : this.playerCache.keySet()) {
                if (this.playerCache.get(uuid).equals(e.getEntityModel())) {
                    this.playerCache.remove(uuid);
                }
            }
        }
    }
}
