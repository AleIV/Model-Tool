package me.aleiv.core.paper.events;

import me.aleiv.core.paper.core.EntityModel;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EntityModelEvent extends Event {

    private final HandlerList handlerList = new HandlerList();

    private final EntityModel entityModel;

    public EntityModelEvent(EntityModel entityModel) {
        super(false);
        this.entityModel = entityModel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public EntityModel getEntityModel() {
        return entityModel;
    }
}
