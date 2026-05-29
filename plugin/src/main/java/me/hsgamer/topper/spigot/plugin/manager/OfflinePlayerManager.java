package me.hsgamer.topper.spigot.plugin.manager;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.projectunified.minelib.plugin.base.Loadable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A manager that caches {@link OfflinePlayer} lookups using Caffeine to avoid
 * repeated (and potentially blocking) calls to {@link Bukkit#getOfflinePlayer(UUID)}.
 */
public class OfflinePlayerManager implements Loadable {
    private final LoadingCache<UUID, OfflinePlayer> cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build((UUID uuid) -> Bukkit.getOfflinePlayer(uuid));

    /**
     * Get the offline player from the cache, loading (and blocking) it if it is not cached yet.
     *
     * @param uuid the unique id of the player
     * @return the offline player
     */
    @NotNull
    public OfflinePlayer getOfflinePlayer(@NotNull UUID uuid) {
        return cache.get(uuid);
    }

    /**
     * Get the offline player only if it is already cached, without triggering a blocking lookup.
     *
     * @param uuid the unique id of the player
     * @return the cached offline player, or {@code null} if it is not cached
     */
    @Nullable
    public OfflinePlayer getOfflinePlayerIfCached(@NotNull UUID uuid) {
        return cache.getIfPresent(uuid);
    }

    /**
     * Put an offline player into the cache.
     *
     * @param player the offline player
     */
    public void cache(@Nullable OfflinePlayer player) {
        if (player != null) {
            cache.put(player.getUniqueId(), player);
        }
    }

    @Override
    public void disable() {
        cache.invalidateAll();
    }
}

