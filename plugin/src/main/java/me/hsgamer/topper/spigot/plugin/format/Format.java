package me.hsgamer.topper.spigot.plugin.format;

public interface Format {
    /**
     * Convert a string input to a double
     *
     * @param input The input string
     * @return The double value
     * @throws NumberFormatException if the input is invalid
     */
    double toDouble(String input) throws NumberFormatException;

    /**
     * Convert a double value to a formatted string
     *
     * @param input The input double
     * @return The formatted string
     */
    String toString(double input);

    /**
     * Get the name of the format
     *
     * @return The name
     */
    String getName();
}