package lib.function.functions;

import lib.function.Function;
import lib.function.domains.IntervalDomain;

/**
 * Represents a constant/parameter function with a single numeric value.
 * Can be defined using double square brackets: "k=[[1:5]]" creates a constant
 * named "k" with an initial value in the range [1, 5].
 */
public class ConstantFunction extends Function {
    
    private double value;

    /**
     * Constructor with expression string and name (for parsing)
     * Expression should be in format "[[min:max]]" or just a number
     * @param expression The expression containing the range or value
     * @param name The constant name/label
     */
    public ConstantFunction(String expression, String name) {

        super(expression, name);
        updateExpression();
    }

    /**
     * Constructor with expression string only (for parsing)
     * Expression should be in format "[[min:max]]" or just a number
     * @param expression The expression containing the range or value
     */
    public ConstantFunction(String expression) {
        this(expression, null);
    }

    /**
     * Get the constant value
     * @return The current value
     */
    public double getValue() { return value; }
    
    /**
     * Set the constant value (will be clamped to range)
     * @param value The new value
     */
    public void setValue(double value) {

        this.value = Math.max(this.domain.getMinBound(), Math.min(this.domain.getMaxBound(), value));
        updateExpression();
    }
    
    /**
     * Update the expression to reflect the current value
     */
    private void updateExpression() { setExpression(String.format("%.4f", value)); }

    /**
     * Parse the expression to determine the domain and initial value
     * @return The parsed domain
     */
    @Override
    protected IntervalDomain parseExpression() { 
        
        String trimmed = this.expression.trim();

        if (trimmed.startsWith("[[") && trimmed.endsWith("]]")) {

            String rangeContent = trimmed.substring(2, trimmed.length() - 2).trim();
            String[] parts = rangeContent.split(":");
            
            if (parts.length != 2) throw new IllegalArgumentException("Invalid constant range format: " + expression);
            try {

                double minBound = Double.parseDouble(parts[0].trim());
                double maxBound = Double.parseDouble(parts[1].trim());

                if (minBound >= maxBound) throw new IllegalArgumentException("minValue must be less than maxValue");

                // Set value to midpoint
                this.value = (minBound + maxBound) / 2.0;
                return new IntervalDomain(minBound, maxBound);
            } catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid number format in constant range: " + expression); }
        } else throw new IllegalArgumentException("Expression must be in the form [[min:max]] for ConstantFunction");
    }
    
    /**
     * Get the range as a formatted string
     * @return String representation of the range
     */
    public String getRangeString() {
        return String.format("[%.2f : %.2f]", this.domain.getMinBound(), this.domain.getMaxBound());
    }
    
    @Override
    public String toString() {
        
        return String.format("ConstantFunction{name='%s', value=%.4f, range=[%.2f:%.2f]}", 
            getName(), value, this.domain.getMinBound(), this.domain.getMaxBound());
    }
}
