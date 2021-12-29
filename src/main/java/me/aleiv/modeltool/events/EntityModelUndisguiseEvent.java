package me.aleiv.modeltool.events;

import lombok.Getter;
import me.aleiv.modeltool.core.EntityModel;
import org.bukkit.entity.Player;

public class EntityModelUndisguiseEvent extends EntityModelEvent {

    @Getter
    private final Player player;

    public EntityModelUndisguiseEvent(EntityModel entityModel, Player player) {
        super(entityModel);
        this.player = player;
    }

}
