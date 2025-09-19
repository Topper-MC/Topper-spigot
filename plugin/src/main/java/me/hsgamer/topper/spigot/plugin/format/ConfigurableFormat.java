package me.hsgamer.topper.spigot.plugin.format;

/**
 * A configurable format that extends DefaultFormat with custom suffixes.
 * This allows users to define their own suffixes for different number ranges.
 */
public class ConfigurableFormat extends DefaultFormat {
    private final String name;

    public ConfigurableFormat(String name, String[] suffixes) {
        super(suffixes);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}