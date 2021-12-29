package me.aleiv.core.paper.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import me.aleiv.core.paper.ModelTool;
import me.aleiv.core.paper.core.EntityModel;
import me.aleiv.core.paper.exceptions.InvalidAnimationException;
import me.aleiv.core.paper.exceptions.InvalidModelIdException;
import me.aleiv.core.paper.models.EntityMood;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Random;

@CommandAlias("modeltool")
@CommandPermission("modeltool.command")
public class ModelToolCommand extends BaseCommand {

    private final ModelTool plugin;

    public ModelToolCommand(ModelTool plugin) {
        this.plugin = plugin;
    }

    @Default
    @CatchUnknown
    @Subcommand("help")
    public void onHelp(CommandSender sender) {

    }

    @Subcommand("fixinvisibility")
    public void onFixinvisibility(Player player) {
        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().getModeledEntity(player.getUniqueId());
        if (modeledEntity != null) {
            modeledEntity.setInvisible(false);
            player.sendMessage("§aYa eres visible");
        } else {
            player.sendMessage("§cNo estas invisible");
        }
    }

    @Subcommand("spawn")
    @CommandCompletion("@mobs @modelids @range:1-1000")
    @Syntax("<Bukkit Models> <ModelId> [Health]")
    public void onSpawn(Player player, EntityType entityType, String modelId, @Default("20") Integer health) {
        try {
            EntityModel entityModel = plugin.getEntityModelManager().spawnEntityModel(entityType.name() + String.valueOf(new Random().nextInt(999)), health, modelId, player.getLocation(), entityType, EntityMood.NEUTRAL);
            player.sendMessage("§aHas spawneado a " + entityModel.getName());
        } catch (InvalidModelIdException e) {
            player.sendMessage("§cModelo invalido");
        }
    }

    @Subcommand("disguise")
    @CommandCompletion("@entitymodels")
    @Syntax("<EntityModelName>")
    public void onDisguise(Player player, EntityModel entityModel) {
        entityModel.disguise(player);
        player.sendMessage("§aHas sido disfrazado de " + entityModel.getName());
    }

    @Subcommand("undisguise")
    public void onUndisguise(Player player) {
        plugin.getEntityModelManager().undisguisePlayer(player);
    }

    @Subcommand("kill")
    @CommandCompletion("@entitymodels")
    @Syntax("<EntityModelName>")
    public void onKill(CommandSender sender, EntityModel entityModel) {
        if (entityModel.isDying()) {
            sender.sendMessage("§cEl modelo ya se esta muriendo");
            return;
        }

        entityModel.kill(null);
        sender.sendMessage("§aHas matado a " + entityModel.getName());
    }

    @Subcommand("forcekill")
    @CommandCompletion("@entitymodels")
    @Syntax("<EntityModelName>")
    public void onForcekill(CommandSender sender, EntityModel entityModel) {
        entityModel.forceKill();
        sender.sendMessage("§aHas matado a " + entityModel.getName());
    }

    @Subcommand("playanim")
    @CommandCompletion("@entitymodels @nothing")
    @Syntax("<EntityModelName> <AnimationName>")
    public void onForcekill(CommandSender sender, EntityModel entityModel, String animation) {
        try {
            entityModel.playAnimation(animation);
            sender.sendMessage("§aHas reproducido la animacion " + animation + " en " + entityModel.getName());
        } catch (InvalidAnimationException e) {
            sender.sendMessage("§cNo existe la animacion " + animation + " en el modelo " + entityModel.getActiveModel().getModelId());
        }
    }

}
