package lib.model.function.definition;

import lib.model.function.base.BaseFunction;
import java.util.ArrayList;
import java.util.List;

/**
 * A function representing a mathematical set of values.
 * Two forms are supported:
 * - Explicit: a={1,2,3,4} - discrete list of specific values
 * - Range: b={1:10} - all integer values from 1 to 10 inclusive
 * 
 * Sets are NOT plottable - they serve as value containers that can be
 * referenced by other functions (e.g., parametric points like P=(0,Q)).
 */
public class SetFunction extends BaseFunction {
    
    private final List<Double> values;
    
    /**
     * Create a set function from a list of values
     * @param name Set name (e.g., "a", "b")
     * @param values List of values in the set
     */
    public SetFunction(String name, List<Double> values) {
        super(name);
        this.values = new ArrayList<>(values);
    }
    
    /**
     * Create a set function from an explicit list of values
     * @param name Set name
     * @param explicitValues Array of explicit values (e.g., {1,2,3,4})
     */
    public static SetFunction fromExplicitValues(String name, double[] explicitValues) {
        List<Double> values = new ArrayList<>();
        for (double value : explicitValues) {
            values.add(value);
        }
        return new SetFunction(name, values);
    }
    
    /**
     * Create a set function from a range
     * @param name Set name
     * @param minValue Minimum value (inclusive)
     * @param maxValue Maximum value (inclusive)
     */
    public static SetFunction fromRange(String name, int minValue, int maxValue) {
        List<Double> values = new ArrayList<>();
        int start = Math.min(minValue, maxValue);
        int end = Math.max(minValue, maxValue);
        for (int i = start; i <= end; i++) {
            values.add((double) i);
        }
        return new SetFunction(name, values);
    }
    
    /**
     * Get the list of values in this set
     */
    public List<Double> getValues() {
        return new ArrayList<>(values);
    }
    
    /**
     * Check if a value is in this set
     */
    public boolean contains(double value) {
        return values.contains(value);
    }
    
    /**
     * Get the size of this set
     */
    public int size() {
        return values.size();
    }
    
    @Override
    public String getDisplayString() {
        if (values.isEmpty()) {
            return name + "={}";
        }
        
        // Check if it's a continuous range
        if (isConsecutiveRange()) {
            int min = values.get(0).intValue();
            int max = values.get(values.size() - 1).intValue();
            return name + "={" + min + ":" + max + "}";
        }
        
        // Explicit values
        StringBuilder sb = new StringBuilder(name + "={");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(values.get(i).intValue());
            
            // Truncate if too many values
            if (i >= 10 && values.size() > 11) {
                sb.append(",...");
                break;
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Check if values form a consecutive integer range
     */
    private boolean isConsecutiveRange() {
        if (values.size() < 2) return false;
        
        // Sort values to check
        List<Double> sorted = new ArrayList<>(values);
        sorted.sort(Double::compareTo);
        
        int first = sorted.get(0).intValue();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).intValue() != first + i) {
                return false;
            }
        }
        return true;
    }
}
