package me.aleiv.modeltool.events;

import me.aleiv.modeltool.core.EntityModel;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EntityModelEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final EntityModel entityModel;

    public EntityModelEvent(EntityModel entityModel) {
        super(false);
        this.entityModel = entityModel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public EntityModel getEntityModel() {
        return entityModel;
    }
}
