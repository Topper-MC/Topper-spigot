package me.hsgamer.topper.spigot.plugin.hook.luckperms;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.hook.HookReloadable;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public class LuckPermsHook implements Loadable, HookReloadable {
    private final TopperPlugin instance;
    private Runnable disableRunnable;
    private Runnable reloadRunnable;

    public LuckPermsHook(TopperPlugin instance) {
        this.instance = instance;
    }

    @Override
    public void enable() {
        LuckPerms api = LuckPermsProvider.get();
        TopContextCalculator contextCalculator = new TopContextCalculator(instance);
        api.getContextManager().registerCalculator(contextCalculator);
        disableRunnable = () -> api.getContextManager().unregisterCalculator(contextCalculator);
        reloadRunnable = contextCalculator::clearCache;
    }

    @Override
    public void disable() {
        if (disableRunnable != null) {
            disableRunnable.run();
            disableRunnable = null;
        }
    }

    @Override
    public void reload() {
        if (reloadRunnable != null) {
            reloadRunnable.run();
        }
    }
}
