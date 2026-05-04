package me.hsgamer.topper.spigot.plugin.template;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.topper.agent.update.UpdateAgent;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.manager.PermissionCheckManager;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class SpigotTopHolderSettings extends NumberTopHolder.MapSettings {
    private final List<String> ignorePermissions;
    private final List<String> resetPermissions;

    public SpigotTopHolderSettings(Map<String, Object> map) {
        super(map);
        ignorePermissions = CollectionUtils.createStringListFromObject(map.get("ignore-permission"), true);
        resetPermissions = CollectionUtils.createStringListFromObject(map.get("reset-permission"), true);
    }

    public String defaultLine() {
        return Objects.toString(map.get("line"), null);
    }

    @Override
    public UpdateAgent.FilterResult filter(UUID uuid) {
        if (ignorePermissions.isEmpty() && resetPermissions.isEmpty()) {
            return UpdateAgent.FilterResult.CONTINUE;
        }

        PermissionCheckManager permissionCheckManager = JavaPlugin.getPlugin(TopperPlugin.class).get(PermissionCheckManager.class);

        Set<PermissionCheckManager.State> resetStates = resetPermissions.stream().map(permission -> permissionCheckManager.apply(uuid, permission)).collect(Collectors.toSet());
        if (resetStates.contains(PermissionCheckManager.State.TRUE)) {
            return UpdateAgent.FilterResult.RESET;
        }

        Set<PermissionCheckManager.State> ignoreStates = ignorePermissions.stream().map(permission -> permissionCheckManager.apply(uuid, permission)).collect(Collectors.toSet());
        if (ignoreStates.contains(PermissionCheckManager.State.TRUE)) {
            return UpdateAgent.FilterResult.SKIP;
        }

        if (!resetStates.contains(PermissionCheckManager.State.FALSE) && !ignoreStates.contains(PermissionCheckManager.State.FALSE)) {
            return UpdateAgent.FilterResult.SKIP;
        }

        return UpdateAgent.FilterResult.CONTINUE;
    }
}
