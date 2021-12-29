package me.aleiv.modeltool.events;

import lombok.Getter;
import me.aleiv.modeltool.core.EntityModel;
import org.bukkit.entity.Player;

public class EntityModelDisguiseEvent extends EntityModelEvent {

    @Getter private final Player player;

    public EntityModelDisguiseEvent(EntityModel entityModel, Player player) {
        super(entityModel);
        this.player = player;
    }

}
