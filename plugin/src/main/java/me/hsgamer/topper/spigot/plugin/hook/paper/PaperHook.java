package me.hsgamer.topper.spigot.plugin.hook.paper;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.template.SpigotTopTemplate;

public class PaperHook implements Loadable {
    private final TopperPlugin plugin;
    private Runnable disableRunnable;

    public PaperHook(TopperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if (PaperNameCache.isAvailable()) {
            plugin.getLogger().info("Using PaperMC's Cache for Name Provider");
            disableRunnable = plugin.get(SpigotTopTemplate.class).getNameProviderManager().addNameProvider(new PaperNameCache());
        }
    }

    @Override
    public void disable() {
        if (disableRunnable != null) {
            disableRunnable.run();
        }
    }
}
