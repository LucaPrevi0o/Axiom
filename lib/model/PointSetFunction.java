package lib.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A function defined by a discrete set of points.
 * Useful for scatter plots, data points, or manually specified points.
 */
public class PointSetFunction extends Function {
    
    private final List<Point2D.Double> points;
    
    /**
     * Create a point set function
     * @param points List of points in graph coordinates
     * @param color Display color
     */
    public PointSetFunction(List<Point2D.Double> points, Color color) {
        this(null, points, color);
    }
    
    /**
     * Create a named point set function
     * @param name Function name
     * @param points List of points in graph coordinates
     * @param color Display color
     */
    public PointSetFunction(String name, List<Point2D.Double> points, Color color) {
        super(name, color);
        this.points = new ArrayList<>(points); // Defensive copy
    }
    
    /**
     * Create a named point set function from coordinate arrays
     * @param name Function name
     * @param xValues Array of x coordinates
     * @param yValues Array of y coordinates
     * @param color Display color
     */
    public PointSetFunction(String name, double[] xValues, double[] yValues, Color color) {
        super(name, color);
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("xValues and yValues must have the same length");
        }
        this.points = new ArrayList<>();
        for (int i = 0; i < xValues.length; i++) {
            this.points.add(new Point2D.Double(xValues[i], yValues[i]));
        }
    }
    
    /**
     * Add a point to the set
     * @param point Point to add
     */
    public void addPoint(Point2D.Double point) {
        points.add(point);
        invalidateCache();
    }
    
    /**
     * Add a point to the set
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void addPoint(double x, double y) {
        addPoint(new Point2D.Double(x, y));
    }
    
    /**
     * Clear all points
     */
    public void clearPoints() {
        points.clear();
        invalidateCache();
    }
    
    /**
     * Get the number of points
     */
    public int getPointCount() {
        return points.size();
    }
    
    @Override
    protected List<Point2D.Double> computePoints(GraphBounds bounds, int width, int height) {
        // Return all points (already in graph coordinates)
        // Could filter by bounds if needed for performance
        return new ArrayList<>(points);
    }
    
    @Override
    public String getDisplayString() {
        return points.size() + " points";
    }
    
    @Override
    public boolean isContinuous() {
        return false; // Discrete points, not a curve
    }
}
