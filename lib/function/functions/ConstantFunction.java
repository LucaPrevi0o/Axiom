package lib.function.functions;

import lib.function.Function;

/**
 * Represents a constant/parameter function with a single numeric value.
 * Can be defined using double square brackets: "k=[[1:5]]" creates a constant
 * named "k" with an initial value in the range [1, 5].
 */
public class ConstantFunction extends Function {
    
    private double value;
    private double minValue;
    private double maxValue;
    
    /**
     * Full constructor with name, value, and range
     * @param name The constant name/label
     * @param value The initial value of the constant
     * @param minValue The minimum value for the slider
     * @param maxValue The maximum value for the slider
     */
    public ConstantFunction(String name, double value, double minValue, double maxValue) {

        super(String.valueOf(value), name);
        
        // Validate range
        if (minValue >= maxValue) throw new IllegalArgumentException("minValue must be less than maxValue");
        
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        
        // Clamp value to range
        this.value = Math.max(minValue, Math.min(maxValue, value));
        
        // Update expression to reflect the current value
        updateExpression();
    }
    
    /**
     * Constructor with name and range (value defaults to midpoint)
     * @param name The constant name/label
     * @param minValue The minimum value for the slider
     * @param maxValue The maximum value for the slider
     */
    public ConstantFunction(String name, double minValue, double maxValue) {
        this(name, (minValue + maxValue) / 2.0, minValue, maxValue);
    }
    
    /**
     * Constructor with expression string and name (for parsing)
     * Expression should be in format "[[min:max]]" or just a number
     * @param expression The expression containing the range or value
     * @param name The constant name/label
     */
    public ConstantFunction(String expression, String name) {

        super(expression, name);
        
        // Parse the expression to extract min/max values
        parseExpression(expression);
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

        this.value = Math.max(minValue, Math.min(maxValue, value));
        updateExpression();
    }
    
    /**
     * Get the minimum value
     * @return The minimum value
     */
    public double getMinValue() { return minValue; }
    
    /**
     * Set the minimum value
     * @param minValue The new minimum value
     */
    public void setMinValue(double minValue) {

        if (minValue >= this.maxValue) throw new IllegalArgumentException("minValue must be less than maxValue");
        this.minValue = minValue;
        // Re-clamp current value
        this.value = Math.max(minValue, Math.min(maxValue, value));
        updateExpression();
    }
    
    /**
     * Get the maximum value
     * @return The maximum value
     */
    public double getMaxValue() { return maxValue; }
    
    /**
     * Set the maximum value
     * @param maxValue The new maximum value
     */
    public void setMaxValue(double maxValue) {

        if (maxValue <= this.minValue) throw new IllegalArgumentException("maxValue must be greater than minValue");
        this.maxValue = maxValue;
        // Re-clamp current value
        this.value = Math.max(minValue, Math.min(maxValue, value));
        updateExpression();
    }
    
    /**
     * Update the expression to reflect the current value
     */
    private void updateExpression() { setExpression(String.format("%.4f", value)); }
    
    /**
     * Parse the expression to extract min/max values
     * Supports: "[[1:5]]" or just "3.14"
     * @param expression The expression to parse
     */
    private void parseExpression(String expression) {

        String trimmed = expression.trim();
        
        // Check if it's a range expression [[min:max]]
        if (trimmed.startsWith("[[") && trimmed.endsWith("]]")) {

            String rangeContent = trimmed.substring(2, trimmed.length() - 2).trim();
            String[] parts = rangeContent.split(":");
            
            if (parts.length != 2) throw new IllegalArgumentException("Invalid constant range format: " + expression);
            
            try {

                this.minValue = Double.parseDouble(parts[0].trim());
                this.maxValue = Double.parseDouble(parts[1].trim());

                if (this.minValue >= this.maxValue) throw new IllegalArgumentException("minValue must be less than maxValue");

                // Set value to midpoint
                this.value = (this.minValue + this.maxValue) / 2.0;
                
            } catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid number format in constant range: " + expression); }
        } else {

            // It's just a single value - create a reasonable range around it
            try {

                this.value = Double.parseDouble(trimmed);
                // Create a range of ±50% around the value (or ±1 if value is 0)
                if (this.value == 0) {

                    this.minValue = -10.0;
                    this.maxValue = 10.0;
                } else {

                    double range = Math.abs(this.value);
                    this.minValue = this.value - range;
                    this.maxValue = this.value + range;
                }
            } catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid constant value format: " + expression); }
        }
    }
    
    /**
     * Get the range as a formatted string
     * @return String representation of the range
     */
    public String getRangeString() {
        return String.format("[%.2f : %.2f]", minValue, maxValue);
    }
    
    @Override
    public String toString() {
        return String.format("ConstantFunction{name='%s', value=%.4f, range=[%.2f:%.2f]}", 
            getName(), value, minValue, maxValue);
    }
}
