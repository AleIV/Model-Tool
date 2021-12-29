package me.aleiv.core.paper.events;

import lombok.Getter;
import me.aleiv.core.paper.core.EntityModel;
import org.bukkit.entity.Player;

public class EntityModelDisguiseEvent extends EntityModelEvent {

    @Getter private final Player player;

    public EntityModelDisguiseEvent(EntityModel entityModel, Player player) {
        super(entityModel);
        this.player = player;
    }

}
