package lib.function.functions;

import java.awt.Color;
import lib.function.PlottableFunction;
import lib.function.domains.DiscreteDomain;

public class PointFunction extends PlottableFunction {

    private double yValue;
    
    /**
     * Full constructor
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     * @param visible Whether the function should be displayed
     * @param name The function name/label
     */
    public PointFunction(String expression, Color color, boolean visible, String name) {
        super(expression, color, visible, name);
    }

    /**
     * Constructor with expression, color, and name
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     * @param name The function name/label
     */
    public PointFunction(String expression, Color color, String name) {
        this(expression, color, true, name);
    }

    /**
     * Constructor with expression and color
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     */
    public PointFunction(String expression, Color color) {
        this(expression, color, true, null);
    }

    /**
     * Get the y-value of the point
     * @return The y-value
     */
    public double getYValue() { return yValue; }

    /**
     * Parse the expression to determine the domain
     * @return The parsed domain
     */   
    @Override
    protected DiscreteDomain parseExpression() {
        
        String trimmed = this.expression.trim();
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {

            String content = trimmed.substring(1, trimmed.length() - 1).trim();
            String[] parts = content.split(",");
            if (parts.length != 2) throw new IllegalArgumentException("Point expression must be in the form (x, y)");
            double[] values = new double[1];
            try { 

                values[0] = Double.parseDouble(parts[0].trim());
                yValue = Double.parseDouble(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in point: " + content);
            }
            return new DiscreteDomain(values);
        } else throw new IllegalArgumentException("Expression must be in the form (x, y)");
    }
}
