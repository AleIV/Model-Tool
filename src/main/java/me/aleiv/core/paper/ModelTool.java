package me.aleiv.core.paper;

import me.aleiv.core.paper.commands.ModelToolCommand;
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
        this.commandManager.registerCommand(new ModelToolCommand(this));
    }

}