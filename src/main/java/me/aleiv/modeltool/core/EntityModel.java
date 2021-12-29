package me.aleiv.modeltool.core;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.Animation;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import lombok.Getter;
import me.aleiv.modeltool.ModelTool;
import me.aleiv.core.paper.events.*;
import me.aleiv.modeltool.exceptions.InvalidAnimationException;
import me.aleiv.modeltool.models.EntityMood;
import me.aleiv.modeltool.events.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

    public EntityModel(String name, Entity entity, ActiveModel activeModel, ModeledEntity modeledEntity, double maxHealth, EntityMood mood) {
        this.uuid = entity.getUniqueId();
        this.name = name;
        this.entityType = entity.getType();
        this.originalEntityType = entity.getType();
        this.entity = entity;
        this.activeModel = activeModel;
        this.modeledEntity = modeledEntity;

        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.mood = mood;

        this.dying = false;
        this.disguised = this.entityType == EntityType.PLAYER;
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
        if (this.disguised) {
            ((Player) this.entity).setGameMode(GameMode.SPECTATOR);
        }

        Animation deathAnimation = this.getAnimation("death");
        if (deathAnimation == null) {
            this.forceKill();
            return;
        }

        int frames = deathAnimation.getLength(); // Every frame is a tick

        this.activeModel.addState("death", 0, 0, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(ModelTool.getInstance(), () -> {
            this.forceKill();
            // TODO: Add some die particles?
            Bukkit.getPluginManager().callEvent(new EntityModelDeathEvent(this, killer));
        }, frames);
    }

    /**
     * Will remove the entity and the model without any animation
     */
    public void forceKill() {
        if (!this.disguised) {
            this.entity.remove();
        } else {
            ((Player) this.entity).setGameMode(GameMode.SPECTATOR);
        }
        this.activeModel.clearModel();
        ModelTool.getInstance().getEntityModelManager()._removeModel(this.uuid);
        Bukkit.getPluginManager().callEvent(new EntityModelForceDeathEvent(this));
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
     * Makes the entity attack another entity. Will change mood to hostile
     *
     * @param target Entity to attack. Must be on same world.
     */
    public void attackEntity(@NotNull Entity target) {
        if (disguised) {
            this.entity.sendMessage("Attack " + target.getName() + " of type " + target.getType());
            return;
        }

        if (!target.getWorld().getUID().equals(this.entity.getWorld().getUID())) return;
        this.mood = EntityMood.HOSTILE;

        // TODO: Need to do NMS stuff
    }

    /**
     * Changes the entity's mood
     *
     * @param mood New mood
     */
    public void setMood(EntityMood mood) {
        if (disguised) {
            this.entity.sendMessage("Mood changed to " + mood.name());
            return;
        }

        // TODO: Need to do NMS stuff
        Bukkit.getPluginManager().callEvent(new EntityModelChangeMoodEvent(this, this.mood, mood));
        this.mood = mood;
    }

    public void disguise(Player player) {
        if (this.disguised || this.dying) return;
        this.disguised = true;
        this.entityType = EntityType.PLAYER;

        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().getModeledEntity(player.getUniqueId());
        if (modeledEntity != null) {
            // Removing old disguise of the player
            ModelTool.getInstance().getEntityModelManager().undisguisePlayer(player);
        } else {
            modeledEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(player);
        }

        player.teleport(this.entity.getLocation());

        this.modeledEntity.clearModels();
        this.modeledEntity.getAllActiveModel().clear();

        this.modeledEntity = modeledEntity;
        this.modeledEntity.setInvisible(true);
        this.modeledEntity.addActiveModel(this.activeModel);

        this.entity.remove();
        this.entity = player;
        this.updateUUID(entity.getUniqueId());
        this.entityType = EntityType.PLAYER;

        this.modeledEntity.detectPlayers();

        Bukkit.getPluginManager().callEvent(new EntityModelDisguiseEvent(this, player));
    }

    public void undisguise() {
        if (!this.disguised || this.dying) return;
        Player player = (Player) this.entity;
        player.setGameMode(GameMode.SPECTATOR);

        Entity entity = this.entity.getWorld().spawnEntity(this.entity.getLocation(), this.originalEntityType);
        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(entity);
        this.modeledEntity.clearModels();
        this.modeledEntity = modeledEntity;
        this.modeledEntity.setInvisible(true);
        this.modeledEntity.addActiveModel(this.activeModel);

        this.entity = entity;
        this.updateUUID(entity.getUniqueId());
        this.disguised = false;
        this.entityType = this.originalEntityType;
        ModeledEntity playerModel = ModelEngineAPI.api.getModelManager().createModeledEntity(player);
        if (playerModel != null) {
            playerModel.clearModels();
            playerModel.getAllActiveModel().clear();
        }

        this.modeledEntity.detectPlayers();

        Bukkit.getPluginManager().callEvent(new EntityModelUndisguiseEvent(this, player));
    }

    private void updateUUID(UUID newUUID) {
        UUID oldUUID = this.uuid;
        this.uuid = newUUID;
        ModelTool.getInstance().getEntityModelManager()._updateUUID(oldUUID, newUUID);
    }

    // Animations stuff

    /**
     * Gets an animation from the model
     *
     * @param animationName Name of the animation
     * @return Animation, or null if not found or doesn't exist
     */
    public Animation getAnimation(String animationName) {
        return this.activeModel.getBlueprint().getAnimation(animationName);
    }

    /**
     * Checks if an animation exists for this model
     *
     * @param animationName Name of the animation
     * @return True if the animation exists
     */
    public boolean doesAnimationExist(String animationName) {
        return getAnimation(animationName) != null;
    }

    /**
     * Plays an animation
     *
     * @param animationName Name of the animation
     */
    public void playAnimation(String animationName) throws InvalidAnimationException {
        Animation animation = getAnimation(animationName);

        if (animation == null) {
            throw new InvalidAnimationException(this.activeModel.getModelId(), animationName);
        }

        this.activeModel.addState(animationName, 1, 1, 1);

        // TODO: Use the TaskChainTool
        Bukkit.getScheduler().scheduleSyncDelayedTask(ModelTool.getInstance(), () -> this.activeModel.removeState(animationName, false), animation.getLength());
    }

}
