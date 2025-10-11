package lib;

import java.awt.Color;

public class PlottableFunction extends Function {
    
    private Color color;
    private boolean visible;

    /**
     * Full constructor
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     * @param visible Whether the function should be displayed
     * @param name The function name/label
     */
    public PlottableFunction(String expression, Color color, boolean visible, String name) {

        super(expression, name);
        this.color = color;
        this.visible = visible;
    }

    /**
     * Constructor with expression, color, and name
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     * @param name The function name/label
     */
    public PlottableFunction(String expression, Color color, String name) {
        this(expression, color, true, name);
    }

    /**
     * Constructor with expression and color
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     */
    public PlottableFunction(String expression, Color color) {
        this(expression, color, true, null);
    }

    /**
     * Get the function color
     * @return The color
     */
    public Color getColor() { return color; }
    
    /**
     * Set the function color
     * @param color The new color
     */
    public void setColor(Color color) { this.color = color; }
    
    /**
     * Check if the function is visible
     * @return true if visible, false otherwise
     */
    public boolean isVisible() { return visible; }
    
    /**
     * Set the function visibility
     * @param visible The visibility state
     */
    public void setVisible(boolean visible) { this.visible = visible; }
}
