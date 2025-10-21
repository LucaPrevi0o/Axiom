package lib.function.functions;

import lib.function.Function;
import lib.function.domain.domains.DiscreteDomain;

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

    /**
     * Parse the expression to determine the domain
     * @return The parsed domain
     */
    @Override
    protected DiscreteDomain parseExpression() {
        
        String trimmed = this.expression.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {

            String content = trimmed.substring(1, trimmed.length() - 1).trim();
            String[] parts = content.split(",");
            double[] values = new double[parts.length];
            for (int i = 0; i < parts.length; i++) try { 
                values[i] = Double.parseDouble(parts[i].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in set: " + parts[i].trim());
            }
            return new DiscreteDomain(values);
        } else throw new IllegalArgumentException("Expression must be in the form {a, b, c, ...}");
    }
}
