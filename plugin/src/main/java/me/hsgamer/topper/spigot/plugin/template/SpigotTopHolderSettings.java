package me.hsgamer.topper.spigot.plugin.template;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.topper.agent.update.UpdateAgent;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;
import me.hsgamer.topper.template.topplayernumber.holder.display.ValueDisplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class SpigotTopHolderSettings implements NumberTopHolder.Settings {
    private final Map<String, Object> map;
    private final SpigotValueDisplaySettings valueDisplaySettings;
    private final List<String> ignorePermissions;
    private final List<String> resetPermissions;

    public SpigotTopHolderSettings(Map<String, Object> map) {
        this.map = map;
        this.valueDisplaySettings = new SpigotValueDisplaySettings(map);
        ignorePermissions = CollectionUtils.createStringListFromObject(map.get("ignore-permission"), true);
        resetPermissions = CollectionUtils.createStringListFromObject(map.get("reset-permission"), true);
    }

    @Override
    public Double defaultValue() {
        return Optional.ofNullable(map.get("default-value"))
                .map(Object::toString)
                .map(s -> {
                    try {
                        return Double.parseDouble(s);
                    } catch (NumberFormatException e) {
                        JavaPlugin.getProvidingPlugin(getClass()).getLogger().log(Level.WARNING, "Invalid default value: " + s + ". Fallback to null", e);
                        return null;
                    }
                })
                .orElse(null);
    }

    @Override
    public ValueDisplay.Settings displaySettings() {
        return valueDisplaySettings;
    }

    @Override
    public boolean showErrors() {
        return Optional.ofNullable(map.get("show-errors"))
                .map(Object::toString)
                .map(String::toLowerCase)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    @Override
    public boolean resetOnError() {
        return Optional.ofNullable(map.get("reset-on-error"))
                .map(Object::toString)
                .map(String::toLowerCase)
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    @Override
    public boolean reverse() {
        return Optional.ofNullable(map.get("reverse"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    @Override
    public UpdateAgent.FilterResult filter(UUID uuid) {
        if (ignorePermissions.isEmpty() && resetPermissions.isEmpty()) {
            return UpdateAgent.FilterResult.CONTINUE;
        }
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
    }

    @Override
    public Map<String, Object> valueProvider() {
        return map;
    }

    public Map<String, Object> map() {
        return map;
    }
}
