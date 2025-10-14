package lib.function.functions;

import lib.function.Function;

public class NumberSetFunction extends Function {
    
    /**
     * Full constructor
     * @param expression The function expression as a string
     * @param name The function name/label
     */
    public NumberSetFunction(String expression, String name) {
        super(expression, name);
    }

    /**
     * Constructor with expression only
     * @param expression The function expression as a string
     */
    public NumberSetFunction(String expression) {
        this(expression, null);
    }
}
