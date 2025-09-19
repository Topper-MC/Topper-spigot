package me.hsgamer.topper.spigot.plugin.format;

import java.util.Locale;

/**
 * The default format that converts numbers to K/M format
 */
public class DefaultFormat implements Format {
    private final String[] suffixes;
    private final double[] powers;
    private final int maxSuffixIndex;

    public DefaultFormat() {
        this(new String[]{"", "K", "M", "B", "T", "P", "E"});
    }

    public DefaultFormat(String[] suffixes) {
        this.suffixes = suffixes;
        this.maxSuffixIndex = suffixes.length - 1;
        this.powers = new double[suffixes.length];
        for (int i = 0; i < suffixes.length; i++) {
            powers[i] = Math.pow(1000, i);
        }
    }

    private String computeFormat(double number) {
        if (number < 1000) {
            if (number == (long) number) {
                return Long.toString((long) number);
            } else {
                if (number * 100 == (long) (number * 100)) {
                    return String.format(Locale.ENGLISH, "%.2f", number);
                } else {
                    return String.format(Locale.ENGLISH, "%.2f", number);
                }
            }
        }

        int exp = Math.min((int) (Math.log10(number) / 3), maxSuffixIndex);
        double scaled = number / powers[exp];
        if (scaled * 100 == (long) (scaled * 100)) {
            return String.format(Locale.ENGLISH, "%.2f%s", scaled, suffixes[exp]);
        } else {
            return String.format(Locale.ENGLISH, "%.2f%s", scaled, suffixes[exp]);
        }
    }

    @Override
    public String toString(double input) {
        return computeFormat(input);
    }

    @Override
    public double toDouble(String input) throws NumberFormatException {
        if (input == null || input.isEmpty()) {
            throw new NumberFormatException("Input string is null or empty");
        }

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
        }

        for (int i = maxSuffixIndex; i >= 0; i--) {
            String suffix = suffixes[i];
            if (suffix.isEmpty()) continue;
            
            if (input.endsWith(suffix)) {
                String numberPart = input.substring(0, input.length() - suffix.length());
                try {
                    return Double.parseDouble(numberPart) * powers[i];
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