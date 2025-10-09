package lib.model.function.definition;

import lib.model.function.base.BaseFunction;
import lib.util.FormattingUtils;

/**
 * Represents a constant or parameter with optional slider control
 * This unifies the old Parameter class into the Function hierarchy
 */
public class ConstantFunction extends BaseFunction {
    
    private final Double minValue;
    private final Double maxValue;
    private double currentValue;
    private final boolean discrete; // true for integer-only values (e.g., [1..5])
    
    /**
     * Create a simple constant with a fixed value
     * @param name Constant name
     * @param value Constant value
     */
    public ConstantFunction(String name, double value) {
        super(name);
        this.currentValue = value;
        this.minValue = null;
        this.maxValue = null;
        this.discrete = false;
    }
    
    /**
     * Create a continuous parameter with a slider range
     * @param name Parameter name
     * @param minValue Minimum value
     * @param maxValue Maximum value
     */
    public ConstantFunction(String name, double minValue, double maxValue) {
        this(name, minValue, maxValue, false);
    }
    
    /**
     * Create a parameter with a slider range
     * @param name Parameter name
     * @param minValue Minimum value
     * @param maxValue Maximum value
     * @param discrete true for integer-only values, false for continuous
     */
    public ConstantFunction(String name, double minValue, double maxValue, boolean discrete) {
        super(name);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.discrete = discrete;
        // Start at the middle of the range
        double midValue = (minValue + maxValue) / 2.0;
        this.currentValue = discrete ? Math.round(midValue) : midValue;
    }
    
    /**
     * Create a parameter with a slider range and initial value
     * @param name Parameter name
     * @param minValue Minimum value
     * @param maxValue Maximum value
     * @param initialValue Initial value
     */
    public ConstantFunction(String name, double minValue, double maxValue, double initialValue) {
        this(name, minValue, maxValue, initialValue, false);
    }
    
    /**
     * Create a parameter with a slider range and initial value
     * @param name Parameter name
     * @param minValue Minimum value
     * @param maxValue Maximum value
     * @param initialValue Initial value
     * @param discrete true for integer-only values, false for continuous
     */
    public ConstantFunction(String name, double minValue, double maxValue, double initialValue, boolean discrete) {
        super(name);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.discrete = discrete;
        double clampedValue = Math.max(minValue, Math.min(maxValue, initialValue));
        this.currentValue = discrete ? Math.round(clampedValue) : clampedValue;
    }
    
    /**
     * Check if this constant has a slider (has a defined range)
     * @return true if this is a parameter with min/max range, false if simple constant
     */
    public boolean hasSlider() {
        return minValue != null && maxValue != null;
    }
    
    public Double getMinValue() {
        return minValue;
    }
    
    public Double getMaxValue() {
        return maxValue;
    }
    
    public double getCurrentValue() {
        return currentValue;
    }
    
    public void setCurrentValue(double value) {
        if (hasSlider()) {
            double clampedValue = Math.max(minValue, Math.min(maxValue, value));
            this.currentValue = discrete ? Math.round(clampedValue) : clampedValue;
        } else {
            this.currentValue = value;
        }
    }
    
    public double getRange() {
        return hasSlider() ? (maxValue - minValue) : 0;
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
     * @return Number of integer values in range, or -1 for continuous or simple constants
     */
    public int getDiscreteStepCount() {
        if (!discrete || !hasSlider()) {
            return -1;
        }
        return (int) (maxValue - minValue) + 1;
    }
    
    @Override
    public String getDisplayString() {
        if (!hasSlider()) {
            // Simple constant: a=5
            return name + "=" + FormattingUtils.formatDecimal(currentValue, 3);
        }
        
        if (discrete) {
            // Discrete parameter: a=[1..5]
            return name + "=[" + minValue.intValue() + ".." + maxValue.intValue() + "]";
        } else {
            // Continuous parameter: a=[1:5]
            return name + "=[" + minValue.intValue() + ":" + maxValue.intValue() + "]";
        }
    }
    
    /**
     * Factory method to create from old Parameter object (for migration)
     * @param param The Parameter to convert
     * @return A new ConstantFunction with same properties
     */
    public static ConstantFunction fromParameter(lib.model.domain.Parameter param) {
        return new ConstantFunction(
            param.getName(),
            param.getMinValue(),
            param.getMaxValue(),
            param.getCurrentValue(),
            param.isDiscrete()
        );
    }
}
