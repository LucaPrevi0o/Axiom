package lib.model.function.composite;

import lib.model.function.base.PlottableFunction;
import lib.model.domain.GraphBounds;
import lib.constants.RenderingConstants;
import lib.core.evaluation.ExpressionEvaluator;
import lib.util.ValidationUtils;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A function representing an inequation (inequality) region.
 * For syntax like (f(x) >= g(x)), this computes the boundary and can be filled.
 * Renamed from RegionFunction for better semantic clarity.
 */
public class InequationFunction extends PlottableFunction {
    
    private final String leftExpression;
    private final String rightExpression;
    private final String operator; // ">=", "<=", ">", "<"
    private final ExpressionEvaluator evaluator;
    
    /**
     * Create an inequation function
     * @param leftExpression Left side of inequality
     * @param operator Comparison operator (">=", "<=", ">", "<")
     * @param rightExpression Right side of inequality
     * @param color Display color
     * @param evaluator Expression evaluator to use
     */
    public InequationFunction(String leftExpression, String operator, String rightExpression,
                             Color color, ExpressionEvaluator evaluator) {
        this(null, leftExpression, operator, rightExpression, color, evaluator);
    }
    
    /**
     * Create a named inequation function
     * @param name Function name
     * @param leftExpression Left side of inequality
     * @param operator Comparison operator
     * @param rightExpression Right side of inequality
     * @param color Display color
     * @param evaluator Expression evaluator to use
     */
    public InequationFunction(String name, String leftExpression, String operator, 
                             String rightExpression, Color color, ExpressionEvaluator evaluator) {
        super(name, color);
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
        this.evaluator = evaluator;
    }
    
    /**
     * Get the left expression
     */
    public String getLeftExpression() {
        return leftExpression;
    }
    
    /**
     * Get the comparison operator
     */
    public String getOperator() {
        return operator;
    }
    
    /**
     * Get the right expression
     */
    public String getRightExpression() {
        return rightExpression;
    }
    
    /**
     * Evaluate the left expression at a given x
     * @param x X coordinate
     * @return Evaluated y value
     * @throws Exception if evaluation fails
     */
    public double evaluateLeft(double x) throws Exception {
        return evaluator.evaluate(leftExpression, x);
    }
    
    /**
     * Evaluate the right expression at a given x
     * @param x X coordinate
     * @return Evaluated y value
     * @throws Exception if evaluation fails
     */
    public double evaluateRight(double x) throws Exception {
        return evaluator.evaluate(rightExpression, x);
    }
    
    /**
     * Evaluate if a point satisfies the inequality
     * @param x X coordinate
     * @return true if point is in the region
     */
    public boolean satisfiesInequality(double x) {
        try {
            double leftValue = evaluator.evaluate(leftExpression, x);
            double rightValue = evaluator.evaluate(rightExpression, x);
            
            if (!ValidationUtils.areAllValid(leftValue, rightValue)) {
                return false;
            }
            
            switch (operator) {
                case ">=": return leftValue >= rightValue;
                case "<=": return leftValue <= rightValue;
                case ">":  return leftValue > rightValue;
                case "<":  return leftValue < rightValue;
                default:   return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected List<Point2D.Double> computePoints(GraphBounds bounds, int width, int height) {
        // Compute the boundary curve (where leftExpression = rightExpression)
        List<Point2D.Double> boundaryPoints = new ArrayList<>();
        
        int sampleCount = RenderingConstants.REGION_SAMPLE_COUNT;
        double xMin = bounds.getMinX();
        double xMax = bounds.getMaxX();
        double step = (xMax - xMin) / sampleCount;
        
        for (int i = 0; i <= sampleCount; i++) {
            double x = xMin + i * step;
            
            try {
                double leftY = evaluator.evaluate(leftExpression, x);
                double rightY = evaluator.evaluate(rightExpression, x);
                
                // For boundary, we plot the difference (should be near zero at boundary)
                // But for regions, we typically want to show both curves
                if (ValidationUtils.areAllValid(leftY, rightY)) {
                    boundaryPoints.add(new Point2D.Double(x, leftY));
                }
            } catch (Exception e) {
                // Skip invalid points
            }
        }
        
        return boundaryPoints;
    }
    
    @Override
    public String getDisplayString() {
        return "(" + leftExpression + " " + operator + " " + rightExpression + ")";
    }
    
    @Override
    public boolean isContinuous() {
        return true; // Boundary is continuous
    }
}
