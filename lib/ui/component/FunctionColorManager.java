package lib.ui.component;

import lib.constants.UIConstants;
import java.awt.Color;
import java.util.List;

/**
 * Manages color assignment for functions
 */
public class FunctionColorManager {
    
    private final List<Color> colors;
    private int nextColorIndex;
    
    /**
     * Create a color manager with default colors
     */
    public FunctionColorManager() {
        this(UIConstants.DEFAULT_FUNCTION_COLORS);
    }
    
    /**
     * Create a color manager with custom colors
     * @param colors List of colors to cycle through
     */
    public FunctionColorManager(List<Color> colors) {
        this.colors = colors;
        this.nextColorIndex = 0;
    }
    
    /**
     * Get the next color in the sequence
     * @return Next color
     */
    public Color getNextColor() {
        Color color = colors.get(nextColorIndex % colors.size());
        nextColorIndex++;
        return color;
    }
    
    /**
     * Reset the color cycle
     */
    public void reset() {
        nextColorIndex = 0;
    }
    
    /**
     * Get color at specific index
     * @param index Index in color list
     * @return Color at that index
     */
    public Color getColorAt(int index) {
        return colors.get(index % colors.size());
    }
    
    /**
     * Get total number of colors
     * @return Number of colors in the palette
     */
    public int getColorCount() {
        return colors.size();
    }
}