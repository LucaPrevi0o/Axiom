package lib.function.functions;

import lib.function.Function;

public class RangeFunction extends Function {
    
    /**
     * Full constructor
     * @param expression The function expression as a string
     * @param name The function name/label
     */
    public RangeFunction(String expression, String name) {
        super(expression, name);
    }

    /**
     * Constructor with expression only
     * @param expression The function expression as a string
     */
    public RangeFunction(String expression) {
        this(expression, null);
    }
}
