package me.hsgamer.topper.spigot.value.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topper.value.core.ValueWrapper;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderValueProvider implements ValueProvider<OfflinePlayer, String> {
    private final String placeholder;
    private final boolean isOnlineOnly;

    public PlaceholderValueProvider(String placeholder, boolean isOnlineOnly) {
        this.placeholder = normalizePlaceholder(placeholder);
        this.isOnlineOnly = isOnlineOnly;
    }

    private static String normalizePlaceholder(String placeholder) {
        return placeholder.isEmpty() || placeholder.startsWith("%") || placeholder.endsWith("%")
                ? placeholder
                : "%" + placeholder + "%";
    }

    @Override
    public @NotNull ValueWrapper<String> apply(@NotNull OfflinePlayer player) {
        if (isOnlineOnly && !player.isOnline()) {
            return ValueWrapper.notHandled();
        }

        String replaced;
        try {
            replaced = PlaceholderAPI.setPlaceholders(player, placeholder);
        } catch (Exception e) {
            return ValueWrapper.error("Error while parsing the placeholder: " + placeholder, e);
        }

        if (placeholder.equals(replaced)) {
            return ValueWrapper.notHandled();
        }

        return ValueWrapper.handled(replaced);
    }
}
