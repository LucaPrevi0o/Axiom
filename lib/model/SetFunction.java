package lib.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A function representing a mathematical set of values.
 * Two forms are supported:
 * - Explicit: a={1,2,3,4} - discrete list of specific values
 * - Range: b={1:10} - all integer values from 1 to 10 inclusive
 * 
 * Renders as discrete points at y=0 on the graph.
 */
public class SetFunction extends Function {
    
    private final List<Double> values;
    
    /**
     * Create a set function from a list of values
     * @param name Set name (e.g., "a", "b")
     * @param values List of values in the set
     * @param color Display color
     */
    public SetFunction(String name, List<Double> values, Color color) {
        super(name, color);
        this.values = new ArrayList<>(values);
    }
    
    /**
     * Create a set function from an explicit list of values
     * @param name Set name
     * @param explicitValues Array of explicit values (e.g., {1,2,3,4})
     * @param color Display color
     */
    public static SetFunction fromExplicitValues(String name, double[] explicitValues, Color color) {
        List<Double> values = new ArrayList<>();
        for (double value : explicitValues) {
            values.add(value);
        }
        return new SetFunction(name, values, color);
    }
    
    /**
     * Create a set function from a range
     * @param name Set name
     * @param minValue Minimum value (inclusive)
     * @param maxValue Maximum value (inclusive)
     * @param color Display color
     */
    public static SetFunction fromRange(String name, int minValue, int maxValue, Color color) {
        List<Double> values = new ArrayList<>();
        int start = Math.min(minValue, maxValue);
        int end = Math.max(minValue, maxValue);
        for (int i = start; i <= end; i++) {
            values.add((double) i);
        }
        return new SetFunction(name, values, color);
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
    public List<Point2D.Double> computePoints(GraphBounds bounds, int screenWidth, int screenHeight) {
        // Sets are value containers, not visual elements
        // They don't render any points themselves
        // Use them in parametric points like P=(0,Q) to create point lists
        return new ArrayList<>();
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
    
    @Override
    public boolean isContinuous() {
        return false; // Sets are discrete points
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
