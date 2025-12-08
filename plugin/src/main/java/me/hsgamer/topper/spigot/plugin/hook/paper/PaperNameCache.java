package me.hsgamer.topper.spigot.plugin.hook.paper;

import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("JavaLangInvokeHandleSignature")
public class PaperNameCache implements Function<UUID, String> {
    private static MethodHandle createProfileMethod;
    private static MethodHandle completeFromCacheMethod;
    private static MethodHandle getNameMethod;

    static {
        try {
            MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();

            Class<?> profileClass = Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
            completeFromCacheMethod = publicLookup.findVirtual(profileClass, "completeFromCache", MethodType.methodType(boolean.class));
            getNameMethod = publicLookup.findVirtual(profileClass, "getName", MethodType.methodType(String.class));
            createProfileMethod = publicLookup.findStatic(Bukkit.class, "createProfile", MethodType.methodType(profileClass, UUID.class));
        } catch (Throwable e) {
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
            Object profile = createProfileMethod.invoke(uuid);
            completeFromCacheMethod.invoke(profile);
            String name = (String) getNameMethod.invoke(profile);
            return name != null && !name.isEmpty() ? name : null;
        } catch (Throwable e) {
            return null;
        }
    }
}
