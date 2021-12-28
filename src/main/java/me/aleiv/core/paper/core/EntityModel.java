package me.aleiv.core.paper.core;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import lombok.Getter;
import me.aleiv.core.paper.ModelTool;
import me.aleiv.core.paper.events.EntityModelDeathEvent;
import me.aleiv.core.paper.models.EntityMood;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntityModel {

    // Placeholder variables
    @Getter private final UUID uuid;
    /**
     * Useless right now
     */
    @Getter private final String name;

    // Internal variables
    @Getter private double maxHealth;
    @Getter private double health;
    /**
     * Useless right now
     */
    @Getter private EntityMood mood;

    @Getter private boolean dying;

    // External variables
    @Getter private Entity entity;
    @Getter private final ActiveModel activeModel;
    @Getter private final ModeledEntity modeledEntity;

    public EntityModel(UUID uuid, String name, Entity entity, ActiveModel activeModel, ModeledEntity modeledEntity, double maxHealth) {
        this.uuid = uuid;
        this.name = name;
        this.entity = entity;
        this.activeModel = activeModel;
        this.modeledEntity = modeledEntity;

        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.mood = EntityMood.NEUTRAL; // TODO: know the mood of entity
    }

    public void teleport(Location location) {
        this.entity.teleport(location);
    }

    public Location getLocation() {
        return this.entity.getLocation();
    }

    public void setHealth(double health) {
        this.health = health >= this.maxHealth ? this.maxHealth : (health <= 0 ? 0 : health);

        if (this.health == 0) {
            this.kill(null);
        }
    }

    /**
     * Will kill the model and the entity
     *
     * @param killer Entity that killed the mob. Can be null
     */
    public void kill(Entity killer) {
        if (this.dying) return;
        this.dying = true;
        int frames = this.activeModel.getBlueprint().getAnimation("death").getLength(); // Every frame is a tick

        this.activeModel.addState("death", 0, 0, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(ModelTool.getInstance(), () -> {
            this.forceKill();
            // TODO: Add some die particles?
            Bukkit.getPluginManager().callEvent(new EntityModelDeathEvent(this, killer));
        }, frames);
    }

    /**
     * Will remove the entity without launching any event, or state
     */
    public void forceKill() {
        this.entity.remove();
        this.activeModel.clearModel();
    }

    /**
     * Makes the entity go to a location
     *
     * @param loc Location to go to. Must be on the same world
     */
    public void goTo(@NotNull Location loc) { // Maybe add a speed parameter or a boolean to make it go running
        if (!loc.getWorld().getUID().equals(this.entity.getWorld().getUID())) return;

        // TODO: Need to do NMS stuff
    }

    /**
     * Makes the entity attack another entity
     *
     * @param target Entity to attack. Must be on same world.
     */
    public void attackEntity(@NotNull Entity target) {
        if (!target.getWorld().getUID().equals(this.entity.getWorld().getUID())) return;

        // TODO: Need to do NMS stuff
    }

}
