package me.aleiv.modeltool.events;

import me.aleiv.modeltool.core.EntityModel;

public class EntityModelRemoveEvent extends EntityModelEvent {

    public EntityModelRemoveEvent(EntityModel entityModel) {
        super(entityModel);
    }

}
