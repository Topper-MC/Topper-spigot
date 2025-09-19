package me.hsgamer.topper.spigot.plugin.format;

import java.text.DecimalFormat;

public class RegularFormat implements Format {
    private static final DecimalFormat FORMAT = new DecimalFormat("#,###.##");

    @Override
    public double toDouble(String input) throws NumberFormatException {
        try {
            input = input.replaceAll("[,\\s]", "");
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid number format: " + input);
        }
    }

    @Override
    public String toString(double input) {
        return FORMAT.format(input);
    }

    @Override
    public String getName() {
        return "Regular";
    }
}