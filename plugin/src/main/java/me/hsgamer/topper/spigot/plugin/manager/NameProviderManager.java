package me.hsgamer.topper.spigot.plugin.manager;

import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.function.Function;

public class NameProviderManager {
    private Function<UUID, String> nameProvider;

    public void setNameProvider(Function<UUID, String> nameProvider) {
        this.nameProvider = nameProvider;
    }

    public String getName(UUID uuid) {
        if (nameProvider == null) {
            nameProvider = uuid1 -> Bukkit.getOfflinePlayer(uuid).getName();
        }
        return nameProvider.apply(uuid);
    }
}
