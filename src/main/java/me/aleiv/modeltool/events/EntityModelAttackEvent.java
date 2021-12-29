package me.aleiv.modeltool.events;

import lombok.Getter;
import me.aleiv.modeltool.core.EntityModel;
import org.bukkit.entity.Entity;

public class EntityModelAttackEvent extends EntityModelEvent {

    @Getter private final Entity target;

    public EntityModelAttackEvent(EntityModel entityModel, Entity target) {
        super(entityModel);
        this.target = target;
    }

}
