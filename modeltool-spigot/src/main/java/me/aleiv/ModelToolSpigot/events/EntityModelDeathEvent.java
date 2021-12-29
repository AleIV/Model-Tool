package me.aleiv.core.paper.events;

import lombok.Getter;
import me.aleiv.core.paper.core.EntityModel;
import org.bukkit.entity.Entity;

public class EntityModelDeathEvent extends EntityModelEvent {

    @Getter
    private final Entity killer;

    public EntityModelDeathEvent(EntityModel entityModel, Entity killer) {
        super(entityModel);
        this.killer = killer;
    }

}
