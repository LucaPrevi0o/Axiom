package lib.util;

/**
 * Utility methods for validating numerical values
 */
public final class ValidationUtils {
    
    // Prevent instantiation
    private ValidationUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }
    
    /**
     * Check if a value is valid (not NaN or infinite)
     * @param value The value to check
     * @return true if the value is finite and not NaN
     */
    public static boolean isValidValue(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }
    
    /**
     * Check if all values in an array are valid
     * @param values The values to check
     * @return true if all values are finite and not NaN
     */
    public static boolean areAllValid(double... values) {
        for (double value : values) {
            if (!isValidValue(value)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if dimensions are positive
     * @param width Width to check
     * @param height Height to check
     * @return true if both dimensions are positive
     */
    public static boolean areValidDimensions(int width, int height) {
        return width > 0 && height > 0;
    }
    
    /**
     * Clamp a value between min and max
     * @param value Value to clamp
     * @param min Minimum value
     * @param max Maximum value
     * @return Clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Clamp an integer value between min and max
     * @param value Value to clamp
     * @param min Minimum value
     * @param max Maximum value
     * @return Clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
