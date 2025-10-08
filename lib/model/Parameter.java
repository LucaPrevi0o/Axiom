package lib.model;

/**
 * Represents a parameter with a range that can be controlled by a slider
 */
public class Parameter {
    
    private final String name;
    private final double minValue;
    private final double maxValue;
    private double currentValue;
    
    /**
     * Create a parameter with a range
     * @param name Parameter name
     * @param minValue Minimum value
     * @param maxValue Maximum value
     */
    public Parameter(String name, double minValue, double maxValue) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        // Start at the middle of the range
        this.currentValue = (minValue + maxValue) / 2.0;
    }
    
    /**
     * Create a parameter with initial value
     * @param name Parameter name
     * @param minValue Minimum value
     * @param maxValue Maximum value
     * @param initialValue Initial value
     */
    public Parameter(String name, double minValue, double maxValue, double initialValue) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = Math.max(minValue, Math.min(maxValue, initialValue));
    }
    
    public String getName() {
        return name;
    }
    
    public double getMinValue() {
        return minValue;
    }
    
    public double getMaxValue() {
        return maxValue;
    }
    
    public double getCurrentValue() {
        return currentValue;
    }
    
    public void setCurrentValue(double value) {
        this.currentValue = Math.max(minValue, Math.min(maxValue, value));
    }
    
    public double getRange() {
        return maxValue - minValue;
    }
    
    @Override
    public String toString() {
        return name + "=[" + minValue + ":" + maxValue + "]";
    }
}
