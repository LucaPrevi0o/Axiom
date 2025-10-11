package lib;
import java.awt.Color;

/**
 * Represents a mathematical function that can be plotted on a graph.
 * 
 * Encapsulates the function expression, visual properties, and evaluation logic.
 */
public class Function {

    private String expression;
    private Color color;
    private boolean visible;
    private String name;
    
    /**
     * Constructor with expression only
     * @param expression The function expression as a string (e.g., "x^2", "sin(x)")
     */
    public Function(String expression) { this(expression, Color.BLUE, true); }
    
    /**
     * Constructor with expression and color
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     */
    public Function(String expression, Color color) { this(expression, color, true); }
    
    /**
     * Full constructor
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     * @param visible Whether the function should be displayed
     */
    public Function(String expression, Color color, boolean visible) {

        this.expression = expression;
        this.color = color;
        this.visible = visible;
        this.name = "f";
    }
    
    /**
     * Get the function expression
     * @return The expression string
     */
    public String getExpression() { return expression; }
    
    /**
     * Set the function expression
     * @param expression The new expression
     */
    public void setExpression(String expression) {

        this.expression = expression;
        this.name = "f(x) = " + expression;
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

    /**
     * Get the function name/label
     * @return The name
     */
    public String getName() { return name; }

    /**
     * Set the function name/label
     * @param name The new name
     */
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return name + "(x) = " + expression; }
}
