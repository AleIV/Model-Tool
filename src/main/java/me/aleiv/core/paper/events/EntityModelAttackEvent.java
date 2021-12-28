package me.aleiv.core.paper.events;

import lombok.Getter;
import me.aleiv.core.paper.core.EntityModel;
import org.bukkit.entity.Entity;

public class EntityModelAttackEvent extends EntityModelEvent {

    @Getter private final Entity target;

    public EntityModelAttackEvent(EntityModel entityModel, Entity target) {
        super(entityModel);
        this.target = target;
    }

}
