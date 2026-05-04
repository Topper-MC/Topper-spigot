package me.hsgamer.topper.spigot.plugin.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class PermissionCheckManager implements Loadable, BiFunction<UUID, String, PermissionCheckManager.State> {
    private final Set<BiFunction<UUID, String, State>> checks = new HashSet<>();

    public Runnable addCheck(BiFunction<UUID, String, State> check) {
        checks.add(check);
        return () -> checks.remove(check);
    }

    @Override
    public void enable() {
        checks.add((uuid, permission) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return State.UNKNOWN;
            }
            return player.hasPermission(permission) ? State.TRUE : State.FALSE;
        });
    }

    @Override
    public void disable() {
        checks.clear();
    }

    @Override
    public State apply(UUID uuid, String permission) {
        Set<State> states = checks.stream().map(check -> check.apply(uuid, permission)).collect(Collectors.toSet());
        if (states.contains(State.TRUE)) {
            return State.TRUE;
        }
        if (states.contains(State.FALSE)) {
            return State.FALSE;
        }
        return State.UNKNOWN;
    }

    public enum State {
        TRUE,
        FALSE,
        UNKNOWN
    }
}
