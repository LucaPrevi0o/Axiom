package lib.model.function.expression;

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
 * A regular function defined by a mathematical expression: y = f(x)
 * This is the standard function type for plotting curves.
 * Renamed from ExpressionFunction for better semantic clarity.
 */
public class RegularFunction extends PlottableFunction {
    
    private final String expression;
    private final ExpressionEvaluator evaluator;
    
    /**
     * Create a function from a mathematical expression
     * @param expression Mathematical expression (e.g., "x^2 + 2*x - 3")
     * @param color Display color
     * @param evaluator Expression evaluator to use
     */
    public RegularFunction(String expression, Color color, ExpressionEvaluator evaluator) {
        this(null, expression, color, evaluator);
    }
    
    /**
     * Create a named function from a mathematical expression
     * @param name Function name (e.g., "f", "g")
     * @param expression Mathematical expression
     * @param color Display color
     * @param evaluator Expression evaluator to use
     */
    public RegularFunction(String name, String expression, Color color, ExpressionEvaluator evaluator) {
        super(name, color);
        this.expression = expression;
        this.evaluator = evaluator;
    }
    
    /**
     * Get the mathematical expression
     */
    public String getExpression() {
        return expression;
    }
    
    @Override
    protected List<Point2D.Double> computePoints(GraphBounds bounds, int width, int height) {
        List<Point2D.Double> points = new ArrayList<>();
        
        // Adaptive sampling based on zoom level
        int sampleCount = calculateAdaptiveSamples(bounds, width);
        double xMin = bounds.getMinX();
        double xMax = bounds.getMaxX();
        double step = (xMax - xMin) / sampleCount;
        
        for (int i = 0; i <= sampleCount; i++) {
            double x = xMin + i * step;
            
            try {
                double y = evaluator.evaluate(expression, x);
                
                if (ValidationUtils.isValidValue(y)) {
                    points.add(new Point2D.Double(x, y));
                }
            } catch (Exception e) {
                // Skip invalid points
            }
        }
        
        return points;
    }
    
    /**
     * Calculate adaptive sample count based on zoom level
     */
    private int calculateAdaptiveSamples(GraphBounds bounds, int pixelWidth) {
        double viewRangeX = bounds.getRangeX();
        double baseSamples = pixelWidth;
        
        // Increase samples when zoomed in (smaller range)
        double zoomFactor = 40.0 / Math.max(1.0, viewRangeX);
        int samples = (int) (baseSamples * Math.sqrt(zoomFactor));
        
        // Clamp to reasonable limits
        return ValidationUtils.clamp(samples, 
            RenderingConstants.MIN_SAMPLES, 
            RenderingConstants.MAX_SAMPLES);
    }
    
    @Override
    public String getDisplayString() {
        return expression;
    }
    
    @Override
    public boolean isContinuous() {
        return true;
    }
}
