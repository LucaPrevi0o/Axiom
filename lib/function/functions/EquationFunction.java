package lib.function.functions;

import lib.function.Domain;
import lib.function.PlottableFunction;
import lib.function.domains.DiscreteDomain;

public class EquationFunction extends PlottableFunction {
    
    /**
     * Full constructor
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     * @param visible Whether the function should be displayed
     * @param name The function name/label
     */
    public EquationFunction(String expression, java.awt.Color color, boolean visible, String name) {
        super(expression, color, visible, name);
    }

    /**
     * Constructor with expression, color, and name
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     * @param name The function name/label
     */
    public EquationFunction(String expression, java.awt.Color color, String name) {
        super(expression, color, true, name);
    }

    /**
     * Constructor with expression and color
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     */
    public EquationFunction(String expression, java.awt.Color color) {
        super(expression, color, true, null);
    }

    protected DiscreteDomain parseExpression() {

        String trimmed = this.expression.trim();
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {

            String inner = trimmed.substring(1, trimmed.length() - 1);
            String[] parts = inner.split("=");
            if (parts.length == 2) {

                String left = parts[0].trim();
                String right = parts[1].trim();
                Domain leftDomain = Domain.parse(left);
                Domain rightDomain = Domain.parse(right);
                return new DiscreteDomain(new double[]{0, 1});
            }
        }
        throw new IllegalArgumentException("Invalid equation format");
    }
}
