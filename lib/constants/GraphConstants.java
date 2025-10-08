package lib.constants;

/**
 * Constants related to graph bounds, zoom, and pan operations
 */
public final class GraphConstants {
    
    // Prevent instantiation
    private GraphConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
    
    // Zoom factors
    public static final double ZOOM_IN_FACTOR = 0.95;
    public static final double ZOOM_OUT_FACTOR = 1.05;
    
    // Default view ranges
    public static final double DEFAULT_VIEW_RANGE_X = 40.0;
    public static final double DEFAULT_VIEW_RANGE_Y = 40.0;
    public static final double DEFAULT_VIEW_RANGE = 20.0;
    public static final double INITIAL_ZOOM = 1.0;
    
    // Aspect ratio and zoom constraints
    public static final double MIN_ZOOM_MULTIPLIER = 1.0;
    public static final double MAX_ZOOM_MULTIPLIER = 10.0;
}
