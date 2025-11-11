package me.hsgamer.topper.spigot.plugin.manager;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class NameProviderManager {
    private final List<Function<UUID, Optional<String>>> nameProviders = new ArrayList<>();

    public Runnable addNameProvider(Function<UUID, Optional<String>> nameProvider) {
        nameProviders.add(nameProvider);
        return () -> nameProviders.remove(nameProvider);
    }

    public String getName(UUID uuid) {
        return nameProviders.stream()
                .map(provider -> provider.apply(uuid))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseGet(() -> Bukkit.getOfflinePlayer(uuid).getName());
    }
}
