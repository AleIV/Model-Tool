package me.aleiv.modeltool.events;

import lombok.Getter;
import me.aleiv.modeltool.core.EntityModel;
import me.aleiv.modeltool.models.EntityMood;

public class EntityModelChangeMoodEvent extends EntityModelEvent {

    @Getter private final EntityMood oldMood;
    @Getter private final EntityMood newMood;

    public EntityModelChangeMoodEvent(EntityModel entityModel, EntityMood oldMood, EntityMood newMood) {
        super(entityModel);
        this.oldMood = oldMood;
        this.newMood = newMood;
    }

}
