package me.hsgamer.topper.spigot.plugin.format;

import java.text.DecimalFormat;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * The default format that converts numbers to K/M format
 */
public class DefaultFormat implements Format {
    private static final NavigableMap<Long, String> SUFFIXES = new TreeMap<>();
    static {
        SUFFIXES.put(1_000L, "k");
        SUFFIXES.put(1_000_000L, "M");
        SUFFIXES.put(1_000_000_000L, "B");
        SUFFIXES.put(1_000_000_000_000L, "T");
        SUFFIXES.put(1_000_000_000_000_000L, "Q");
        SUFFIXES.put(1_000_000_000_000_000_000L, "E"); // Add just in case of extremely large numbers
    }

    private static final DecimalFormat FORMAT = new DecimalFormat("#,###.##");

    @Override
    public double toDouble(String input) throws NumberFormatException {
        try {
            input = input.replaceAll("[,\\s]", "");
            String upperInput = input.toUpperCase();
            if (upperInput.endsWith("K")) {
                return Double.parseDouble(input.substring(0, input.length() - 1)) * 1000;
            } else if (upperInput.endsWith("M")) {
                return Double.parseDouble(input.substring(0, input.length() - 1)) * 1_000_000;
            } else if (upperInput.endsWith("B")) {
                return Double.parseDouble(input.substring(0, input.length() - 1)) * 1_000_000_000;
            }
            
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid number format: " + input);
        }
    }

    @Override
    public String toString(double input) {
        if (input < 0) {
            return "-" + toString(-input);
        }

        Long divider = SUFFIXES.floorKey((long) input);
        if (divider == null) {
            return FORMAT.format(input);
        }

        double value = input / divider;
        return FORMAT.format(value) + SUFFIXES.get(divider);
    }

    @Override
    public String getName() {
        return "Default";
    }
}