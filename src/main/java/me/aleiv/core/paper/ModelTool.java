package me.aleiv.core.paper;

import co.aikar.commands.InvalidCommandArgument;
import com.ticxo.modelengine.api.ModelEngineAPI;
import me.aleiv.core.paper.commands.ModelToolCommand;
import me.aleiv.core.paper.core.EntityModel;
import me.aleiv.core.paper.core.EntityModelManager;
import me.aleiv.core.paper.listener.JoinQuitListener;
import me.aleiv.core.paper.listener.MountUnmountListener;
import me.aleiv.core.paper.listener.PlayerDieListener;
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

        this.entityModelManager = new EntityModelManager(this);
        this.entityModelManager.registerListener();

        this.registerCommands();
        this.registerListeners();
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

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDieListener(this), this);
        this.getServer().getPluginManager().registerEvents(new MountUnmountListener(this), this);
    }

}