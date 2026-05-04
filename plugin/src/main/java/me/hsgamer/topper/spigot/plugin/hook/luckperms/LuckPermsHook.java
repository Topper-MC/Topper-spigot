package me.hsgamer.topper.spigot.plugin.hook.luckperms;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.hook.HookReloadable;
import me.hsgamer.topper.spigot.plugin.manager.PermissionCheckManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.User;

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
        Runnable removePermissionCheck = instance.get(PermissionCheckManager.class).addCheck((uuid, permission) -> {
            User user = api.getUserManager().getUser(uuid);
            if (user == null) {
                return PermissionCheckManager.State.UNKNOWN;
            }
            CachedPermissionData permissionData = user.getCachedData().getPermissionData();
            return permissionData.checkPermission(permission).asBoolean() ? PermissionCheckManager.State.TRUE : PermissionCheckManager.State.FALSE;
        });
        disableRunnable = () -> {
            api.getContextManager().unregisterCalculator(contextCalculator);
            removePermissionCheck.run();
        };
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
