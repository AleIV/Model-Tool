package me.aleiv.modeltool.events;

import lombok.Getter;
import me.aleiv.modeltool.core.EntityModel;
import org.bukkit.entity.Entity;

public class EntityModelDeathEvent extends EntityModelEvent {

    @Getter
    private final Entity killer;

    public EntityModelDeathEvent(EntityModel entityModel, Entity killer) {
        super(entityModel);
        this.killer = killer;
    }

}
