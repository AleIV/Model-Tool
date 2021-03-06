package me.aleiv.modeltool;

import co.aikar.commands.InvalidCommandArgument;
import com.ticxo.modelengine.api.ModelEngineAPI;
import me.aleiv.modeltool.commands.ModelToolCommand;
import me.aleiv.modeltool.core.EntityModel;
import me.aleiv.modeltool.core.EntityModelManager;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.SpigotPlugin;
import lombok.Getter;

@SpigotPlugin
public class ModelTool extends JavaPlugin {

    @Getter private static ModelTool instance;
    private PaperCommandManager commandManager;

    @Getter private EntityModelManager entityModelManager;

    @Override
    public void onEnable() {
        instance = this;

        this.entityModelManager = new EntityModelManager(this);
        this.entityModelManager.setDebug(true);

        this.registerCommands();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        this.commandManager = new PaperCommandManager(this);

        this.commandManager.getCommandCompletions().registerStaticCompletion("bool", new String[]{"true", "false"});
        this.commandManager.getCommandCompletions().registerAsyncCompletion("entitymodels", (ctx) -> this.entityModelManager.getEntityModels().stream().map(EntityModel::getName).collect(java.util.stream.Collectors.toList()));
        this.commandManager.getCommandCompletions().registerAsyncCompletion("modelids", (ctx) -> ModelEngineAPI.api.getModelManager().getModelRegistry().getRegisteredModel().keySet());
        this.commandManager.getCommandCompletions().registerAsyncCompletion("entitymodelsuuid", (ctx) -> this.entityModelManager.getEntityModels().stream().map(em -> em.getUuid().toString()).collect(java.util.stream.Collectors.toList()));
        this.commandManager.getCommandContexts().registerContext(EntityModel.class, (ctx) -> {
            String name = ctx.popFirstArg();
            EntityModel entityModel = this.entityModelManager.getEntityModel(name);
            if (entityModel == null) {
                throw new InvalidCommandArgument("No entity model with the name '" + name + "' exists.");
            }
            return entityModel;
        });

        this.commandManager.registerCommand(new ModelToolCommand(this));
    }

}