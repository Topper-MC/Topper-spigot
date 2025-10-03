package me.hsgamer.topper.spigot.plugin.template;

import me.hsgamer.topper.template.topplayernumber.holder.display.ValueDisplay;

import java.util.Map;
import java.util.Optional;

public class SpigotValueDisplaySettings implements ValueDisplay.Settings {
    private final Map<String, Object> map;

    public SpigotValueDisplaySettings(Map<String, Object> map) {
        this.map = map;
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
}
