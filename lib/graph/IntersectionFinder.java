package lib.graph;

import lib.expression.ExpressionEvaluator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles finding intersection points between two mathematical expressions
 * using numerical methods.
 */
public class IntersectionFinder {
    
    // Intersection computation constants
    private static final int MIN_INTERSECTION_SAMPLES = 200;
    private static final int MAX_INTERSECTION_SAMPLES = 1000;
    private static final int BISECTION_MAX_ITERATIONS = 40;
    private static final double BISECTION_EPSILON = 1e-8;
    private static final double DEDUPLICATION_THRESHOLD = 1e-6;
    
    private final ExpressionEvaluator evaluator;
    
    /**
     * Create an intersection finder with the given evaluator
     * @param evaluator The expression evaluator to use
     */
    public IntersectionFinder(ExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }
    
    /**
     * Find all intersection points between two expressions over a range
     * @param leftExpr Left side expression
     * @param rightExpr Right side expression
     * @param minX Minimum X value to search
     * @param maxX Maximum X value to search
     * @param screenWidth Width in pixels (used for adaptive sampling)
     * @return List of intersection points
     */
    public List<Point2D.Double> findIntersections(String leftExpr, String rightExpr, 
                                                    double minX, double maxX, int screenWidth) {
        List<Point2D.Double> roots = new ArrayList<>();
        
        int samples = calculateSampleCount(screenWidth);
        double step = (maxX - minX) / (double) samples;
        double prevVal = Double.NaN;
        double prevX = minX;
        
        for (int i = 0; i <= samples; i++) {
            double x = minX + i * step;
            try {
                double vLeft = evaluator.evaluate(leftExpr, x);
                double vRight = evaluator.evaluate(rightExpr, x);
                double v = vLeft - vRight;
                
                if (isValidValue(prevVal) && isValidValue(v)) {
                    if (hasSignChange(prevVal, v)) {
                        double root = findRootByBisection(leftExpr, rightExpr, prevX, x, prevVal);
                        
                        if (isValidValue(root)) {
                            try {
                                double y = evaluator.evaluate(leftExpr, root);
                                Point2D.Double p = new Point2D.Double(root, y);
                                
                                // Deduplicate close roots
                                if (!isDuplicate(p, roots)) {
                                    roots.add(p);
                                }
                            } catch (Exception ex) {
                                // Ignore invalid points
                            }
                        }
                    }
                }
                
                prevVal = v;
                prevX = x;
            } catch (Exception ex) {
                prevVal = Double.NaN;
                prevX = x;
            }
        }
        
        return roots;
    }
    
    /**
     * Find the root of (leftExpr - rightExpr) using bisection method
     * @param leftExpr The left expression
     * @param rightExpr The right expression
     * @param a Start of interval
     * @param b End of interval
     * @param fa Function value at a
     * @return The root, or NaN if not found
     */
    public double findRootByBisection(String leftExpr, String rightExpr, 
                                      double a, double b, double fa) {
        double root = Double.NaN;
        try {
            // Perform bisection iterations
            for (int it = 0; it < BISECTION_MAX_ITERATIONS; it++) {
                double m = (a + b) / 2.0;
                double fm = evaluator.evaluate(leftExpr, m) - evaluator.evaluate(rightExpr, m);
                
                if (!isValidValue(fm)) break;
                
                if (Math.abs(fm) < BISECTION_EPSILON) { 
                    root = m; 
                    break; 
                }
                
                if ((fa > 0 && fm < 0) || (fa < 0 && fm > 0)) { 
                    b = m; 
                } else { 
                    a = m; 
                    fa = fm; 
                }
            }
            
            if (Double.isNaN(root)) {
                root = (a + b) / 2.0;
            }
        } catch (Exception ex) {
            root = (a + b) / 2.0;
        }
        
        return root;
    }
    
    /**
     * Calculate the number of samples based on screen width
     * @param screenWidth Width in pixels
     * @return Number of samples to use
     */
    private int calculateSampleCount(int screenWidth) {
        return Math.min(MAX_INTERSECTION_SAMPLES, 
                       Math.max(MIN_INTERSECTION_SAMPLES, screenWidth));
    }
    
    /**
     * Check if a value is valid (not NaN or infinite)
     * @param value Value to check
     * @return true if valid
     */
    private boolean isValidValue(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }
    
    /**
     * Check if there's a sign change between two values
     * @param prev Previous value
     * @param curr Current value
     * @return true if sign change detected
     */
    private boolean hasSignChange(double prev, double curr) {
        return prev == 0.0 || curr == 0.0 || 
               (prev > 0 && curr < 0) || (prev < 0 && curr > 0);
    }
    
    /**
     * Check if a point is a duplicate of any existing point
     * @param point Point to check
     * @param existingPoints List of existing points
     * @return true if duplicate
     */
    private boolean isDuplicate(Point2D.Double point, List<Point2D.Double> existingPoints) {
        for (Point2D.Double existing : existingPoints) {
            if (Math.abs(existing.x - point.x) < DEDUPLICATION_THRESHOLD) {
                return true;
            }
        }
        return false;
    }
}
