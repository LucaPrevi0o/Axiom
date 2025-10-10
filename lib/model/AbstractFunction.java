package lib.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all plottable functions.
 * Uses Template Method pattern - subclasses define how points are computed,
 * base class manages the point list and common properties.
 */
public abstract class AbstractFunction {
    
    protected final String name; // Function name (e.g., "f", "g", or null for anonymous)
    protected final String expression; // Mathematical expression defining the function
    protected List<Double> domain; // Domain of the function (list of x values)
    
    /**
     * Create a function with a name
     * @param name Function name (e.g., "f", "g", or null for anonymous)
     * @param expression Mathematical expression defining the function
     */
    protected AbstractFunction(String name, String expression, List<Double> domain) {

        this.name = name;
        this.expression = expression;
        this.domain = domain != null ? domain : new ArrayList<>();
    }
    
    /**
     * Get the function name (may be null for anonymous functions)
     */
    public String getName() {
        return name;
    }

    /**
     * Get the function expression
     */
    public String getExpression() {
        return expression;
    }
}
