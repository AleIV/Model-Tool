package me.aleiv.core.paper.utilities.MT;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class EntityModelManager implements Listener{

    private static HashMap<UUID, EntityModel> entityModels = new HashMap<>();
    private static HashMap<UUID, EntityModel> entityModeldisguise = new HashMap<>();

    public EntityModelManager(){
        //listener
    }

    public enum Mood{
        NEUTRAL, PEACEFUL, HOSTILE, STATIC
    }

    public static EntityModel getEntityModel(UUID uuid){
        return null;
    }

    public static List<EntityModel> getNearbyEntityModel(float radius){
        return null;

    }

    public static boolean disguise(Player player, String model){
        return false;
    }

    public static boolean undisguise(Player player){
        return false;
    }


    
}
