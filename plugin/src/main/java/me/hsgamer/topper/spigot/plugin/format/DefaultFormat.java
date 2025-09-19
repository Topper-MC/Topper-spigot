package me.hsgamer.topper.spigot.plugin.format;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The default format that converts numbers to K/M format
 */
public class DefaultFormat implements Format {
    private static final String[] SUFFIXES = {"", "K", "M", "B", "T", "P", "E"};
    private static final ConcurrentMap<Long, String> PRECOMPUTED_FORMATS = new ConcurrentHashMap<>();

    static {
        precomputeFormats();
    }

    private static void precomputeFormats() {
        for (long i = 1; i <= 100_000; i++) {
            PRECOMPUTED_FORMATS.put(i, computeFormat(i));
        }
    }

    private static String computeFormat(double number) {
        if (number < 1000) {
            return (number == Math.floor(number)) ? String.format("%.0f", number) : String.format("%.2f", number);
        }

        int exp = (int) (Math.log10(number) / 3);
        if (exp >= SUFFIXES.length) {
            exp = SUFFIXES.length - 1;
        }

        double scaled = number / Math.pow(1000, exp);
        double truncated = Math.floor(scaled * 100d) / 100d;
        return String.format("%.2f%s", truncated, SUFFIXES[exp]);
    }

    private static String formatNumber(double number) {
        if (number <= 100_000) {
            long rounded = Math.round(number);
            return PRECOMPUTED_FORMATS.getOrDefault(rounded, computeFormat(number));
        }
        return computeFormat(number);
    }

    @Override
    public String toString(double input) {
        return formatNumber(input);
    }

    @Override
    public double toDouble(String input) throws NumberFormatException {
        for (int i = SUFFIXES.length - 1; i >= 0; i--) {
            if (input.endsWith(SUFFIXES[i])) {
                String numberPart = input.substring(0, input.length() - SUFFIXES[i].length());
                return Double.parseDouble(numberPart) * Math.pow(1000, i);
            }
        }
        return Double.parseDouble(input);
    }

    @Override
    public String getName() {
        return "default";
    }
}