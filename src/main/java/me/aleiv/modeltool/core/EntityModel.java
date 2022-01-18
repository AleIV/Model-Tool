package me.aleiv.modeltool.core;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.Animation;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import lombok.Getter;
import me.aleiv.modeltool.exceptions.InvalidAnimationException;
import me.aleiv.modeltool.models.EntityMood;
import me.aleiv.modeltool.events.*;
import me.aleiv.modeltool.utilities.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntityModel {

    private JavaPlugin javaPlugin;
    private EntityModelManager manager;

    // Placeholder variables
    @Getter private UUID uuid;
    @Getter private final String name;

    @Getter private EntityType entityType;
    @Getter private final EntityType originalEntityType;

    @Getter private EntityMood mood;

    @Getter private boolean dying;
    @Getter private boolean disguised;

    // External variables
    @Getter private Entity entity;
    @Getter private final ActiveModel activeModel;
    @Getter private ModeledEntity modeledEntity;
    private int taskId;

    private String oldState = null;

    public EntityModel(JavaPlugin javaPlugin, EntityModelManager manager, String name, Entity entity, ActiveModel activeModel, ModeledEntity modeledEntity, double maxHealth, EntityMood mood) {
        this.javaPlugin = javaPlugin;
        this.manager = manager;

        this.uuid = entity.getUniqueId();
        this.name = name;
        this.entityType = entity.getType();
        this.originalEntityType = entity.getType();
        this.entity = entity;
        this.activeModel = activeModel;
        this.modeledEntity = modeledEntity;

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        }
        this.mood = mood;

        this.dying = false;
        this.disguised = this.entityType == EntityType.PLAYER;

        // In seconds
        int walkInterval = 1;

        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.javaPlugin, () -> {
            if (this.modeledEntity.isWalking()) {
                this.playSound(this.getAnimationSound("walk"), 1);
            } else if (!this.isDead() && RandomUtils.generateInt(1, 2) == 3) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.javaPlugin, () -> {
                    if (!this.isDead() && this.modeledEntity.isIdle()) {
                        this.playSound(this.getAnimationSound("idle"), 1);
                    }
                }, RandomUtils.generateInt(2, 10) * 20L);
            }
        }, 0L, walkInterval*20L);
    }

    public void teleport(Location location) {
        this.entity.teleport(location);
    }

    public Location getLocation() {
        return this.entity.getLocation();
    }

    public double getHealth() {
        return (this.entity instanceof LivingEntity) ? ((LivingEntity) this.entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : 0;
    }

    public void setHealth(double health) {
        double newHealth = health >= this.getHealth() ? this.getHealth() : (health <= 0 ? 0 : health);

        if (newHealth == 0) {
            this.kill(null);
            return;
        }

        if (this.entity instanceof LivingEntity livingEntity) {
            livingEntity.setHealth(newHealth);
        } else {
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

        try {
            int length = this.playAnimation("death");
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.javaPlugin, () -> {
                this.remove();
                // TODO: Add some die particles?
                Bukkit.getPluginManager().callEvent(new EntityModelDeathEvent(this, killer));
            }, length);
        } catch (InvalidAnimationException e) {
            this.remove();
        }
    }

    /**
     * Will remove the entity and the model without any animation
     */
    public void remove() {
        if (!this.disguised) {
            this.entity.remove();
        } else {
            ((Player) this.entity).setGameMode(GameMode.SPECTATOR);
        }
        this.activeModel.clearModel();
        this.manager._removeModel(this.uuid);
        Bukkit.getScheduler().cancelTask(this.taskId);
        Bukkit.getPluginManager().callEvent(new EntityModelRemoveEvent(this));
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
        this.mood = mood;
        this.applyMood(mood);

        Bukkit.getPluginManager().callEvent(new EntityModelChangeMoodEvent(this, this.mood, mood));
    }

    private void applyMood(EntityMood mood) {
        this.manager._debug("Applying mood " + mood);
        if (disguised) {
            this.entity.sendMessage("Mood changed to " + mood.name());
            return;
        }

        switch (mood) {
            case STATIC -> {
                if (this.entity instanceof LivingEntity livingEntity) {
                    livingEntity.setAI(false);
                }
            }
            case HOSTILE, NEUTRAL, PEACEFUL -> { // TODO: Need to do NMS stuff
                if (this.entity instanceof LivingEntity livingEntity) {
                    livingEntity.setAI(true);
                }
            }
        }
    }

    public void disguise(Player player) {
        if (this.disguised || this.dying) return;
        this.manager._debug("Disguising " + this.uuid);
        this.disguised = true;
        this.entityType = EntityType.PLAYER;

        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().getModeledEntity(player.getUniqueId());
        if (modeledEntity != null) {
            // Removing old disguise of the player
            this.manager.undisguisePlayer(player);
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
        this.manager._debug("Undisguising " + this.uuid);
        Player player = (Player) this.entity;
        player.setGameMode(GameMode.SPECTATOR);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());

        Entity entity = this.entity.getWorld().spawnEntity(this.entity.getLocation(), this.originalEntityType);
        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(entity);
        this.modeledEntity.clearModels();
        this.modeledEntity = modeledEntity;
        this.modeledEntity.setInvisible(true);
        this.modeledEntity.addActiveModel(this.activeModel);

        this.entity = entity;
        this.applyMood(this.mood);
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
        this.manager._updateUUID(oldUUID, newUUID);
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
     * @return Length of the animation
     */
    public int playAnimation(String animationName) throws InvalidAnimationException {
        return this.playAnimation(animationName, false);
    }

    /**
     * Plays an animation
     *
     * @param animationName Name of the animation
     * @param loop If loop is true, the animation will loop
     * @return Length of the animation
     */
    public int playAnimation(String animationName, boolean loop) throws InvalidAnimationException {
        Animation animation = getAnimation(animationName);

        if (animation == null) {
            throw new InvalidAnimationException(this.activeModel.getModelId(), animationName);
        }

        this.manager._debug("Playing animation " + animationName + " on " + this.name);

        if (this.oldState != null) {
            this.activeModel.removeState(animationName, false);
            this.oldState = null;
        }

        this.activeModel.addState(animationName, 1, 1, 1);
        this.playSound(this.getAnimationSound(animationName), 1);

        if (loop) {
            this.oldState = animationName;
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.javaPlugin, () -> this.activeModel.removeState(animationName, false), animation.getLength());
        }
        return animation.getLength();
    }

    public boolean isDead() {
        return this.dying || this.entity.isDead();
    }

    private String getAnimationSound(String animationName) {
        return "modeltool." + this.activeModel.getModelId() + "." + animationName;
    }

    private void playSound(String sound, float vol) {
        this.manager._debug("Playing sound " + sound + " on " + this.name);
        this.entity.getWorld().playSound(this.entity.getLocation(), sound, vol, 1);
    }

}
