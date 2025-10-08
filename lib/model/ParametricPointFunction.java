package lib.model;

import lib.core.ExpressionEvaluator;
import lib.util.ValidationUtils;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A function representing a single point with coordinates that can be
 * mathematical expressions or parameters (e.g., P=(a,0) where 'a' is a parameter).
 * Unlike PointSetFunction, this re-evaluates coordinates each time.
 */
public class ParametricPointFunction extends Function {
    
    private final String xExpression;
    private final String yExpression;
    private final ExpressionEvaluator evaluator;
    
    /**
     * Create a parametric point function
     * @param name Point name (e.g., "P", "A")
     * @param xExpression Expression for X coordinate (can be a number or parameter name)
     * @param yExpression Expression for Y coordinate (can be a number or parameter name)
     * @param color Display color
     * @param evaluator Expression evaluator to use
     */
    public ParametricPointFunction(String name, String xExpression, String yExpression,
                                   Color color, ExpressionEvaluator evaluator) {
        super(name, color);
        this.xExpression = xExpression;
        this.yExpression = yExpression;
        this.evaluator = evaluator;
    }
    
    /**
     * Get the X coordinate expression
     */
    public String getXExpression() {
        return xExpression;
    }
    
    /**
     * Get the Y coordinate expression
     */
    public String getYExpression() {
        return yExpression;
    }
    
    @Override
    protected List<Point2D.Double> computePoints(GraphBounds bounds, int width, int height) {
        List<Point2D.Double> points = new ArrayList<>();
        
        try {
            // Evaluate both coordinates using evaluateConstant
            // This allows expressions like f(4) to work correctly without x substitution
            double x = evaluator.evaluateConstant(xExpression);
            double y = evaluator.evaluateConstant(yExpression);
            
            // Only add if both coordinates are valid
            if (ValidationUtils.areAllValid(x, y)) {
                points.add(new Point2D.Double(x, y));
            }
        } catch (Exception e) {
            // If evaluation fails, return empty list (point not visible)
        }
        
        return points;
    }
    
    @Override
    public String getDisplayString() {
        return getName() + " = (" + xExpression + ", " + yExpression + ")";
    }
    
    @Override
    public boolean isContinuous() {
        return false; // Single point, not a curve
    }
    
    /**
     * Check if this point is draggable (i.e., depends on at least one parameter)
     * @return true if the point uses any parameters
     */
    public boolean isDraggable() {
        return hasParameterInExpression(xExpression) || hasParameterInExpression(yExpression);
    }
    
    /**
     * Get the parameter name used in X coordinate (if any)
     * For simple cases like "a" or "a+1", returns "a"
     * For complex cases or no parameter, returns null
     * @return Parameter name or null
     */
    public String getParameterInX() {
        return extractSingleParameter(xExpression);
    }
    
    /**
     * Get the parameter name used in Y coordinate (if any)
     * @return Parameter name or null
     */
    public String getParameterInY() {
        return extractSingleParameter(yExpression);
    }
    
    /**
     * Check if an expression contains any parameter references
     */
    private boolean hasParameterInExpression(String expression) {
        // A parameter is a word character sequence that is not a function call
        // and is not 'x' (which is the variable)
        String expr = expression.toLowerCase();
        
        // Look for word boundaries followed by letters (potential parameters)
        // Exclude common function names and 'x'
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b([a-z_]\\w*)\\b");
        java.util.regex.Matcher matcher = pattern.matcher(expr);
        
        while (matcher.find()) {
            String token = matcher.group(1);
            // Skip 'x' and common math functions
            if (!token.equals("x") && !isMathFunction(token)) {
                return true; // Found a parameter
            }
        }
        
        return false;
    }
    
    /**
     * Extract a single parameter name from an expression.
     * Returns null if the expression doesn't contain exactly one parameter,
     * or if it's too complex.
     */
    private String extractSingleParameter(String expression) {
        String expr = expression.toLowerCase().trim();
        
        // Pattern to find parameter names (word characters that aren't functions)
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b([a-z_]\\w*)\\b");
        java.util.regex.Matcher matcher = pattern.matcher(expr);
        
        String foundParam = null;
        
        while (matcher.find()) {
            String token = matcher.group(1);
            // Skip 'x' and common math functions
            if (!token.equals("x") && !isMathFunction(token)) {
                if (foundParam != null && !foundParam.equals(token)) {
                    // Multiple different parameters found
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
}
