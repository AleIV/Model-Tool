package me.aleiv.modeltool.listener;

import com.ticxo.modelengine.api.ModelEngineAPI;
import me.aleiv.modeltool.core.EntityModel;
import me.aleiv.modeltool.core.EntityModelManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.Objects;

public class WorldListener implements Listener {

    private final EntityModelManager manager;
    private boolean hasPlayerJoined;

    public WorldListener(EntityModelManager manager) {
        this.manager = manager;
        this.hasPlayerJoined = false;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldUnload(WorldUnloadEvent e) {
        if (e.isCancelled()) return;

        this.unloadWorld(e.getWorld());
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        Bukkit.getWorlds().forEach(this::unloadWorld);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldLoadEvent e) {
        if (this.hasPlayerJoined) {
            this.checkWorld(e.getWorld());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (this.hasPlayerJoined) return;
        this.hasPlayerJoined = true;
        Bukkit.getWorlds().forEach(this::checkWorld);
    }

    public void checkWorld(World world) {
        world.getEntities().stream().filter(m -> ModelEngineAPI.api.getModelManager().getModeledEntity(m.getUniqueId()) != null).forEach(entity -> Bukkit.getScheduler().runTask(manager.getJavaPlugin(), () -> manager.restoreEntityModel(entity)));
    }

    public void unloadWorld(World world) {
        world.getEntities().stream().map(m -> manager.getEntityModel(m.getUniqueId())).filter(Objects::nonNull).forEach(m -> {
            if (m.isDying()) {
                m.forceKill();
                return;
            }

            m.undisguise();
            manager._removeModel(m.getUuid());
        });
    }

}
