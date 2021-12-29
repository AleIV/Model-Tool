package me.aleiv.core.paper.events;

import lombok.Getter;
import me.aleiv.core.paper.core.EntityModel;
import me.aleiv.core.paper.models.EntityMood;

public class EntityModelChangeMoodEvent extends EntityModelEvent {

    @Getter private final EntityMood oldMood;
    @Getter private final EntityMood newMood;

    public EntityModelChangeMoodEvent(EntityModel entityModel, EntityMood oldMood, EntityMood newMood) {
        super(entityModel);
        this.oldMood = oldMood;
        this.newMood = newMood;
    }

}
