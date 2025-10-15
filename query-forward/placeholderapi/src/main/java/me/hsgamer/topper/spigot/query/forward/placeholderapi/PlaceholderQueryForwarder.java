package me.hsgamer.topper.spigot.query.forward.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.topper.query.forward.QueryForwardContext;
import me.hsgamer.topper.spigot.query.forward.plugin.PluginContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlaceholderQueryForwarder<C extends QueryForwardContext<UUID>> implements Consumer<C> {
    private final List<PlaceholderExpansion> expansions = new ArrayList<>();
    private final Plugin defaultPlugin;

    public PlaceholderQueryForwarder(Plugin defaultPlugin) {
        this.defaultPlugin = defaultPlugin;
    }

    @Override
    public void accept(C context) {
        Plugin plugin = context instanceof PluginContext ? ((PluginContext) context).getPlugin() : defaultPlugin;
        PlaceholderExpansion expansion = new PlaceholderExpansion() {
            @Override
            public @NotNull String getIdentifier() {
                return context.getName();
            }

            @Override
            public @NotNull String getAuthor() {
                return String.join(", ", plugin.getDescription().getAuthors());
            }

            @Override
            public @NotNull String getVersion() {
                return plugin.getDescription().getVersion();
            }

            @Override
            public boolean persist() {
                return true;
            }

            @Override
            public String onRequest(OfflinePlayer player, @NotNull String params) {
                return context.getQuery().apply(player != null ? player.getUniqueId() : null, params).result;
            }
        };
        expansion.register();
        expansions.add(expansion);
    }

    public void unregister() {
        expansions.forEach(PlaceholderExpansion::unregister);
        expansions.clear();
    }
}
