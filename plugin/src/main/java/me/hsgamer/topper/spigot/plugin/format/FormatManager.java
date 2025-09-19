package me.hsgamer.topper.spigot.plugin.format;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;

public class FormatManager {
    private static volatile ConcurrentMap<String, Format> formats;
    private static Format defaultFormat = new DefaultFormat();

    static {
        initializeFormats();
    }

    private static MainConfig mainConfig;

    private static void initializeFormats() {
        formats = new ConcurrentHashMap<>();
        register(new DefaultFormat());
        register(new RegularFormat());
        loadCustomFormats();
    }

    /**
     * Initialize FormatManager with the plugin's MainConfig
     *
     * @param config the MainConfig instance
     */
    public static void init(MainConfig config) {
        mainConfig = config;
        initializeFormats();
    }

    private static void loadCustomFormats() {
        if (mainConfig == null) return;
        
        mainConfig.getCustomFormats().forEach((name, settings) -> {
            List<String> suffixes = Optional.ofNullable(settings.get("suffixes"))
                    .map(CollectionUtils::createStringListFromObject)
                    .orElse(Collections.emptyList());

            if (!suffixes.isEmpty()) {
                register(new ConfigurableFormat(name, suffixes.toArray(new String[0])));
            }
        });
    }

    /**
     * Register a new format
     *
     * @param format the format
     */
    public static void register(Format format) {
        formats.put(format.getName().toLowerCase(), format);
    }

    /**
     * Register formats in parallel
     *
     * @param formatsToRegister the formats to register
     */
    public static void registerAllParallel(Map<String, Format> formatsToRegister) {
        formatsToRegister.entrySet().parallelStream().forEach(entry ->
            formats.put(entry.getKey().toLowerCase(), entry.getValue())
        );
    }

    /**
     * Get a format by name
     *
     * @param name the name
     * @return the format
     */
    public static Format getFormat(String name) {
        return formats.getOrDefault(name.toLowerCase(), defaultFormat);
    }

    /**
     * Set the default format
     *
     * @param format the format
     */
    public static void setDefaultFormat(Format format) {
        defaultFormat = format;
    }

    /**
     * Get the default format
     *
     * @return the default format
     */
    public static Format getDefaultFormat() {
        return defaultFormat;
    }

    /**
     * Get all available formats
     *
     * @return the formats
     */
    public static Map<String, Format> getFormats() {
        return formats;
    }
}