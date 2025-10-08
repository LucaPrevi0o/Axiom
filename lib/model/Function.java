package lib.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all plottable functions.
 * Uses Template Method pattern - subclasses define how points are computed,
 * base class manages the point list and common properties.
 */
public abstract class Function {
    
    protected final String name;
    protected final Color color;
    protected boolean enabled;
    protected List<Point2D.Double> cachedPoints;
    protected boolean pointsCacheValid;
    
    /**
     * Create a function with a name and color
     * @param name Function name (e.g., "f", "g", or null for anonymous)
     * @param color Display color
     */
    protected Function(String name, Color color) {
        this.name = name;
        this.color = color;
        this.enabled = true;
        this.cachedPoints = new ArrayList<>();
        this.pointsCacheValid = false;
    }
    
    /**
     * Get the function name (may be null for anonymous functions)
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the display color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Check if function is enabled for display
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Set whether function is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Invalidate the points cache (call when bounds or parameters change)
     */
    public void invalidateCache() {
        this.pointsCacheValid = false;
    }
    
    /**
     * Get the computed points for this function.
     * Uses caching - recomputes only if cache is invalid.
     * @param bounds Graph bounds
     * @param width Screen width in pixels
     * @param height Screen height in pixels
     * @return List of points to plot
     */
    public List<Point2D.Double> getPoints(GraphBounds bounds, int width, int height) {
        if (!pointsCacheValid) {
            cachedPoints = computePoints(bounds, width, height);
            pointsCacheValid = true;
        }
        return cachedPoints;
    }
    
    /**
     * Compute the points for this function.
     * Subclasses must implement this to define how points are calculated.
     * @param bounds Graph bounds
     * @param width Screen width in pixels
     * @param height Screen height in pixels
     * @return List of points in graph coordinates
     */
    protected abstract List<Point2D.Double> computePoints(GraphBounds bounds, int width, int height);
    
    /**
     * Get a display string for this function (for UI)
     * @return Human-readable representation
     */
    public abstract String getDisplayString();
    
    /**
     * Check if this function should be rendered as a continuous curve
     * @return true for continuous functions, false for discrete points
     */
    public boolean isContinuous() {
        return true; // Most functions are continuous
    }
    
    /**
     * Check if this function should be rendered as a filled region
     * @return true for region functions (inequalities)
     */
    public boolean isRegion() {
        return false; // Most functions are not regions
    }
    
    @Override
    public String toString() {
        return (name != null ? name + ": " : "") + getDisplayString();
    }
}
