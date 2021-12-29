package me.aleiv.core.paper;

import co.aikar.commands.InvalidCommandArgument;
import me.aleiv.core.paper.commands.ModelToolCommand;
import me.aleiv.core.paper.core.EntityModel;
import me.aleiv.core.paper.core.EntityModelManager;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.SpigotPlugin;
import lombok.Getter;
import me.aleiv.core.paper.utilities.NegativeSpaces;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;

@SpigotPlugin
public class ModelTool extends JavaPlugin {

    @Getter private static ModelTool instance;
    private PaperCommandManager commandManager;

    @Getter private EntityModelManager entityModelManager;

    @Override
    public void onEnable() {
        instance = this;

        BukkitTCT.registerPlugin(this);
        NegativeSpaces.registerCodes();

        this.registerCommands();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        this.commandManager = new PaperCommandManager(this);

        this.commandManager.getCommandCompletions().registerAsyncCompletion("entitymodels", (ctx) -> this.entityModelManager.getEntityModels().stream().map(EntityModel::getName).collect(java.util.stream.Collectors.toList()));
        this.commandManager.getCommandCompletions().registerAsyncCompletion("entitymodelsuuid", (ctx) -> this.entityModelManager.getEntityModels().stream().map(em -> em.getUuid().toString()).collect(java.util.stream.Collectors.toList()));
        this.commandManager.getCommandContexts().registerContext(EntityModel.class, (ctx) -> {
            String name = ctx.getFirstArg();
            EntityModel entityModel = this.entityModelManager.getEntityModel(name);
            if (entityModel == null) {
                throw new InvalidCommandArgument("No entity model with the name '" + name + "' exists.");
            }
            return entityModel;
        });

        this.commandManager.registerCommand(new ModelToolCommand(this));
    }

}