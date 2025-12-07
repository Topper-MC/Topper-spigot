package me.hsgamer.topper.spigot.plugin.hook.paper;

import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class PaperNameCache implements Function<UUID, String> {
    private static final Function<UUID, Optional<?>> GET_PROFILE_FUNCTION;
    private static final Function<Object, String> GET_NAME_FROM_PROFILE_FUNCTION;

    static {
        Function<UUID, Optional<?>> getProfileFunction = null;
        Function<Object, String> getNameFromProfileFunction = null;
        try {
            Object server = Bukkit.getServer();
            Class<?> serverClass = server.getClass();

            Method getDedicatedServerMethod = serverClass.getMethod("getServer");
            Object dedicatedServer = getDedicatedServerMethod.invoke(server);
            Class<?> dedicatedServerClass = dedicatedServer.getClass();

            Object nameToIdCache;
            Class<?> nameToIdCacheClass;
            try {
                Method servicesMethod = dedicatedServerClass.getMethod("services");
                Object services = servicesMethod.invoke(dedicatedServer);
                Class<?> serviceClass = services.getClass();
                Method nameToIdCacheMethod = serviceClass.getMethod("nameToIdCache");
                nameToIdCache = nameToIdCacheMethod.invoke(services);
            } catch (Throwable e) {
                Method nameToIdCacheMethod = dedicatedServerClass.getMethod("getProfileCache");
                nameToIdCache = nameToIdCacheMethod.invoke(dedicatedServer);
            }
            Object finalNameToIdCache = nameToIdCache;
            nameToIdCacheClass = nameToIdCache.getClass();

            Method getByIdMethod = nameToIdCacheClass.getMethod("get", UUID.class);
            getProfileFunction = uuid -> {
                try {
                    Object optionalProfile = getByIdMethod.invoke(finalNameToIdCache, uuid);
                    if (optionalProfile instanceof Optional<?>) {
                        return (Optional<?>) optionalProfile;
                    } else {
                        return Optional.empty();
                    }
                } catch (Throwable e) {
                    return Optional.empty();
                }
            };

            try {
                Class<?> nameAndIdClass = Class.forName("net.minecraft.server.players.NameAndId");
                Method nameMethod = nameAndIdClass.getMethod("name");
                getNameFromProfileFunction = profile -> {
                    try {
                        Object name = nameMethod.invoke(profile);
                        if (name instanceof String) {
                            return (String) name;
                        } else {
                            return null;
                        }
                    } catch (Throwable e) {
                        return null;
                    }
                };
            } catch (Throwable e) {
                Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
                Method getNameMethod = gameProfileClass.getMethod("getName");
                getNameFromProfileFunction = profile -> {
                    try {
                        Object name = getNameMethod.invoke(profile);
                        if (name instanceof String) {
                            return (String) name;
                        } else {
                            return null;
                        }
                    } catch (Throwable ignored) {
                        return null;
                    }
                };
            }
        } catch (Throwable ignored) {
            // IGNORED
        }
        GET_PROFILE_FUNCTION = getProfileFunction;
        GET_NAME_FROM_PROFILE_FUNCTION = getNameFromProfileFunction;
    }

    public static boolean isAvailable() {
        return GET_PROFILE_FUNCTION != null && GET_NAME_FROM_PROFILE_FUNCTION != null;
    }

    @Override
    public String apply(UUID uuid) {
        if (!isAvailable()) return null;
        Optional<?> optionalProfile = GET_PROFILE_FUNCTION.apply(uuid);
        if (!optionalProfile.isPresent()) return null;
        Object profile = optionalProfile.get();
        String name = GET_NAME_FROM_PROFILE_FUNCTION.apply(profile);
        return name != null && !name.isEmpty() ? name : null;
    }
}
