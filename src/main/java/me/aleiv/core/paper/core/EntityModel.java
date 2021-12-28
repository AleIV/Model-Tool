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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntityModel {

    // Placeholder variables
    @Getter private UUID uuid;
    /**
     * Useless right now
     */
    @Getter private final String name;

    @Getter private EntityType entityType;
    @Getter private final EntityType originalEntityType;

    // Internal variables
    @Getter private double maxHealth;
    @Getter private double health;
    /**
     * Useless right now
     */
    @Getter private EntityMood mood;

    @Getter private boolean dying;
    @Getter private boolean disguised;

    // External variables
    @Getter private Entity entity;
    @Getter private final ActiveModel activeModel;
    @Getter private ModeledEntity modeledEntity;

    public EntityModel(String name, Entity entity, ActiveModel activeModel, ModeledEntity modeledEntity, double maxHealth) {
        this.uuid = entity.getUniqueId();
        this.name = name;
        this.entityType = entity.getType();
        this.originalEntityType = entity.getType();
        this.entity = entity;
        this.activeModel = activeModel;
        this.modeledEntity = modeledEntity;

        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.mood = EntityMood.NEUTRAL; // TODO: know the mood of entity

        this.dying = false;
        this.disguised = this.entityType != EntityType.PLAYER;
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
        if (!this.disguised) {
            this.entity.remove();
        }
        this.activeModel.clearModel();
    }

    /**
     * Makes the entity go to a location
     *
     * @param loc Location to go to. Must be on the same world
     */
    public void goTo(@NotNull Location loc) { // Maybe add a speed parameter or a boolean to make it go running
        if (disguised) {
            this.entity.sendMessage("Go to " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
            return;
        }

        if (!loc.getWorld().getUID().equals(this.entity.getWorld().getUID())) return;

        // TODO: Need to do NMS stuff
    }

    /**
     * Makes the entity attack another entity
     *
     * @param target Entity to attack. Must be on same world.
     */
    public void attackEntity(@NotNull Entity target) {
        if (disguised) {
            this.entity.sendMessage("Attack " + target.getName() + " of type " + target.getType());
            return;
        }

        if (!target.getWorld().getUID().equals(this.entity.getWorld().getUID())) return;

        // TODO: Need to do NMS stuff
    }

    public void disguise(Player player) {
        if (this.disguised) return;
        this.disguised = true;
        this.entityType = EntityType.PLAYER;

        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().getModeledEntity(player.getUniqueId());
        if (modeledEntity != null) {
            // TODO: Force old model to be undisguised
            // modeledEntity.clearModels();
        } else {
            modeledEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(player);
        }

        player.teleport(this.entity.getLocation());

        this.modeledEntity = modeledEntity;
        this.activeModel.setModeledEntity(modeledEntity);
        this.modeledEntity.setInvisible(true);
        this.modeledEntity.addActiveModel(this.activeModel);

        this.entity.remove();
        this.entity = player;
    }

    public void undisguise() {
        if (!this.disguised) return;

        Entity entity = this.entity.getWorld().spawnEntity(this.entity.getLocation(), this.originalEntityType);
        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().getModeledEntity(this.entity.getUniqueId());
        this.activeModel.setModeledEntity(modeledEntity);
        this.modeledEntity = modeledEntity;
        this.modeledEntity.setInvisible(true);
        this.modeledEntity.addActiveModel(this.activeModel);

        this.entity = entity;
        this.disguised = false;
        this.entityType = this.originalEntityType;
    }

}
