package me.hsgamer.topper.spigot.plugin.format;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FormatManager {
    private static final Map<String, Format> formats = new ConcurrentHashMap<>();
    private static Format defaultFormat = new DefaultFormat();

    static {
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