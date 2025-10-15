package me.hsgamer.topper.spigot.plugin.template;

import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.agent.core.DataEntryAgent;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.spigot.plugin.event.GenericEntryUpdateEvent;
import me.hsgamer.topper.spigot.plugin.manager.ValueProviderManager;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.template.topplayernumber.TopPlayerNumberTemplate;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;
import me.hsgamer.topper.value.core.ValueProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

public class SpigotTopTemplate extends TopPlayerNumberTemplate implements Loadable {
    private final TopperPlugin plugin;

    public SpigotTopTemplate(TopperPlugin plugin) {
        super(new SpigotTopTemplateSettings(plugin));
        this.plugin = plugin;
    }

    @Override
    public Function<String, DataStorage<UUID, Double>> getStorageSupplier() {
        return plugin.get(SpigotStorageSupplierTemplate.class).getNumberStorageSupplier();
    }

    @Override
    public Optional<ValueProvider<UUID, Double>> createValueProvider(Map<String, Object> settings) {
        return plugin.get(ValueProviderManager.class).build(settings);
    }

    @Override
    public boolean isOnline(UUID uuid) {
        return Bukkit.getPlayer(uuid) != null;
    }

    @Override
    public String getName(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return player.hasPermission(permission);
        }
        return false;
    }

    @Override
    public Agent createTaskAgent(Runnable runnable, boolean async, long delay) {
        return new SpigotRunnableAgent(runnable, async ? AsyncScheduler.get(plugin) : GlobalScheduler.get(plugin), delay);
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
                }
            }
        });
    }

    @Override
    public void enable() {
        super.enable();
        getEntryConsumeManager().addConsumer((context) -> Bukkit.getPluginManager().callEvent(new GenericEntryUpdateEvent(
                context.group,
                context.holder,
                context.uuid,
                context.oldValue,
                context.value,
                true
        )));
    }
}
