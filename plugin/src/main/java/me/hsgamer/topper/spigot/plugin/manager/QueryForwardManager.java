package me.hsgamer.topper.spigot.plugin.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.query.core.QueryManager;
import me.hsgamer.topper.query.forward.QueryForward;
import me.hsgamer.topper.query.forward.QueryForwardContext;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.template.SpigotTopTemplate;
import me.hsgamer.topper.spigot.query.forward.plugin.PluginContext;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class QueryForwardManager extends QueryForward<UUID, QueryForwardManager.ForwardContext> implements Loadable {
    private final TopperPlugin plugin;

    public QueryForwardManager(TopperPlugin plugin) {
        this.plugin = plugin;
    }

    public void addQueryManager(Plugin plugin, String name, QueryManager<UUID> queryManager) {
        addContext(new ForwardContext(plugin, name, queryManager));
    }

    @Override
    public void enable() {
        addQueryManager(plugin, TopperPlugin.GROUP, plugin.get(SpigotTopTemplate.class).getTopQueryManager());
    }

    @Override
    public void disable() {
        clearContexts();
        clearForwarders();
    }

    public static final class ForwardContext implements QueryForwardContext<UUID>, PluginContext {
        public final Plugin plugin;
        public final String name;
        public final QueryManager<UUID> queryManager;

        private ForwardContext(Plugin plugin, String name, QueryManager<UUID> queryManager) {
            this.plugin = plugin;
            this.name = name;
            this.queryManager = queryManager;
        }

        @Override
        public Plugin getPlugin() {
            return plugin;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public QueryManager<UUID> getQuery() {
            return queryManager;
        }
    }
}
