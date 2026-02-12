package me.hsgamer.topper.spigot.plugin.template;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.topper.agent.update.UpdateAgent;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

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
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return UpdateAgent.FilterResult.SKIP;
        }
        if (!resetPermissions.isEmpty() && resetPermissions.stream().anyMatch(player::hasPermission)) {
            return UpdateAgent.FilterResult.RESET;
        }
        if (!ignorePermissions.isEmpty() && ignorePermissions.stream().anyMatch(player::hasPermission)) {
            return UpdateAgent.FilterResult.SKIP;
        }
        return UpdateAgent.FilterResult.CONTINUE;
    }
}
