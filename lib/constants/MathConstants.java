package lib.constants;

/**
 * Mathematical constants used in calculations
 */
public final class MathConstants {
    
    // Prevent instantiation
    private MathConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
    
    // Numerical analysis
    public static final int BISECTION_MAX_ITERATIONS = 40;
    public static final double BISECTION_EPSILON = 1e-8;
    public static final double DEDUPLICATION_THRESHOLD = 1e-6;
    
    // Precision and formatting
    public static final double EPSILON = 1e-12; // General floating point comparison
    public static final int MAX_DECIMAL_PRECISION = 6;
    public static final int MIN_DECIMAL_PRECISION = 0;
}
