package lib.function.functions;

import java.awt.Color;

import lib.function.Domain;
import lib.function.PlottableFunction;
import lib.function.domains.IntervalDomain;

public class ExpressionFunction extends PlottableFunction {
    
    /**
     * Full constructor
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     * @param visible Whether the function should be displayed
     * @param name The function name/label
     */
    public ExpressionFunction(String expression, Color color, boolean visible, String name) {
        super(expression, color, visible, name);
    }

    /**
     * Constructor with expression, color, and name
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     * @param name The function name/label
     */
    public ExpressionFunction(String expression, Color color, String name) {
        this(expression, color, true, name);
    }

    /**
     * Constructor with expression and color
     * @param expression The function expression as a string
     * @param color The color to use when plotting
     */
    public ExpressionFunction(String expression, Color color) {
        this(expression, color, true, null);
    }

    /**
     * Parse the expression to determine the domain
     * @return The parsed domain
     */   
    @Override
    protected IntervalDomain parseExpression() { return (IntervalDomain)Domain.parse(this.expression); }
}
