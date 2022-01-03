package me.aleiv.modeltool.core;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import lombok.Getter;
import me.aleiv.modeltool.events.EntityModelAttackEvent;
import me.aleiv.modeltool.events.EntityModelDamageEvent;
import me.aleiv.modeltool.events.EntityModelSpawnEvent;
import me.aleiv.modeltool.exceptions.InvalidAnimationException;
import me.aleiv.modeltool.exceptions.InvalidModelIdException;
import me.aleiv.modeltool.listener.JoinQuitListener;
import me.aleiv.modeltool.listener.MountUnmountListener;
import me.aleiv.modeltool.listener.PlayerDieListener;
import me.aleiv.modeltool.listener.RestoreListener;
import me.aleiv.modeltool.models.EntityMood;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntityModelManager implements Listener {

    @Getter private final JavaPlugin javaPlugin;
    private final HashMap<UUID, EntityModel> entityModelHashMap;
    private final HashMap<String, EntityModel> entityModelNameHashMap;

    public EntityModelManager(JavaPlugin plugin) {
        this.javaPlugin = plugin;
        this.entityModelHashMap = new HashMap<>();
        this.entityModelNameHashMap = new HashMap<>();

        // Registering Listeners
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerDieListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new MountUnmountListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new RestoreListener(this), plugin);
    }

    /**
     *
     * @param entityUUID The UUID of the bukkit entity
     * @return The EntityModel of the entity or null if no entity exists
     */
    public EntityModel getEntityModel(UUID entityUUID) {
        return entityModelHashMap.get(entityUUID);
    }

    /**
     * Get an EntityModel from the name
     *
     * @param entityModelName The Name of the entityModel (not case sensitive)
     * @return The EntityModel of the entity or null if no matches are found
     */
    public EntityModel getEntityModel(String entityModelName) {
        return entityModelNameHashMap.get(entityModelName.toLowerCase());
    }

    /**
     * @return List of all EntityModels
     */
    public List<EntityModel> getEntityModels() {
        return this.entityModelHashMap.values().stream().toList();
    }

    /**
     * Creates and spawns an entity with a model
     *
     * @param name Name that the entity will have
     * @param health Health of the entity
     * @param modelId ID of the ModelEngine model
     * @param loc Location where the mob will be spawned
     * @param entityType Type of the entity that will be used as base
     * @return The spawned EntityModel
     * @throws InvalidModelIdException If the modelId is invalid or the model is not found
     */
    public EntityModel spawnEntityModel(String name, double health, String modelId, Location loc, EntityType entityType, EntityMood entityMood) throws InvalidModelIdException {
        Entity entity = loc.getWorld().spawnEntity(loc, entityType);
        entity.setCustomName(name);

        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(entity);
        ActiveModel activeModel = ModelEngineAPI.api.getModelManager().createActiveModel(modelId);
        if (activeModel == null) {
            throw new InvalidModelIdException(modelId);
        }

        modeledEntity.addActiveModel(activeModel);
        modeledEntity.detectPlayers();
        modeledEntity.setInvisible(true);

        EntityModel entityModel = new EntityModel(this.javaPlugin, this, name, entity, activeModel, modeledEntity, health, entityMood);

        this.entityModelHashMap.put(entity.getUniqueId(), entityModel);
        this.entityModelNameHashMap.put(name.toLowerCase(), entityModel);

        Bukkit.getPluginManager().callEvent(new EntityModelSpawnEvent(entityModel));

        return entityModel;
    }

    /**
     * If the entity is valid, it will be restored to an EntityModel.
     *
     * @param entity The entity to restore.
     * @return The restored EntityModel or null if the entity is not valid.
     */
    public EntityModel restoreEntityModel(Entity entity) {
        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().getModeledEntity(entity.getUniqueId());
        if (modeledEntity == null) {
            return null;
        }

        ActiveModel activeModel = modeledEntity.getAllActiveModel().values().stream().filter(am -> am.getModelId() != null && !am.getModelId().equals("")).findFirst().orElse(null);
        if (activeModel == null) {
            return null;
        }

        EntityModel entityModel = new EntityModel(this.javaPlugin, this, entity.getCustomName(), entity, activeModel, modeledEntity, (entity instanceof LivingEntity) ? ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : 20, EntityMood.NEUTRAL);
        this.entityModelHashMap.put(entity.getUniqueId(), entityModel);
        this.entityModelNameHashMap.put(entityModel.getName(), entityModel);

        return entityModel;
    }

    /**
     * Alias of {@link EntityModel#disguise(Player)}
     *
     * @param player Player to disguise
     * @param entityModel EntityModel to disguise to
     */
    public void disguisePlayer(Player player, EntityModel entityModel) {
        entityModel.disguise(player);
    }

    /**
     * Undisguise a player if it's in a model
     *
     * @param player Player to undisguise
     * @return True if the player was undisguised, false if the player was not in a model
     */
    public boolean undisguisePlayer(Player player) {
        AtomicBoolean result = new AtomicBoolean(false);
        getEntityModels().stream()
            .filter(em -> em.getEntity().getUniqueId().equals(player.getUniqueId()))
            .findFirst().ifPresent(
                em -> {
                    em.undisguise();
                    result.set(true);
                });
        return result.get();
    }

    /**
     * Checks if a player is disguised
     *
     * @param player Player to check
     * @return True if the player is disguised
     */
    public boolean isPlayerDisguised(Player player) {
        return getEntityModels().stream()
            .anyMatch(em -> em.getEntity().getUniqueId().equals(player.getUniqueId()));
    }

    public enum EventDamageCause {
        NONE,
        BLOCK,
        ENTITY,
    }

    @EventHandler
    public void onModelDamage(EntityDamageEvent e) {
        this.causeDamage(e, EventDamageCause.NONE);
    }

    @EventHandler
    public void onModelDamageByBlock(EntityDamageByBlockEvent e) {
        this.causeDamage(e, EventDamageCause.BLOCK);
    }

    @EventHandler
    public void onModelDamageByEntity(EntityDamageByEntityEvent e) {
        this.causeDamage(e, EventDamageCause.ENTITY);
    }

    private void causeDamage(EntityDamageEvent e, EventDamageCause cause) {
        Entity entity = e.getEntity();

        // Detect if damaged
        EntityModel entityModel = this.entityModelHashMap.get(entity.getUniqueId());

        if (entityModel != null && !entityModel.isDying()) {
            double newHealth = entityModel.getHealth() - e.getDamage();

            if (newHealth <= 0) {
                entityModel.kill(entity);
                e.setCancelled(true);
            } else {
                // e.setDamage(0);
                // entityModel.setHealth(newHealth);
                Bukkit.getPluginManager().callEvent(new EntityModelDamageEvent(entityModel, cause == EventDamageCause.ENTITY ? ((EntityDamageByEntityEvent) e).getDamager() : null, cause, e.getDamage()));
            }
        }

        // Detect if entity attacked
        if (cause == EventDamageCause.ENTITY) {
            entityModel = this.entityModelHashMap.get(((EntityDamageByEntityEvent) e).getDamager().getUniqueId());
            if (entityModel != null) {
                try {
                    entityModel.playAnimation("attack");
                } catch (InvalidAnimationException ignore) {}
                Bukkit.getPluginManager().callEvent(new EntityModelAttackEvent(entityModel, entity));
            }
        }
    }

    public void _updateUUID(UUID oldUUID, UUID newUUID) {
        EntityModel entityModel = this.entityModelHashMap.get(oldUUID);
        if (entityModel != null) {
            this.entityModelHashMap.remove(oldUUID);
            this.entityModelHashMap.put(newUUID, entityModel);
        }
    }

    public void _removeModel(UUID uuid) {
        this.entityModelHashMap.remove(uuid);
    }

    // The entity cannot die (shouldn't trigger this event)
    /*@EventHandler
    private void onEntityDie(EntityDeathEvent e) {
        EntityModel entityModel = this.entityModelHashMap.get(e.getEntity().getUniqueId());
        if (entityModel != null) {
            this.entityModelHashMap.remove(e.getEntity().getUniqueId());
        }
    }*/

    // Not using this by now
    /*@EventHandler
    public void onEntityMove(EntityMoveEvent e) {
        EntityModel entityModel = this.entityModelHashMap.get(e.getEntity().getUniqueId());
        if (entityModel != null) {
            Bukkit.getPluginManager().callEvent(new EntityModelMoveEvent(entityModel, e.getFrom(), e.getTo()));
        }
    }*/

}
