package me.aleiv.core.paper.events;

import lombok.Getter;
import me.aleiv.core.paper.core.EntityModel;
import org.bukkit.entity.Player;

public class EntityModelUndisguiseEvent extends EntityModelEvent {

    @Getter
    private final Player player;

    public EntityModelUndisguiseEvent(EntityModel entityModel, Player player) {
        super(entityModel);
        this.player = player;
    }

}
