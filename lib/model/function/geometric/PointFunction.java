package lib.model.function.geometric;

import lib.model.function.base.PlottableFunction;
import lib.model.function.definition.SetFunction;
import lib.model.domain.GraphBounds;
import lib.core.evaluation.ExpressionEvaluator;
import lib.util.ValidationUtils;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A unified function representing point(s) on the graph.
 * Supports both:
 * - Parametric points with expression coordinates (e.g., P=(a,0) or P=(S,0) where S is a set)
 * - Static point sets with fixed coordinates
 * 
 * This unifies ParametricPointFunction and PointSetFunction into a single class.
 */
public class PointFunction extends PlottableFunction {
    
    // Parametric mode fields
    private final String xExpression;
    private final String yExpression;
    private final ExpressionEvaluator evaluator;
    private Map<String, SetFunction> availableSets;
    
    // Static mode fields
    private final List<Point2D.Double> staticPoints;
    
    // Mode flag
    private final boolean isParametric;
    
    /**
     * Create a parametric point function with expression coordinates
     * @param name Point name (e.g., "P", "A")
     * @param xExpression Expression for X coordinate (number, parameter, or set name)
     * @param yExpression Expression for Y coordinate (number, parameter, or set name)
     * @param color Display color
     * @param evaluator Expression evaluator to use
     */
    public PointFunction(String name, String xExpression, String yExpression,
                        Color color, ExpressionEvaluator evaluator) {
        super(name, color);
        this.xExpression = xExpression;
        this.yExpression = yExpression;
        this.evaluator = evaluator;
        this.availableSets = new java.util.HashMap<>();
        this.staticPoints = null;
        this.isParametric = true;
    }
    
    /**
     * Create a static point set function with fixed coordinates
     * @param name Function name
     * @param points List of points in graph coordinates
     * @param color Display color
     */
    public PointFunction(String name, List<Point2D.Double> points, Color color) {
        super(name, color);
        this.xExpression = null;
        this.yExpression = null;
        this.evaluator = null;
        this.availableSets = null;
        this.staticPoints = new ArrayList<>(points); // Defensive copy
        this.isParametric = false;
    }
    
    /**
     * Create a static point set from coordinate arrays
     * @param name Function name
     * @param xValues Array of x coordinates
     * @param yValues Array of y coordinates
     * @param color Display color
     */
    public PointFunction(String name, double[] xValues, double[] yValues, Color color) {
        super(name, color);
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("xValues and yValues must have the same length");
        }
        this.xExpression = null;
        this.yExpression = null;
        this.evaluator = null;
        this.availableSets = null;
        this.staticPoints = new ArrayList<>();
        for (int i = 0; i < xValues.length; i++) {
            this.staticPoints.add(new Point2D.Double(xValues[i], yValues[i]));
        }
        this.isParametric = false;
    }
    
    /**
     * Check if this is a parametric point (vs static point set)
     */
    public boolean isParametric() {
        return isParametric;
    }
    
    /**
     * Set the available sets that can be referenced in parametric coordinates
     * @param sets Map of set name to SetFunction
     */
    public void setAvailableSets(Map<String, SetFunction> sets) {
        if (!isParametric) return;
        this.availableSets = sets != null ? sets : new java.util.HashMap<>();
        invalidateCache(); // Recompute points when sets change
    }
    
    /**
     * Get the X coordinate expression (parametric mode only)
     */
    public String getXExpression() {
        return xExpression;
    }
    
    /**
     * Get the Y coordinate expression (parametric mode only)
     */
    public String getYExpression() {
        return yExpression;
    }
    
    /**
     * Add a point to static point set (static mode only)
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void addPoint(double x, double y) {
        if (isParametric) {
            throw new UnsupportedOperationException("Cannot add points to parametric point function");
        }
        staticPoints.add(new Point2D.Double(x, y));
        invalidateCache();
    }
    
    /**
     * Clear all points (static mode only)
     */
    public void clearPoints() {
        if (isParametric) {
            throw new UnsupportedOperationException("Cannot clear points in parametric point function");
        }
        staticPoints.clear();
        invalidateCache();
    }
    
    /**
     * Get the number of points (static mode only)
     */
    public int getPointCount() {
        return isParametric ? 0 : staticPoints.size();
    }
    
    @Override
    protected List<Point2D.Double> computePoints(GraphBounds bounds, int width, int height) {
        if (!isParametric) {
            // Static mode: return the fixed point set
            return new ArrayList<>(staticPoints);
        }
        
        // Parametric mode: evaluate expressions
        List<Point2D.Double> points = new ArrayList<>();
        
        // Check if either coordinate references a set
        SetFunction xSet = getReferencedSet(xExpression);
        SetFunction ySet = getReferencedSet(yExpression);
        
        if (xSet != null && ySet != null) {
            // Both coordinates are sets - create cartesian product
            for (double x : xSet.getValues()) {
                for (double y : ySet.getValues()) {
                    if (ValidationUtils.areAllValid(x, y)) {
                        points.add(new Point2D.Double(x, y));
                    }
                }
            }
        } else if (xSet != null) {
            // X is a set, Y is an expression
            double yVal = evaluateCoordinate(yExpression);
            if (ValidationUtils.isValidValue(yVal)) {
                for (double x : xSet.getValues()) {
                    points.add(new Point2D.Double(x, yVal));
                }
            }
        } else if (ySet != null) {
            // Y is a set, X is an expression
            double xVal = evaluateCoordinate(xExpression);
            if (ValidationUtils.isValidValue(xVal)) {
                for (double y : ySet.getValues()) {
                    points.add(new Point2D.Double(xVal, y));
                }
            }
        } else {
            // Both are simple expressions
            double x = evaluateCoordinate(xExpression);
            double y = evaluateCoordinate(yExpression);
            if (ValidationUtils.areAllValid(x, y)) {
                points.add(new Point2D.Double(x, y));
            }
        }
        
        return points;
    }
    
    /**
     * Check if a coordinate expression references a set
     */
    private SetFunction getReferencedSet(String expr) {
        if (expr == null || availableSets == null) {
            return null;
        }
        
        String trimmed = expr.trim();
        
        // Check if it's a simple set reference (just the set name)
        if (availableSets.containsKey(trimmed)) {
            return availableSets.get(trimmed);
        }
        
        return null;
    }
    
    /**
     * Evaluate a coordinate expression to a numeric value
     */
    private double evaluateCoordinate(String expr) {
        try {
            return evaluator.evaluate(expr, 0); // x=0 for parameter evaluation
        } catch (Exception e) {
            return Double.NaN;
        }
    }
    
    /**
     * Check if this point is draggable (parametric mode only)
     * @return true if the point uses any parameters
     */
    public boolean isDraggable() {
        if (!isParametric) return false;
        return hasParameterInExpression(xExpression) || hasParameterInExpression(yExpression);
    }
    
    /**
     * Get the parameter name used in X coordinate (parametric mode only)
     * @return Parameter name or null
     */
    public String getParameterInX() {
        if (!isParametric) return null;
        return extractSingleParameter(xExpression);
    }
    
    /**
     * Get the parameter name used in Y coordinate (parametric mode only)
     * @return Parameter name or null
     */
    public String getParameterInY() {
        if (!isParametric) return null;
        return extractSingleParameter(yExpression);
    }
    
    /**
     * Check if an expression contains any parameter references
     */
    private boolean hasParameterInExpression(String expression) {
        String expr = expression.toLowerCase();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b([a-z_]\\w*)\\b");
        java.util.regex.Matcher matcher = pattern.matcher(expr);
        
        while (matcher.find()) {
            String token = matcher.group(1);
            if (!token.equals("x") && !isMathFunction(token)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Extract a single parameter name from an expression
     */
    private String extractSingleParameter(String expression) {
        String expr = expression.toLowerCase().trim();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b([a-z_]\\w*)\\b");
        java.util.regex.Matcher matcher = pattern.matcher(expr);
        
        String foundParam = null;
        while (matcher.find()) {
            String token = matcher.group(1);
            if (!token.equals("x") && !isMathFunction(token)) {
                if (foundParam != null && !foundParam.equals(token)) {
                    return null;
                }
                foundParam = token;
            }
        }
        return foundParam;
    }
    
    /**
     * Check if a token is a known math function name
     */
    private boolean isMathFunction(String token) {
        return token.equals("sin") || token.equals("cos") || token.equals("tan") ||
               token.equals("sqrt") || token.equals("abs") || token.equals("log") ||
               token.equals("ln") || token.equals("exp") || token.equals("floor") ||
               token.equals("ceil");
    }
    
    @Override
    public String getDisplayString() {
        if (!isParametric) {
            // Static mode: show point count
            return name + " (" + staticPoints.size() + " points)";
        }
        
        // Parametric mode: show coordinate expressions
        return name + "=(" + xExpression + ", " + yExpression + ")";
    }
    
    @Override
    public boolean isContinuous() {
        return false; // Points are always discrete
    }
}
