package lib.model;

/**
 * Represents a parameter with a range that can be controlled by a slider
 */
public class Parameter {
    
    private final String name;
    private final double minValue;
    private final double maxValue;
    private double currentValue;
    private final boolean discrete; // true for integer-only values (e.g., [1;5])
    
    /**
     * Create a continuous parameter with a range
     * @param name Parameter name
     * @param minValue Minimum value
     * @param maxValue Maximum value
     */
    public Parameter(String name, double minValue, double maxValue) {
        this(name, minValue, maxValue, false);
    }
    
    /**
     * Create a parameter with a range
     * @param name Parameter name
     * @param minValue Minimum value
     * @param maxValue Maximum value
     * @param discrete true for integer-only values, false for continuous
     */
    public Parameter(String name, double minValue, double maxValue, boolean discrete) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.discrete = discrete;
        // Start at the middle of the range
        double midValue = (minValue + maxValue) / 2.0;
        this.currentValue = discrete ? Math.round(midValue) : midValue;
    }
    
    /**
     * Create a parameter with initial value
     * @param name Parameter name
     * @param minValue Minimum value
     * @param maxValue Maximum value
     * @param initialValue Initial value
     */
    public Parameter(String name, double minValue, double maxValue, double initialValue) {
        this(name, minValue, maxValue, initialValue, false);
    }
    
    /**
     * Create a parameter with initial value
     * @param name Parameter name
     * @param minValue Minimum value
     * @param maxValue Maximum value
     * @param initialValue Initial value
     * @param discrete true for integer-only values, false for continuous
     */
    public Parameter(String name, double minValue, double maxValue, double initialValue, boolean discrete) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.discrete = discrete;
        double clampedValue = Math.max(minValue, Math.min(maxValue, initialValue));
        this.currentValue = discrete ? Math.round(clampedValue) : clampedValue;
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
        double clampedValue = Math.max(minValue, Math.min(maxValue, value));
        this.currentValue = discrete ? Math.round(clampedValue) : clampedValue;
    }
    
    public double getRange() {
        return maxValue - minValue;
    }
    
    /**
     * Check if this parameter only accepts integer values
     * @return true for discrete (integer-only), false for continuous
     */
    public boolean isDiscrete() {
        return discrete;
    }
    
    /**
     * Get the number of discrete steps (for discrete parameters)
     * @return Number of integer values in range, or -1 for continuous
     */
    public int getDiscreteStepCount() {
        if (!discrete) {
            return -1;
        }
        return (int) (maxValue - minValue) + 1;
    }
    
    @Override
    public String toString() {
        if (discrete) {
            return name + "=[" + (int)minValue + ".." + (int)maxValue + "]";
        } else {
            return name + "=[" + (int)minValue + ":" + (int)maxValue + "]";
        }
    }
}
