package lib.model.function.base;

import lib.model.domain.GraphBounds;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for functions that can be plotted on the graph.
 * Extends Function with color, point computation, and caching.
 * Uses Template Method pattern - subclasses define how points are computed,
 * this class manages the point list and common plottable properties.
 * 
 * Examples: Regular functions, Equations, Inequations, Points
 */
public abstract class PlottableFunction extends Function {
    
    protected final Color color;
    protected List<Point2D.Double> cachedPoints;
    protected boolean pointsCacheValid;
    
    /**
     * Create a plottable function with a name and color
     * @param name Function name (e.g., "f", "g", or null for anonymous)
     * @param color Display color
     */
    protected PlottableFunction(String name, Color color) {
        super(name);
        this.color = color;
        this.cachedPoints = new ArrayList<>();
        this.pointsCacheValid = false;
    }
    
    /**
     * Get the display color
     */
    public Color getColor() {
        return color;
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
     * Check if this function represents a continuous curve
     * @return true if continuous, false if discrete points
     */
    public abstract boolean isContinuous();
    
    @Override
    public final boolean isPlottable() {
        return true;
    }
}
