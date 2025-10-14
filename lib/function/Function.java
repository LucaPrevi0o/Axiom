package lib.function;

/**
 * Represents a mathematical function.
 * Can be extended for additional properties like color and visibility.
 */
public class Function {

    private String expression;
    private String name;
    
    /**
     * Constructor with expression only
     * @param expression The function expression as a string (e.g., "x^2", "sin(x)")
     */
    public Function(String expression) { this(expression, null); }

    /**
     * Full constructor
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     * @param visible Whether the function should be displayed
     */
    public Function(String expression, String name) {

        this.expression = expression;
        this.name = name;
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
    public void setExpression(String expression) { this.expression = expression; }

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
