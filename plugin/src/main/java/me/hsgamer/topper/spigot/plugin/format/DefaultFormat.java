package me.hsgamer.topper.spigot.plugin.format;

import java.util.Locale;

/**
 * The default format that converts numbers to K/M format
 */
public class DefaultFormat implements Format {
    private final String[] suffixes;

    public DefaultFormat() {
        this(new String[]{"", "K", "M", "B", "T", "P", "E"});
    }

    public DefaultFormat(String[] suffixes) {
        this.suffixes = suffixes;
    }

    private String formatNumber(double value, int maxDecimalPlaces) {
        if (value == (long) value) {
            return Long.toString((long) value);
        }

        if (value < 1.0) {
            return String.format(Locale.ENGLISH, "%.2f", value);
        } else if (value < 10.0) {
            return String.format(Locale.ENGLISH, "%.2f", value);
        } else if (value < 100.0) {
            return String.format(Locale.ENGLISH, "%.1f", value);
        } else {
            return String.format(Locale.ENGLISH, "%.0f", value);
        }
    }

    @Override
    public String toString(double input) {
        if (Double.isNaN(input) || Double.isInfinite(input)) {
            return "0";
        }
        
        boolean negative = input < 0;
        double absValue = Math.abs(input);
        
        if (absValue < 1000) {
            return (negative ? "-" : "") + formatNumber(absValue, 2);
        }

        int exp = Math.min((int) (Math.log10(absValue) / 3), suffixes.length - 1);
        double scaled = absValue / Math.pow(1000, exp);
        
        return (negative ? "-" : "") + formatNumber(scaled, 2) + suffixes[exp];
    }

    @Override
    public double toDouble(String input) throws NumberFormatException {
        if (input == null || input.isEmpty()) {
            throw new NumberFormatException("Input string is null or empty");
        }

        boolean negative = input.startsWith("-");
        String processedInput = negative ? input.substring(1) : input;
        try {
            return (negative ? -1 : 1) * Double.parseDouble(processedInput);
        } catch (NumberFormatException e) {
        }

        for (int i = suffixes.length - 1; i >= 0; i--) {
            String suffix = suffixes[i];
            if (suffix.isEmpty()) continue;
            
            if (processedInput.endsWith(suffix)) {
                String numberPart = processedInput.substring(0, processedInput.length() - suffix.length());
                try {
                    return (negative ? -1 : 1) * Double.parseDouble(numberPart) * Math.pow(1000, i);
                } catch (NumberFormatException e) {
                }
            }
        }
        
        throw new NumberFormatException("Invalid number format: " + input);
    }

    @Override
    public String getName() {
        return "default";
    }
}