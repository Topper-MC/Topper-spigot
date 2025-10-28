package me.hsgamer.topper.spigot.plugin.hook.luckperms;

import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.template.SpigotTopTemplate;
import me.hsgamer.topper.template.topplayernumber.manager.EntryConsumeManager;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class TopContextCalculator implements ContextCalculator<Player> {
    private final TopperPlugin plugin;

    public TopContextCalculator(TopperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void calculate(@NotNull Player player, @NotNull ContextConsumer contextConsumer) {
        EntryConsumeManager entryConsumeManager = plugin.get(SpigotTopTemplate.class).getEntryConsumeManager();
        for (Map.Entry<String, EntryConsumeManager.Provider> entry : entryConsumeManager.getProviderMap().entrySet()) {
            String group = entry.getKey();
            EntryConsumeManager.Provider provider = entry.getValue();
            for (String holder : provider.getHolders()) {
                Optional<Integer> snapshotIndex = provider.getSnapshotIndex(holder, player.getUniqueId()).filter(i -> i >= 0);
                snapshotIndex.ifPresent(index -> contextConsumer.accept(
                        // <group>_rank_<holder>
                        String.join("_",
                                group,
                                "rank",
                                holder
                        ),
                        Integer.toString(index + 1)
                ));
            }
        }
    }

    @Override
    public @NotNull ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        EntryConsumeManager entryConsumeManager = plugin.get(SpigotTopTemplate.class).getEntryConsumeManager();
        for (Map.Entry<String, EntryConsumeManager.Provider> entry : entryConsumeManager.getProviderMap().entrySet()) {
            String group = entry.getKey();
            EntryConsumeManager.Provider provider = entry.getValue();
            for (String holder : provider.getHolders()) {
                for (int index = 0; index < 10; index++) {
                    builder.add(
                            // <group>_rank_<holder>
                            String.join("_",
                                    group,
                                    "rank",
                                    holder
                            ),
                            Integer.toString(index + 1)
                    );
                }
            }
        }
        return builder.build();
    }
}
