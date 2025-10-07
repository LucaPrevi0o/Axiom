package lib.graph.rendering;

import java.util.Locale;

/**
 * Utility class for calculating nice tick spacing and formatting labels
 */
public class TickCalculator {
    
    /**
     * Calculate a "nice" step size for tick marks
     * @param range The range of values to display
     * @param targetTicks Approximate number of ticks desired
     * @return Nice step size
     */
    public static double calculateNiceStep(double range, int targetTicks) {
        if (range <= 0) return 1.0;
        
        double rawStep = range / Math.max(1, targetTicks);
        double exponent = Math.floor(Math.log10(rawStep));
        double base = Math.pow(10, exponent);
        double fraction = rawStep / base;
        
        double niceFraction;
        if (fraction <= 1.0) niceFraction = 1.0;
        else if (fraction <= 2.0) niceFraction = 2.0;
        else if (fraction <= 5.0) niceFraction = 5.0;
        else niceFraction = 10.0;
        
        return niceFraction * base;
    }
    
    /**
     * Format a tick label with appropriate precision
     * @param value The value to format
     * @param step The step size (used to determine precision)
     * @return Formatted label string
     */
    public static String formatTickLabel(double value, double step) {
        double absStep = Math.abs(step);
        
        if (absStep >= 1.0) {
            // Use integer formatting for large steps
            long rounded = Math.round(value);
            return String.valueOf(rounded);
        } else {
            // Use decimal formatting for small steps
            int precision = (int) Math.max(0, Math.min(6, -Math.floor(Math.log10(absStep)) + 1));
            return String.format(Locale.US, "%." + precision + "f", value);
        }
    }
}
