package me.hsgamer.topper.spigot.plugin.template;

import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.agent.core.DataEntryAgent;
import me.hsgamer.topper.query.core.QueryResult;
import me.hsgamer.topper.query.forward.QueryForwardContext;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.spigot.plugin.config.MessageConfig;
import me.hsgamer.topper.spigot.plugin.event.GenericEntryUpdateEvent;
import me.hsgamer.topper.spigot.plugin.manager.ValueProviderManager;
import me.hsgamer.topper.spigot.query.forward.plugin.PluginContext;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.flat.converter.NumberFlatValueConverter;
import me.hsgamer.topper.storage.flat.converter.UUIDFlatValueConverter;
import me.hsgamer.topper.storage.sql.converter.NumberSqlValueConverter;
import me.hsgamer.topper.storage.sql.converter.UUIDSqlValueConverter;
import me.hsgamer.topper.template.storagesupplier.StorageSupplierTemplate;
import me.hsgamer.topper.template.topplayernumber.TopPlayerNumberTemplate;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;
import me.hsgamer.topper.template.topplayernumber.manager.ReloadManager;
import me.hsgamer.topper.value.core.ValueProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;

public class SpigotTopTemplate extends TopPlayerNumberTemplate implements Loadable {
    private final TopperPlugin plugin;
    private final SpigotDataStorageSupplierSettings dataStorageSupplierSettings;

    public SpigotTopTemplate(TopperPlugin plugin) {
        super(new Settings() {
            @Override
            public Map<String, NumberTopHolder.Settings> holders() {
                return plugin.get(MainConfig.class).getHolders();
            }

            @Override
            public int taskSaveEntryPerTick() {
                return plugin.get(MainConfig.class).getTaskSaveEntryPerTick();
            }

            @Override
            public int taskUpdateEntryPerTick() {
                return plugin.get(MainConfig.class).getTaskUpdateEntryPerTick();
            }

            @Override
            public int taskUpdateMaxSkips() {
                return plugin.get(MainConfig.class).getTaskUpdateMaxSkips();
            }
        });
        this.plugin = plugin;
        this.dataStorageSupplierSettings = new SpigotDataStorageSupplierSettings(plugin);
    }

    @Override
    public Function<String, DataStorage<UUID, Double>> getStorageSupplier() {
        return plugin.get(StorageSupplierTemplate.class).getDataStorageSupplier(dataStorageSupplierSettings).getStorageSupplier(
                new UUIDFlatValueConverter(),
                new NumberFlatValueConverter<>(Number::doubleValue),
                new UUIDSqlValueConverter("uuid"),
                new NumberSqlValueConverter<>("value", true, Number::doubleValue)
        );
    }

    @Override
    public Optional<ValueProvider<UUID, Double>> createValueProvider(Map<String, Object> settings) {
        return plugin.get(ValueProviderManager.class).build(settings);
    }

    private Agent createTask(Runnable runnable, boolean async, long delay) {
        return new SpigotRunnableAgent(runnable, async ? AsyncScheduler.get(plugin) : GlobalScheduler.get(plugin), delay);
    }

    @Override
    public Agent createTask(Runnable runnable, NumberTopHolder.TaskType taskType) {
        MainConfig mainConfig = plugin.get(MainConfig.class);
        switch (taskType) {
            case SET:
                return createTask(runnable, true, mainConfig.getTaskUpdateSetDelay());
            case STORAGE:
                return createTask(runnable, true, mainConfig.getTaskSaveDelay());
            case SNAPSHOT:
            default:
                return createTask(runnable, true, 20L);
        }
    }

    @Override
    public Agent createUpdateTask(Runnable runnable, boolean async) {
        return createTask(runnable, async, plugin.get(MainConfig.class).getTaskUpdateDelay());
    }

    @Override
    public void logWarning(String message, @Nullable Throwable throwable) {
        plugin.getLogger().log(Level.WARNING, message, throwable);
    }

    @Override
    public void modifyAgents(NumberTopHolder holder, List<Agent> agents, List<DataEntryAgent<UUID, Double>> entryAgents) {
        agents.add(new Agent() {
            @Override
            public void start() {
                if (plugin.get(MainConfig.class).isLoadAllOfflinePlayers()) {
                    GlobalScheduler.get(plugin).run(() -> {
                        for (OfflinePlayer player : plugin.getServer().getOfflinePlayers()) {
                            holder.getOrCreateEntry(player.getUniqueId());
                        }
                    });
                } else {
                    GlobalScheduler.get(plugin).run(() -> {
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            holder.getOrCreateEntry(player.getUniqueId());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void enable() {
        super.enable();
        getEntryConsumeManager().addConsumer(
                (context) ->
                        AsyncScheduler.get(plugin).run(
                                () ->
                                        Bukkit.getPluginManager().callEvent(new GenericEntryUpdateEvent(
                                                        context.group,
                                                        context.holder,
                                                        context.uuid,
                                                        context.oldValue,
                                                        context.value,
                                                        true
                                                )
                                        )
                        )
        );
        getReloadManager().add(new ReloadManager.ReloadEntry() {
            @Override
            public void reload() {
                plugin.get(MainConfig.class).reloadConfig();
                plugin.get(MessageConfig.class).reloadConfig();
            }
        });
    }

    public void addQueryForwardContext(Plugin plugin, String name, BiFunction<@Nullable UUID, @NotNull String, @NotNull QueryResult> query) {
        getQueryForwardManager().addContext(new PluginTopQueryForwardContext() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public BiFunction<@Nullable UUID, @NotNull String, @NotNull QueryResult> getQuery() {
                return query;
            }

            @Override
            public Plugin getPlugin() {
                return plugin;
            }
        });
    }

    private interface PluginTopQueryForwardContext extends QueryForwardContext<UUID>, PluginContext {
    }
}
