package me.aleiv.modeltool.events;

import lombok.Getter;
import me.aleiv.modeltool.core.EntityModel;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EntityModelMoveEvent extends EntityModelEvent {

    private HandlerList handlers = new HandlerList();

    @Getter private final Location from;
    @Getter private final Location to;

    public EntityModelMoveEvent(EntityModel entityModel, Location from, Location to) {
        super(entityModel);
        this.from = from;
        this.to = to;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }
}
