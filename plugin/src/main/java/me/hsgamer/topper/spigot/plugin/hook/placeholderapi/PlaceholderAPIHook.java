package me.hsgamer.topper.spigot.plugin.hook.placeholderapi;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.query.forward.QueryForwardContext;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.manager.ValueProviderManager;
import me.hsgamer.topper.spigot.plugin.template.SpigotTopTemplate;
import me.hsgamer.topper.spigot.plugin.util.ParseUtil;
import me.hsgamer.topper.spigot.query.forward.placeholderapi.PlaceholderQueryForwarder;
import me.hsgamer.topper.spigot.value.placeholderapi.PlaceholderValueProvider;
import me.hsgamer.topper.value.string.StringDeformatters;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class PlaceholderAPIHook implements Loadable {
    private final TopperPlugin plugin;
    private final PlaceholderQueryForwarder<QueryForwardContext<UUID>> queryForwarder;

    public PlaceholderAPIHook(TopperPlugin plugin) {
        this.plugin = plugin;
        this.queryForwarder = new PlaceholderQueryForwarder<>(plugin);
    }

    @Override
    public void load() {
        plugin.get(ValueProviderManager.class).register(map -> {
            String placeholder = Optional.ofNullable(map.get("placeholder")).map(Object::toString).orElse("");
            boolean isOnlineOnly = Optional.ofNullable(map.get("online"))
                    .map(Object::toString)
                    .map(String::toLowerCase)
                    .map(Boolean::parseBoolean)
                    .orElse(true);
            return new PlaceholderValueProvider(placeholder, isOnlineOnly)
                    .thenApply(StringDeformatters.deformatterOrIdentity(map))
                    .thenApply(ParseUtil::parsePlaceholderNumber)
                    .beforeApply(Bukkit::getOfflinePlayer);
        }, "placeholderapi", "placeholder", "papi");
        plugin.get(SpigotTopTemplate.class).getQueryForwardManager().addForwarder(queryForwarder);
    }

    @Override
    public void disable() {
        queryForwarder.unregister();
    }
}
