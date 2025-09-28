package me.hsgamer.topper.spigot.plugin.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.agent.core.AgentHolder;
import me.hsgamer.topper.agent.core.DataEntryAgent;
import me.hsgamer.topper.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.agent.update.UpdateAgent;
import me.hsgamer.topper.data.core.DataEntry;
import me.hsgamer.topper.data.simple.SimpleDataHolder;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.spigot.plugin.holder.display.ValueDisplay;
import me.hsgamer.topper.spigot.plugin.manager.EntryConsumeManager;
import me.hsgamer.topper.spigot.plugin.manager.TopManager;
import me.hsgamer.topper.spigot.plugin.manager.ValueProviderManager;
import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topper.value.core.ValueWrapper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

public class NumberTopHolder extends SimpleDataHolder<UUID, Double> implements AgentHolder<UUID, Double> {
    private final ValueDisplay valueDisplay;
    private final List<Agent> agents;
    private final List<DataEntryAgent<UUID, Double>> entryAgents;
    private final StorageAgent<UUID, Double> storageAgent;
    private final UpdateAgent<UUID, Double> updateAgent;
    private final SnapshotAgent<UUID, Double> snapshotAgent;
    private final Double defaultValue;

    public NumberTopHolder(TopperPlugin instance, String name, Map<String, Object> map) {
        this.defaultValue = Optional.ofNullable(map.get("default-value"))
                .map(Object::toString)
                .map(s -> {
                    try {
                        return Double.parseDouble(s);
                    } catch (NumberFormatException e) {
                        instance.getLogger().log(Level.WARNING, "Invalid default value for " + name + ": " + s + ". Fallback to null", e);
                        return null;
                    }
                })
                .orElse(null);

        this.agents = new ArrayList<>();
        this.entryAgents = new ArrayList<>();
        this.valueDisplay = new ValueDisplay(map);

        this.storageAgent = new StorageAgent<>(instance.get(TopManager.class).buildStorage(name));
        storageAgent.setMaxEntryPerCall(instance.get(MainConfig.class).getTaskSaveEntryPerTick());
        agents.add(storageAgent);
        agents.add(storageAgent.getLoadAgent(this));
        agents.add(new SpigotRunnableAgent(storageAgent, AsyncScheduler.get(instance), instance.get(MainConfig.class).getTaskSaveDelay()));
        entryAgents.add(storageAgent);

        ValueProvider<UUID, Double> valueProvider = instance.get(ValueProviderManager.class).build(map).orElseGet(() -> {
            instance.getLogger().warning("No value provider found for " + name);
            return ValueProvider.empty();
        });
        boolean isAsync = Optional.ofNullable(map.get("async"))
                .map(Object::toString)
                .map(String::toLowerCase)
                .map(Boolean::parseBoolean)
                .orElse(false);
        boolean showErrors = Optional.ofNullable(map.get("show-errors"))
                .map(Object::toString)
                .map(String::toLowerCase)
                .map(Boolean::parseBoolean)
                .orElse(false);
        boolean resetOnError = Optional.ofNullable(map.get("reset-on-error"))
                .map(Object::toString)
                .map(String::toLowerCase)
                .map(Boolean::parseBoolean)
                .orElse(true);
        List<String> ignorePermissions = CollectionUtils.createStringListFromObject(map.get("ignore-permission"), true);
        List<String> resetPermissions = CollectionUtils.createStringListFromObject(map.get("reset-permission"), true);
        this.updateAgent = new UpdateAgent<>(this, valueProvider);
        if (!ignorePermissions.isEmpty() || !resetPermissions.isEmpty()) {
            updateAgent.setFilter(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    return UpdateAgent.FilterResult.SKIP;
                }
                if (!resetPermissions.isEmpty() && resetPermissions.stream().anyMatch(player::hasPermission)) {
                    return UpdateAgent.FilterResult.RESET;
                }
                if (!ignorePermissions.isEmpty() && ignorePermissions.stream().anyMatch(player::hasPermission)) {
                    return UpdateAgent.FilterResult.SKIP;
                }
                return UpdateAgent.FilterResult.CONTINUE;
            });
        }
        if (resetOnError) {
            updateAgent.setErrorHandler((uuid, valueWrapper) -> {
                if (showErrors && valueWrapper.state == ValueWrapper.State.ERROR) {
                    instance.getLogger().log(Level.WARNING, "Error on getting value for " + name + " from " + uuid + " - " + valueWrapper.errorMessage, valueWrapper.throwable);
                }
                return ValueWrapper.handled(defaultValue);
            });
        } else if (showErrors) {
            updateAgent.setErrorHandler((uuid, valueWrapper) -> {
                if (valueWrapper.state == ValueWrapper.State.ERROR) {
                    instance.getLogger().log(Level.WARNING, "Error on getting value for " + name + " from " + uuid + " - " + valueWrapper.errorMessage, valueWrapper.throwable);
                }
            });
        }
        updateAgent.setMaxSkips(instance.get(MainConfig.class).getTaskUpdateMaxSkips());
        entryAgents.add(updateAgent);
        agents.add(new SpigotRunnableAgent(updateAgent.getUpdateRunnable(instance.get(MainConfig.class).getTaskUpdateEntryPerTick()), isAsync ? AsyncScheduler.get(instance) : GlobalScheduler.get(instance), instance.get(MainConfig.class).getTaskUpdateDelay()));
        agents.add(new SpigotRunnableAgent(updateAgent.getSetRunnable(), AsyncScheduler.get(instance), instance.get(MainConfig.class).getTaskUpdateSetDelay()));

        this.snapshotAgent = SnapshotAgent.create(this);
        boolean reverseOrder = Optional.ofNullable(map.get("reverse")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
        snapshotAgent.setComparator(reverseOrder ? Comparator.naturalOrder() : Comparator.reverseOrder());
        snapshotAgent.setFilter(entry -> entry.getValue() != null);
        agents.add(snapshotAgent);
        agents.add(new SpigotRunnableAgent(snapshotAgent, AsyncScheduler.get(instance), 20L));

        agents.add(new Agent() {
            @Override
            public void start() {
                if (instance.get(MainConfig.class).isLoadAllOfflinePlayers()) {
                    GlobalScheduler.get(instance).run(() -> {
                        for (OfflinePlayer player : instance.getServer().getOfflinePlayers()) {
                            getOrCreateEntry(player.getUniqueId());
                        }
                    });
                }
            }
        });
        entryAgents.add(new DataEntryAgent<UUID, Double>() {
            @Override
            public void onUpdate(DataEntry<UUID, Double> entry, Double oldValue, Double newValue) {
                instance.get(EntryConsumeManager.class).consume(new EntryConsumeManager.Context(
                        TopperPlugin.GROUP,
                        name,
                        entry.getKey(),
                        oldValue,
                        newValue
                ));
            }
        });
    }

    @Override
    public @Nullable Double getDefaultValue() {
        return defaultValue;
    }

    @Override
    public List<Agent> getAgents() {
        return agents;
    }

    @Override
    public List<DataEntryAgent<UUID, Double>> getEntryAgents() {
        return entryAgents;
    }

    public StorageAgent<UUID, Double> getStorageAgent() {
        return storageAgent;
    }

    public UpdateAgent<UUID, Double> getUpdateAgent() {
        return updateAgent;
    }

    public SnapshotAgent<UUID, Double> getSnapshotAgent() {
        return snapshotAgent;
    }

    public ValueDisplay getValueDisplay() {
        return valueDisplay;
    }
}
