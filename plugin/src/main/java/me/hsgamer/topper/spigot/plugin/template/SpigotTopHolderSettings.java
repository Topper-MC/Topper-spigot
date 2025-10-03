package me.hsgamer.topper.spigot.plugin.template;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class SpigotTopHolderSettings implements NumberTopHolder.Settings {
    private final Map<String, Object> map;

    public SpigotTopHolderSettings(Map<String, Object> map) {
        this.map = map;
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
    public String defaultLine() {
        return Optional.ofNullable(map.get("line"))
                .map(Object::toString)
                .orElse("&7[&b{index}&7] &b{name} &7- &b{value}");
    }

    @Override
    public String displayNullName() {
        return Optional.ofNullable(map.get("null-name"))
                .map(Object::toString)
                .orElse("---");
    }

    @Override
    public String displayNullUuid() {
        return Optional.ofNullable(map.get("null-uuid"))
                .map(Object::toString)
                .orElse("---");
    }

    @Override
    public String displayNullValue() {
        return Optional.ofNullable(map.get("null-value"))
                .map(Object::toString)
                .orElse("---");
    }

    @Override
    public boolean async() {
        return Optional.ofNullable(map.get("async"))
                .map(Object::toString)
                .map(String::toLowerCase)
                .map(Boolean::parseBoolean)
                .orElse(false);
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
    public List<String> ignorePermissions() {
        return CollectionUtils.createStringListFromObject(map.get("ignore-permission"), true);
    }

    @Override
    public List<String> resetPermissions() {
        return CollectionUtils.createStringListFromObject(map.get("reset-permission"), true);
    }

    @Override
    public Map<String, Object> valueProvider() {
        return map;
    }

    public Map<String, Object> map() {
        return map;
    }
}
