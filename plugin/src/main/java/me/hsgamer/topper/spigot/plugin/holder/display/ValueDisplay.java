package me.hsgamer.topper.spigot.plugin.holder.display;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.hsgamer.topper.query.display.number.NumberDisplay;
import me.hsgamer.topper.spigot.plugin.format.Format;
import me.hsgamer.topper.spigot.plugin.format.FormatManager;
import me.hsgamer.topper.spigot.plugin.holder.NumberTopHolder;

public class ValueDisplay extends NumberDisplay<UUID, Double> {
    public final String displayNullName;
    public final String displayNullUuid;
    private final String formatName;
    private final String nullValue;

    public ValueDisplay(Map<String, Object> map) {
        super(
                Optional.ofNullable(map.get("line"))
                        .map(Object::toString)
                        .orElse("&7[&b{index}&7] &b{name} &7- &b{value}"),
                Optional.ofNullable(map.get("null-value"))
                        .map(Object::toString)
                        .orElse("---")
        );
        this.nullValue = Optional.ofNullable(map.get("null-value"))
                .map(Object::toString)
                .orElse("---");
        this.displayNullName = Optional.ofNullable(map.get("null-name"))
                .map(Object::toString)
                .orElse("---");
        this.displayNullUuid = Optional.ofNullable(map.get("null-uuid"))
                .map(Object::toString)
                .orElse("---");
        this.formatName = Optional.ofNullable(map.get("format"))
                .map(Object::toString)
                .orElse("default");
    }

    @Override
    public @NotNull String getDisplayValue(@Nullable Double value, @Nullable String formatQuery) {
        if (value == null) {
            return nullValue;
        }
        Format format = formatQuery != null
                ? FormatManager.getFormat(formatQuery)
                : FormatManager.getFormat(formatName);
        return format.toString(value);
    }

    public String getNullValue() {
        return nullValue;
    }

    @Override
    public @NotNull String getDisplayKey(@Nullable UUID uuid) {
        return uuid != null ? uuid.toString() : displayNullUuid;
    }

    @Override
    public @NotNull String getDisplayName(@Nullable UUID uuid) {
        return Optional.ofNullable(uuid)
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .orElse(displayNullName);
    }

    public String getDisplayLine(int index /* 1-based */, NumberTopHolder holder) {
        return getDisplayLine(index, holder.getSnapshotAgent().getSnapshotByIndex(index - 1).orElse(null));
    }
}
