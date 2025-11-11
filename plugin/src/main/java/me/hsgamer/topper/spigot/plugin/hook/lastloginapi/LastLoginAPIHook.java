package me.hsgamer.topper.spigot.plugin.hook.lastloginapi;

import com.alessiodp.lastloginapi.api.interfaces.LastLoginAPI;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginPlayer;
import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.template.SpigotTopTemplate;

import java.util.UUID;

import static com.alessiodp.lastloginapi.api.LastLogin.getApi;

public class LastLoginAPIHook implements Loadable {
    private final TopperPlugin plugin;
    private Runnable disableRunnable;

    public LastLoginAPIHook(TopperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        disableRunnable = plugin.get(SpigotTopTemplate.class).getNameProviderManager().addNameProvider(this::getName);
    }

    @Override
    public void disable() {
        if (disableRunnable != null) {
            disableRunnable.run();
        }
    }

    private String getName(UUID uuid) {
        LastLoginAPI api = getApi();
        LastLoginPlayer player = api.getPlayer(uuid);
        String name = player.getName();
        return name.isEmpty() ? null : name;
    }
}
