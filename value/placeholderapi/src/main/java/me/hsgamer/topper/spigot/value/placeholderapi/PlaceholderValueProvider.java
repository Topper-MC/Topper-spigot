package me.hsgamer.topper.spigot.value.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topper.value.core.ValueWrapper;
import org.bukkit.OfflinePlayer;

import java.util.function.Consumer;

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
    public void accept(OfflinePlayer offlinePlayer, Consumer<ValueWrapper<String>> callback) {
        if (isOnlineOnly && !offlinePlayer.isOnline()) {
            callback.accept(ValueWrapper.notHandled());
            return;
        }

        String replaced;
        try {
            replaced = PlaceholderAPI.setPlaceholders(offlinePlayer, placeholder);
        } catch (Exception e) {
            callback.accept(ValueWrapper.error("Error while parsing the placeholder: " + placeholder, e));
            return;
        }

        callback.accept(placeholder.equals(replaced) ? ValueWrapper.notHandled() : ValueWrapper.handled(replaced));
    }
}
