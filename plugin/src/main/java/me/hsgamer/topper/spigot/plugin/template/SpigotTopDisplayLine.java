package me.hsgamer.topper.spigot.plugin.template;

import me.hsgamer.topper.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.query.simple.SimpleQueryDisplay;
import me.hsgamer.topper.template.snapshotdisplayline.SnapshotDisplayLine;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;

import java.util.Optional;
import java.util.UUID;

public class SpigotTopDisplayLine implements SnapshotDisplayLine<UUID, Double> {
    private final NumberTopHolder holder;

    public SpigotTopDisplayLine(NumberTopHolder holder) {
        this.holder = holder;
    }

    @Override
    public SimpleQueryDisplay<UUID, Double> getDisplay() {
        return holder.getValueDisplay();
    }

    @Override
    public SnapshotAgent<UUID, Double> getSnapshotAgent() {
        return holder.getSnapshotAgent();
    }

    @Override
    public String getDisplayLine() {
        return Optional.of(holder.getSettings())
                .filter(SpigotTopHolderSettings.class::isInstance)
                .map(SpigotTopHolderSettings.class::cast)
                .map(SpigotTopHolderSettings::defaultLine)
                .orElse("&7[&b{index}&7] &b{name} &7- &b{value}");
    }
}
