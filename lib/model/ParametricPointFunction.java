package lib.model;

import lib.core.ExpressionEvaluator;
import lib.util.ValidationUtils;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

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
}
