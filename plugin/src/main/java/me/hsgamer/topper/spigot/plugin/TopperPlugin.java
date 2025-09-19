package me.hsgamer.topper.spigot.plugin;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bstats.bukkit.Metrics;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.command.CommandComponent;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.topper.spigot.plugin.builder.ValueProviderBuilder;
import me.hsgamer.topper.spigot.plugin.command.GetTopListCommand;
import me.hsgamer.topper.spigot.plugin.command.ReloadCommand;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.spigot.plugin.config.MessageConfig;
import me.hsgamer.topper.spigot.plugin.format.FormatManager;
import me.hsgamer.topper.spigot.plugin.hook.HookSystem;
import me.hsgamer.topper.spigot.plugin.listener.JoinListener;
import me.hsgamer.topper.spigot.plugin.manager.EntryConsumeManager;
import me.hsgamer.topper.spigot.plugin.manager.QueryForwardManager;
import me.hsgamer.topper.spigot.plugin.manager.TopManager;
import me.hsgamer.topper.spigot.plugin.manager.TopQueryManager;
import me.hsgamer.topper.spigot.storage.SpigotDataStorageBuilder;
import me.hsgamer.topper.storage.bundle.DataStorageBuilder;

public class TopperPlugin extends BasePlugin {
    public static final String GROUP = "topper";

    @Override
    protected List<Object> getComponents() {
        return Arrays.asList(
                SpigotDataStorageBuilder.register(new DataStorageBuilder()),
                new ValueProviderBuilder(),
                ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this)),
                ConfigGenerator.newInstance(MessageConfig.class, new BukkitConfig(this, "messages.yml")),

                new HookSystem(this),

                new TopManager(this),
                new TopQueryManager(this),
                new QueryForwardManager(this),
                new EntryConsumeManager(this),

                new Permissions(this),
                new CommandComponent(this,
                        new ReloadCommand(this),
                        new GetTopListCommand(this)
                ),
                new JoinListener(this)
        );
    }

    @Override
    public void load() {
        MessageUtils.setPrefix(get(MessageConfig.class)::getPrefix);
    }

    @Override
    public void enable() {
        FormatManager.init(get(MainConfig.class));
        new Metrics(this, 14938);
        if (getDescription().getVersion().contains("SNAPSHOT")) {
            getLogger().warning("You are using the development version");
            getLogger().warning("This is not ready for production");
            getLogger().warning("Use in your own risk");
        } else {
            new SpigotVersionChecker(101325).getVersion().whenComplete((output, throwable) -> {
                if (throwable != null) {
                    getLogger().log(Level.WARNING, "Failed to check spigot version", throwable);
                } else if (output != null) {
                    if (this.getDescription().getVersion().equalsIgnoreCase(output)) {
                        getLogger().info("You are using the latest version");
                    } else {
                        getLogger().warning("There is an available update");
                        getLogger().log(Level.WARNING, "New Version: {0}", output);
                    }
                }
            });
        }
    }
}
