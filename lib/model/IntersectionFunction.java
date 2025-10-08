package lib.model;

import lib.rendering.IntersectionFinder;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * A function defined by intersection points between two expressions.
 * For syntax like (f(x) = g(x)), this computes and displays only the intersection points.
 */
public class IntersectionFunction extends Function {
    
    private final String leftExpression;
    private final String rightExpression;
    private final IntersectionFinder intersectionFinder;
    
    /**
     * Create an intersection function
     * @param leftExpression Left side of equation (e.g., "x^2")
     * @param rightExpression Right side of equation (e.g., "2*x + 1")
     * @param color Display color
     * @param intersectionFinder Intersection finder to use
     */
    public IntersectionFunction(String leftExpression, String rightExpression, 
                                Color color, IntersectionFinder intersectionFinder) {
        this(null, leftExpression, rightExpression, color, intersectionFinder);
    }
    
    /**
     * Create a named intersection function
     * @param name Function name (e.g., "h")
     * @param leftExpression Left side of equation
     * @param rightExpression Right side of equation
     * @param color Display color
     * @param intersectionFinder Intersection finder to use
     */
    public IntersectionFunction(String name, String leftExpression, String rightExpression,
                                Color color, IntersectionFinder intersectionFinder) {
        super(name, color);
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.intersectionFinder = intersectionFinder;
    }
    
    /**
     * Get the left expression
     */
    public String getLeftExpression() {
        return leftExpression;
    }
    
    /**
     * Get the right expression
     */
    public String getRightExpression() {
        return rightExpression;
    }
    
    @Override
    protected List<Point2D.Double> computePoints(GraphBounds bounds, int width, int height) {
        // Find all intersection points within the current view
        return intersectionFinder.findIntersections(
            leftExpression, 
            rightExpression,
            bounds.getMinX(), 
            bounds.getMaxX(),
            width
        );
    }
    
    @Override
    public String getDisplayString() {
        return "(" + leftExpression + " = " + rightExpression + ")";
    }
    
    @Override
    public boolean isContinuous() {
        return false; // Intersections are discrete points
    }
}
