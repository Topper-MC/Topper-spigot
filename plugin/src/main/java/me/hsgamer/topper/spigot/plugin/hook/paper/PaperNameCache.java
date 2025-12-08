package me.hsgamer.topper.spigot.plugin.hook.paper;

import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

public class PaperNameCache implements Function<UUID, String> {
    private static Method createProfileMethod;
    private static Method completeFromCacheMethod;
    private static Method getNameMethod;

    static {
        try {
            Class<?> profileClass = Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
            completeFromCacheMethod = profileClass.getMethod("completeFromCache");
            getNameMethod = profileClass.getMethod("getName");

            Class<?> bukkitClass = Bukkit.class;
            createProfileMethod = bukkitClass.getMethod("createProfile", UUID.class);
            Class<?> returnProfileClass = createProfileMethod.getReturnType();
            if (!profileClass.isAssignableFrom(returnProfileClass)) {
                throw new UnsupportedOperationException(returnProfileClass.getName());
            }
        } catch (Throwable ignored) {
            createProfileMethod = null;
            completeFromCacheMethod = null;
            getNameMethod = null;
        }
    }

    public static boolean isAvailable() {
        return createProfileMethod != null && completeFromCacheMethod != null && getNameMethod != null;
    }

    @Override
    public String apply(UUID uuid) {
        if (!isAvailable()) return null;

        if (Bukkit.getPlayer(uuid) != null) return null;

        try {
            Object profile = createProfileMethod.invoke(null, uuid);
            completeFromCacheMethod.invoke(profile);
            String name = (String) getNameMethod.invoke(profile);
            Bukkit.getLogger().log(Level.INFO, "Paper name: " + name);
            return name != null && !name.isEmpty() ? name : null;
        } catch (Throwable e) {
            return null;
        }
    }
}
