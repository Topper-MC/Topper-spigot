package me.hsgamer.topper.spigot.plugin.manager;

import io.github.projectunified.faststats.bukkit.BukkitPlatform;
import io.github.projectunified.faststats.core.Metric;
import io.github.projectunified.faststats.gson.GsonSerializer;
import io.github.projectunified.faststats.net.NetSubmitter;
import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.template.SpigotTopTemplate;
import org.bstats.charts.SingleLineChart;

public class MetricsManager implements Loadable {
    private final TopperPlugin plugin;
    private org.bstats.bukkit.Metrics bstatsMetrics;
    private io.github.projectunified.faststats.core.Metrics fastStatsMetrics;

    public MetricsManager(TopperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        bstatsMetrics = new org.bstats.bukkit.Metrics(plugin, 14938);
        bstatsMetrics.addCustomChart(new SingleLineChart("holders", () -> plugin.get(SpigotTopTemplate.class).getTopManager().getHolderNames().size()));

        fastStatsMetrics = io.github.projectunified.faststats.core.Metrics.builder()
                .platform(new BukkitPlatform(plugin))
                .serializer(new GsonSerializer())
                .submitter(new NetSubmitter("314aeec477ff85ca7e547c506bebf24b"))
                .addMetric(Metric.number("holders", () -> plugin.get(SpigotTopTemplate.class).getTopManager().getHolderNames().size()))
                .build();
        fastStatsMetrics.start();
    }

    @Override
    public void disable() {
        bstatsMetrics.shutdown();
        fastStatsMetrics.shutdown();
    }
}
