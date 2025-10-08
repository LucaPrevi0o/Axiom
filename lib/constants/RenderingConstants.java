package lib.constants;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

/**
 * Constants related to rendering graphs, axes, grids, and functions
 */
public final class RenderingConstants {
    
    // Prevent instantiation
    private RenderingConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
    
    // Stroke widths
    public static final int FUNCTION_STROKE_WIDTH = 2;
    public static final int AXIS_STROKE_WIDTH = 2;
    public static final int BORDER_STROKE_WIDTH = 2;
    public static final int GRID_STROKE_WIDTH = 1;
    
    // Grid rendering
    public static final int MIN_PIXELS_FOR_SUBGRID = 50;
    public static final Color SUBGRID_COLOR = new Color(240, 240, 240);
    public static final Color MAIN_GRID_COLOR = new Color(200, 200, 200);
    
    // Axis rendering
    public static final Color AXIS_COLOR = Color.BLACK;
    public static final int TICK_LENGTH = 5;
    public static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 10);
    public static final int TARGET_TICK_COUNT = 20;
    
    // Region rendering
    public static final int FILL_ALPHA = 80; // Transparency for region fills (0-255)
    public static final int REGION_SAMPLE_COUNT = 500;
    
    // Intersection rendering
    public static final int INTERSECTION_POINT_RADIUS = 4;
    
    // Function plotting samples
    public static final int MIN_SAMPLES = 50;
    public static final int MAX_SAMPLES = 5000;
    public static final int MIN_INTERSECTION_SAMPLES = 200;
    public static final int MAX_INTERSECTION_SAMPLES = 1000;
    
    // Strokes (pre-created for performance)
    public static final Stroke FUNCTION_STROKE = new BasicStroke(FUNCTION_STROKE_WIDTH);
    public static final Stroke AXIS_STROKE = new BasicStroke(AXIS_STROKE_WIDTH);
    public static final Stroke GRID_STROKE = new BasicStroke(GRID_STROKE_WIDTH);
    public static final Stroke BORDER_STROKE = new BasicStroke(BORDER_STROKE_WIDTH);
}
