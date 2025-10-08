package lib.constants;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constants related to user interface components
 */
public final class UIConstants {
    
    // Prevent instantiation
    private UIConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
    
    // Split pane configuration
    public static final int INITIAL_DIVIDER_LOCATION = 280;
    public static final int COLLAPSED_SIZE = 12;
    public static final int MIN_EXPANDED_SIZE = 250;
    public static final int MAX_EXPANDED_SIZE = 450;
    public static final int MIN_GRAPH_PANEL_WIDTH = 400;
    
    // Function panel
    public static final int MAX_FUNCTION_ENTRY_HEIGHT = 80;
    
    // Parameter slider
    public static final int SLIDER_STEPS = 1000; // Resolution for parameter sliders
    
    // LaTeX rendering
    public static final float LATEX_FONT_SIZE = 18f;
    public static final int LATEX_ICON_MARGIN = 4;
    
    // Default color palette for functions
    private static final List<Color> DEFAULT_COLORS_INTERNAL = Arrays.asList(
        Color.BLUE, 
        Color.RED, 
        Color.GREEN, 
        Color.ORANGE,
        Color.MAGENTA, 
        Color.CYAN, 
        new Color(139, 69, 19),  // Brown
        new Color(128, 0, 128)    // Purple
    );
    
    public static final List<Color> DEFAULT_FUNCTION_COLORS = 
        Collections.unmodifiableList(DEFAULT_COLORS_INTERNAL);
}
