package lib.graph;
import java.awt.Color;

public class GraphFunction {
    
    private String expression;
    private Color color;
    
    /**
     * Constructor to create a graph function
     * @param expression The function expression
     * @param color The color to draw this function
     */
    public GraphFunction(String expression, Color color) {
        this.expression = expression;
        this.color = color;
    }
    
    /**
     * Get the expression
     * @return The expression string
     */
    public String getExpression() {
        return expression;
    }
    
    /**
     * Get the color
     * @return The color
     */
    public Color getColor() {
        return color;
    }
}
