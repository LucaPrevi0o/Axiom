package lib.model;

import java.awt.Color;
import java.util.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import lib.core.ExpressionEvaluator;
import lib.util.ValidationUtils;

public abstract class PlottableFunction extends AbstractFunction {

    protected final Color color;
    protected boolean enabled;
    protected ArrayList<Point2D.Double> cachedPoints;
    protected boolean pointsCacheValid;
    protected ExpressionEvaluator evaluator;

    /**
     * Create a named function from a mathematical expression
     * @param name Function name (e.g., "f", "g")
     * @param expression Mathematical expression
     * @param color Display color
     */
    public PlottableFunction(String name, String expression, List<Double> domain, Color color) {

        super(name, expression, domain);
        this.color = color;
        this.enabled = true; // Enabled by default
        this.cachedPoints = new ArrayList<>();
        this.pointsCacheValid = false;
        this.evaluator = new ExpressionEvaluator();
    }

    /**
     * Get the display color of the function
     * @return Display color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Check if the function is enabled for plotting
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enable or disable the function for plotting
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the cached computed points for plotting
     * @return List of cached points
     */
    public ArrayList<Point2D.Double> getCachedPoints() {
        return cachedPoints;
    }

    /**
     * Invalidate the points cache (call when domain or expression parameters change)
     */
    public void invalidateCache() {
        this.pointsCacheValid = false;
    }

    /**
     * Get the computed points for plotting. Uses caching - recomputes only if cache is invalid.
     * @return List of 2D points to plot
     */
    public List<Point2D.Double> getPoints() {
        if (!pointsCacheValid) {
            cachedPoints = computePoints();
            pointsCacheValid = true;
        }
        return cachedPoints;
    }

    /**
     * Compute the points for this function by iterating over the domain.
     * Evaluates the expression at each x value in the domain and creates 2D points.
     * Invalid values (NaN, infinite) are skipped.
     * @return List of points in graph coordinates
     */
    protected ArrayList<Point2D.Double> computePoints() {
        ArrayList<Point2D.Double> points = new ArrayList<>();
        
        // Iterate over each x value in the domain
        for (Double x : domain) {
            try {
                // Evaluate expression at this x value
                double y = evaluator.evaluate(expression, x);
                
                // Only add valid points (skip NaN and infinite values)
                if (ValidationUtils.isValidValue(y)) {
                    points.add(new Point2D.Double(x, y));
                }
            } catch (Exception e) {
                // Skip points where evaluation fails
            }
        }
        
        return points;
    }


}
