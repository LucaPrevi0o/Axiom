package lib.rendering.pipeline;

import lib.util.FormattingUtils;

/**
 * Utility class for calculating nice tick spacing and formatting labels
 * @deprecated Use FormattingUtils instead
 */
@Deprecated
public class TickCalculator {
    
    /**
     * Calculate a "nice" step size for tick marks
     * @param range The range of values to display
     * @param targetTicks Approximate number of ticks desired
     * @return Nice step size
     * @deprecated Use {@link FormattingUtils#calculateNiceStep(double, int)} instead
     */
    @Deprecated
    public static double calculateNiceStep(double range, int targetTicks) {
        return FormattingUtils.calculateNiceStep(range, targetTicks);
    }
    
    /**
     * Format a tick label with appropriate precision
     * @param value The value to format
     * @param step The step size (used to determine precision)
     * @return Formatted label string
     * @deprecated Use {@link FormattingUtils#formatTickLabel(double, double)} instead
     */
    @Deprecated
    public static String formatTickLabel(double value, double step) {
        return FormattingUtils.formatTickLabel(value, step);
    }
}
