package me.hsgamer.topper.spigot.plugin.format;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FormatManager {
    private static volatile ConcurrentMap<String, Format> formats;
    private static Format defaultFormat = new DefaultFormat();

    static {
        initializeFormats();
    }

    private static void initializeFormats() {
        formats = new ConcurrentHashMap<>();
        register(new DefaultFormat());
        register(new RegularFormat());
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