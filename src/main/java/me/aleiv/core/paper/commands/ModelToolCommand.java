package me.aleiv.core.paper.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import me.aleiv.core.paper.ModelTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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


}
