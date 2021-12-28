package me.aleiv.core.paper.core;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import lombok.Getter;
import me.aleiv.core.paper.models.EntityMood;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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

    // External variables
    private Entity entity;
    private final ActiveModel activeModel;
    private final ModeledEntity modeledEntity;

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
            this.kill();
        }
    }

    /**
     * Will kill the model and the entity
     */
    public void kill() {
        this.kill(false);
    }

    /**
     * Will kill the model and the entity (can be revived)
     *
     * @param force If true, no animations will be played
     */
    public void kill(boolean force) {
        // TODO: Test death animation if it kills the entity or should be done manually
        // TODO: Fire death event
    }

    public boolean isPlayer() {
        return this.entity instanceof Player;
    }


}
