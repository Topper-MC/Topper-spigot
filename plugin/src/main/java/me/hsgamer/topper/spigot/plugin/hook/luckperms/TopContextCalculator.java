package me.hsgamer.topper.spigot.plugin.hook.luckperms;

import me.hsgamer.hscore.common.CachedValue;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.template.SpigotTopTemplate;
import me.hsgamer.topper.template.topplayernumber.manager.EntryConsumeManager;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TopContextCalculator implements ContextCalculator<Player> {
    private final CachedValue<ContextCache> cache;

    public TopContextCalculator(TopperPlugin plugin) {
        this.cache = new CachedValue<ContextCache>() {
            @Override
            public ContextCache generate() {
                Map<String, EntryConsumeManager.Provider> providerMap = plugin.get(SpigotTopTemplate.class).getEntryConsumeManager().getProviderMap();
                List<HolderContext> holderContexts = new ArrayList<>();
                ImmutableContextSet.Builder potentialContextBuilder = ImmutableContextSet.builder();
                for (Map.Entry<String, EntryConsumeManager.Provider> entry : providerMap.entrySet()) {
                    String group = entry.getKey();
                    EntryConsumeManager.Provider provider = entry.getValue();
                    for (String holder : provider.getHolders()) {
                        String contextKey = String.join("_", group, "rank", holder);
                        holderContexts.add(new HolderContext(provider, holder, contextKey));
                        for (int i = 1; i <= 10; i++) {
                            potentialContextBuilder.add(contextKey, Integer.toString(i));
                        }
                    }
                }
                return new ContextCache(holderContexts, potentialContextBuilder.build());
            }
        };
    }

    public void clearCache() {
        this.cache.clearCache();
    }

    @Override
    public void calculate(@NotNull Player player, @NotNull ContextConsumer contextConsumer) {
        for (HolderContext holderContext : cache.get().holderContexts) {
            Optional<Integer> snapshotIndex = holderContext.provider.getSnapshotIndex(holderContext.holder, player.getUniqueId()).filter(i -> i >= 0);
            snapshotIndex.ifPresent(index -> contextConsumer.accept(
                    holderContext.contextKey,
                    Integer.toString(index + 1)
            ));
        }
    }

    @Override
    public @NotNull ContextSet estimatePotentialContexts() {
        return cache.get().potentialContexts;
    }

    private static class HolderContext {
        final EntryConsumeManager.Provider provider;
        final String holder;
        final String contextKey;

        HolderContext(EntryConsumeManager.Provider provider, String holder, String contextKey) {
            this.provider = provider;
            this.holder = holder;
            this.contextKey = contextKey;
        }
    }

    private static class ContextCache {
        final List<HolderContext> holderContexts;
        final ContextSet potentialContexts;

        ContextCache(List<HolderContext> holderContexts, ContextSet potentialContexts) {
            this.holderContexts = holderContexts;
            this.potentialContexts = potentialContexts;
        }
    }
}
