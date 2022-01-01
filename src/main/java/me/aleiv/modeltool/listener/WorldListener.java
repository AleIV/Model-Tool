package me.aleiv.modeltool.listener;

import com.ticxo.modelengine.api.ModelEngineAPI;
import me.aleiv.modeltool.core.EntityModel;
import me.aleiv.modeltool.core.EntityModelManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener implements Listener {

    private final EntityModelManager manager;

    public WorldListener(EntityModelManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldUnload(WorldUnloadEvent e) {
        if (e.isCancelled()) return;

        this.manager.getEntityModels().stream().filter(m -> m.getLocation().getWorld().getUID().equals(e.getWorld().getUID())).filter(EntityModel::isDisguised).forEach(m -> {
            m.undisguise();
            manager._removeModel(m.getUuid());
        });
        this.manager.getEntityModels().stream().filter(EntityModel::isDying).forEach(EntityModel::forceKill);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldLoadEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(manager.getJavaPlugin(), () -> {
            e.getWorld().getEntities().stream().filter(m -> ModelEngineAPI.api.getModelManager().getModeledEntity(m.getUniqueId()) != null).forEach(entity -> {
                Bukkit.getScheduler().runTask(manager.getJavaPlugin(), () -> manager.restoreEntityModel(entity));
            });
        });

    }

}
