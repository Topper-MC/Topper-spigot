package me.hsgamer.topper.spigot.plugin.listener;

import io.github.projectunified.minelib.plugin.listener.ListenerComponent;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.template.SpigotTopTemplate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements ListenerComponent {
    private final TopperPlugin instance;

    public JoinListener(TopperPlugin instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        instance.get(SpigotTopTemplate.class).getTopManager().create(event.getPlayer().getUniqueId());
    }
}
