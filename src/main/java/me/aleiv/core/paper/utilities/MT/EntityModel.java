package me.aleiv.core.paper.utilities.MT;

import java.util.UUID;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import lombok.Getter;
import lombok.Setter;

public class EntityModel {

    @Getter @Setter UUID uuid;
    @Getter @Setter String name;
    @Getter String model;
    @Getter Entity entity;
    @Getter ActiveModel activeModel;
    @Getter ModeledEntity modeledEntity;

    @Getter @Setter float health;
    @Getter @Setter String entityState;

    public EntityModel(String model, Location location){
        this.model = model;
    }

    public void kill(){

    }

    public Location getLocation(){
        return null;
    }

    public void teleport(Location location){
        
    }




}
