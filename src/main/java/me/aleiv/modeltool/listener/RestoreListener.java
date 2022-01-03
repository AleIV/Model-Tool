package me.aleiv.modeltool.listener;

import com.ticxo.modelengine.api.ModelEngineAPI;
import me.aleiv.modeltool.core.EntityModelManager;
import me.aleiv.modeltool.exceptions.AlreadyUsedNameException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.List;
import java.util.Objects;

public class RestoreListener implements Listener {

    private final EntityModelManager manager;

    public RestoreListener(EntityModelManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldUnload(WorldUnloadEvent e) {
        if (e.isCancelled()) return;

        this.unloadWorld(e.getWorld());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDisable(PluginDisableEvent e) {
        Bukkit.getWorlds().forEach(this::unloadWorld);
    }

    @EventHandler
    public void onChunkLoad(EntitiesLoadEvent e) {
        this.manager._debug("Chunk where entities were loaded: " + e.getChunk().getX() + ", " + e.getChunk().getZ());
        this.checkEntities(List.of(e.getChunk().getEntities()));
    }

    public void checkEntities(List<Entity> entities) {
        this.manager._debug("Checking the following entities (" + entities.size() + "): " + entities.toString());
        entities.stream().filter(m -> ModelEngineAPI.api.getModelManager().getModeledEntity(m.getUniqueId()) != null).forEach(entity -> Bukkit.getScheduler().runTask(manager.getJavaPlugin(), () -> {
            this.manager._debug("Entity found: " + entity.getUniqueId());
            try {
                manager.restoreEntityModel(entity);
            } catch (AlreadyUsedNameException ignore) {}
        }));
    }

    public void unloadWorld(World world) {
        world.getEntities().stream().map(m -> manager.getEntityModel(m.getUniqueId())).filter(Objects::nonNull).forEach(m -> {
            if (m.isDying()) {
                m.remove();
                return;
            }

            m.undisguise();
            manager._removeModel(m.getUuid());
        });
    }

}
