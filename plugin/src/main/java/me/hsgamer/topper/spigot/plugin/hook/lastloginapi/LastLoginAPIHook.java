package me.hsgamer.topper.spigot.plugin.hook.lastloginapi;

import com.alessiodp.lastloginapi.api.interfaces.LastLoginAPI;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginPlayer;
import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.manager.NameProviderManager;

import java.util.UUID;

import static com.alessiodp.lastloginapi.api.LastLogin.getApi;

public class LastLoginAPIHook implements Loadable {
    private final TopperPlugin plugin;

    public LastLoginAPIHook(TopperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        plugin.get(NameProviderManager.class).setNameProvider(this::getName);
    }

    private String getName(UUID uuid) {
        LastLoginAPI api = getApi();
        LastLoginPlayer player = api.getPlayer(uuid);
        return player.getName();
    }
}
