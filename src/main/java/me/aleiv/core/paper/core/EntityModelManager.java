package me.aleiv.core.paper.core;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import me.aleiv.core.paper.ModelTool;
import me.aleiv.core.paper.events.EntityModelAttackEvent;
import me.aleiv.core.paper.events.EntityModelDamageEvent;
import me.aleiv.core.paper.events.EntityModelSpawnEvent;
import me.aleiv.core.paper.exceptions.InvalidModelIdException;
import me.aleiv.core.paper.models.EntityMood;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EntityModelManager implements Listener {

    private final ModelTool plugin;

    private final HashMap<UUID, EntityModel> entityModelHashMap;

    public EntityModelManager(ModelTool modelTool) {
        this.plugin = modelTool;
        this.entityModelHashMap = new HashMap<>();
    }

    /**
     * Registers itself as a listener
     */
    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
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
        return entityModelHashMap.values().stream().filter(entityModel -> entityModel.getName().equalsIgnoreCase(entityModelName)).findFirst().orElse(null);
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

        EntityModel entityModel = new EntityModel(name, entity, activeModel, modeledEntity, health, entityMood);

        this.entityModelHashMap.put(entity.getUniqueId(), entityModel);

        Bukkit.getPluginManager().callEvent(new EntityModelSpawnEvent(entityModel));

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
     */
    public void undisguisePlayer(Player player) {
        getEntityModels().stream()
            .filter(em -> em.getEntity().getUniqueId().equals(player.getUniqueId()))
            .findFirst().ifPresentOrElse(
                em -> {
                    em.undisguise();
                    player.sendMessage("§6Ya no estas disfrazado de " + em.getName());
                },
                () -> player.sendMessage("§cNo estas en un modelo"));
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

        if (entityModel != null && entityModel.getHealth() != 0) {
            double newHealth = entityModel.getHealth() - e.getDamage();

            if (newHealth <= 0) {
                entityModel.kill(entity);
            } else {
                entityModel.setHealth(newHealth);
                Bukkit.getPluginManager().callEvent(new EntityModelDamageEvent(entityModel, cause == EventDamageCause.ENTITY ? ((EntityDamageByEntityEvent) e).getDamager() : null, cause, e.getDamage()));
            }
        }

        // Detect if entity attacked
        if (cause == EventDamageCause.ENTITY) {
            entityModel = this.entityModelHashMap.get(((EntityDamageByEntityEvent) e).getDamager().getUniqueId());
            if (entityModel != null) {
                Bukkit.getPluginManager().callEvent(new EntityModelAttackEvent(entityModel, entity));
            }
        }
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
