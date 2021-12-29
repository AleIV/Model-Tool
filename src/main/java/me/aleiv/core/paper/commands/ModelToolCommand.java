package me.aleiv.core.paper.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import me.aleiv.core.paper.ModelTool;
import me.aleiv.core.paper.core.EntityModel;
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
    @CommandCompletion("@mobs @none @range:1-1000")
    public void onSpawn(Player player, EntityType entityType, String modelId, @Default("20") Integer health) {
        try {
            plugin.getEntityModelManager().spawnEntityModel(entityType.name() + String.valueOf(new Random().nextInt(999)), health, modelId, player.getLocation(), entityType, EntityMood.NEUTRAL);
        } catch (InvalidModelIdException e) {
            player.sendMessage("§cModelo invalido");
        }
    }

    @Subcommand("disguise")
    @CommandCompletion("@entitimodels")
    public void onDisguise(Player player, EntityModel entityModel) {

    }


}
