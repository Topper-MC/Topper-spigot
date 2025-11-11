package me.hsgamer.topper.spigot.plugin.hook.luckperms;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public class LuckPermsHook implements Loadable {
    private final TopperPlugin instance;
    private Runnable disableRunnable;

    public LuckPermsHook(TopperPlugin instance) {
        this.instance = instance;
    }

    @Override
    public void enable() {
        LuckPerms api = LuckPermsProvider.get();
        TopContextCalculator contextCalculator = new TopContextCalculator(instance);
        api.getContextManager().registerCalculator(contextCalculator);
        disableRunnable = () -> api.getContextManager().unregisterCalculator(contextCalculator);
    }

    @Override
    public void disable() {
        disableRunnable.run();
    }
}
