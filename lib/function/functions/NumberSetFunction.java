package lib.function.functions;

import lib.function.Function;
import lib.function.domains.DiscreteDomain;

public class NumberSetFunction extends Function {
    
    /**
     * Full constructor
     * @param expression The function expression as a string
     * @param name The function name/label
     */
    public NumberSetFunction(String expression, String name) {

        super(expression, name);
        // this.setDomain(new DiscreteDomain());
    }

    /**
     * Constructor with expression only
     * @param expression The function expression as a string
     */
    public NumberSetFunction(String expression) {
        this(expression, null);
    }
}
