package me.aleiv.core.paper.events;

import lombok.Getter;
import me.aleiv.core.paper.core.EntityModel;
import me.aleiv.core.paper.core.EntityModelManager;
import org.bukkit.entity.Entity;

public class EntityModelDamageEvent extends EntityModelEvent{

    @Getter private final Entity damager;
    @Getter private final EntityModelManager.EventDamageCause cause;
    @Getter private final double damage;

    public EntityModelDamageEvent(EntityModel entityModel, Entity damager, EntityModelManager.EventDamageCause cause, double damage) {
        super(entityModel);
        this.damager = damager;
        this.cause = cause;
        this.damage = damage;
    }

}
