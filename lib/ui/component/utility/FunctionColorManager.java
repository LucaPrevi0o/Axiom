package lib.ui.component.utility;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages color assignment for plottable functions.
 * Provides a cycling palette of distinct colors and tracks color usage.
 */
public class FunctionColorManager {
    
    private static final Color[] COLOR_PALETTE = {
        new Color(31, 119, 180),   // Blue
        new Color(255, 127, 14),   // Orange
        new Color(44, 160, 44),    // Green
        new Color(214, 39, 40),    // Red
        new Color(148, 103, 189),  // Purple
        new Color(140, 86, 75),    // Brown
        new Color(227, 119, 194),  // Pink
        new Color(127, 127, 127),  // Gray
        new Color(188, 189, 34),   // Olive
        new Color(23, 190, 207)    // Cyan
    };
    
    private int nextColorIndex;
    private List<Color> usedColors;
    
    /**
     * Constructor
     */
    public FunctionColorManager() {
        this.nextColorIndex = 0;
        this.usedColors = new ArrayList<>();
    }
    
    /**
     * Get the next available color from the palette
     * @return Color object
     */
    public Color getNextColor() {
        Color color = COLOR_PALETTE[nextColorIndex];
        nextColorIndex = (nextColorIndex + 1) % COLOR_PALETTE.length;
        usedColors.add(color);
        return color;
    }
    
    /**
     * Get a color at a specific index in the palette
     * @param index Index (will be wrapped if out of bounds)
     * @return Color object
     */
    public Color getColorAtIndex(int index) {
        return COLOR_PALETTE[index % COLOR_PALETTE.length];
    }
    
    /**
     * Reset the color manager (start from first color again)
     */
    public void reset() {
        nextColorIndex = 0;
        usedColors.clear();
    }
    
    /**
     * Get the number of colors in the palette
     * @return Palette size
     */
    public int getPaletteSize() {
        return COLOR_PALETTE.length;
    }
    
    /**
     * Get all colors in the palette
     * @return Array of colors
     */
    public Color[] getAllColors() {
        return COLOR_PALETTE.clone();
    }
    
    /**
     * Get the list of currently used colors
     * @return List of used colors
     */
    public List<Color> getUsedColors() {
        return new ArrayList<>(usedColors);
    }
}
