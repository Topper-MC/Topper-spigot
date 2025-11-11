package me.hsgamer.topper.spigot.plugin.hook.luckperms;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.manager.NameProviderManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.Optional;

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

        Runnable unregisterNameProvider = instance.get(NameProviderManager.class).addNameProvider(uuid -> Optional.ofNullable(api.getUserManager().getUser(uuid)).map(User::getUsername));

        disableRunnable = () -> {
            api.getContextManager().unregisterCalculator(contextCalculator);
            unregisterNameProvider.run();
        };
    }

    @Override
    public void disable() {
        disableRunnable.run();
    }
}
