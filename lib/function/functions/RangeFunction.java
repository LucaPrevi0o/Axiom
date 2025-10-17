package lib.function.functions;

import lib.function.Function;
import lib.function.domains.IntervalDomain;

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

    /**
     * Parse the expression to determine the domain
     * @return The parsed domain
     */
    @Override
    protected IntervalDomain parseExpression() {

        String trimmed = this.expression.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {

            String content = trimmed.substring(1, trimmed.length() - 1).trim();
            String[] parts = content.split(":");
            if (parts.length != 2) throw new IllegalArgumentException("Range expression must be in the form [min:max]");

            double min, max;
            try { 

                min = Double.parseDouble(parts[0].trim());
                max = Double.parseDouble(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in range: " + content);
            }
            if (min >= max) throw new IllegalArgumentException("min must be less than max");
            return new IntervalDomain(min, max);
        } else throw new IllegalArgumentException("Expression must be in the form [min:max]");
    }
}
