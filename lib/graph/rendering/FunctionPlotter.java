package lib.graph.rendering;

import lib.expression.ExpressionEvaluator;
import lib.graph.GraphBounds;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Handles plotting individual mathematical functions
 */
public class FunctionPlotter {
    
    private static final int DEFAULT_STROKE_WIDTH = 2;
    private static final double DEFAULT_VIEW_RANGE = 20.0;
    private static final int MIN_SAMPLES = 50;
    private static final int MAX_SAMPLES = 5000;
    
    private final ExpressionEvaluator evaluator;
    private final GraphBounds bounds;
    
    /**
     * Create a function plotter
     * @param evaluator Expression evaluator
     * @param bounds Graph bounds
     */
    public FunctionPlotter(ExpressionEvaluator evaluator, GraphBounds bounds) {
        this.evaluator = evaluator;
        this.bounds = bounds;
    }
    
    /**
     * Plot a mathematical function
     * @param g2 Graphics context
     * @param expression Function expression
     * @param color Line color
     * @param width Panel width
     * @param height Panel height
     */
    public void plotFunction(Graphics2D g2, String expression, Color color, 
                            int width, int height) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(DEFAULT_STROKE_WIDTH));
        
        Path2D path = new Path2D.Double();
        int sampleCount = calculateAdaptiveSamples(width);
        double step = (double) width / (double) sampleCount;
        boolean firstPoint = true;
        
        for (double sx = 0.0; sx < width; sx += step) {
            int screenX = (int) Math.round(sx);
            double x = bounds.screenToX(screenX, width);
            
            try {
                double y = evaluator.evaluate(expression, x);
                
                if (isValidValue(y)) {
                    int screenY = bounds.yToScreen(y, height);
                    
                    if (firstPoint) {
                        path.moveTo(screenX, screenY);
                        firstPoint = false;
                    } else {
                        path.lineTo(screenX, screenY);
                    }
                } else {
                    firstPoint = true;
                }
            } catch (Exception e) {
                firstPoint = true;
            }
        }
        
        g2.draw(path);
    }
    
    /**
     * Calculate adaptive sample count based on zoom level
     */
    private int calculateAdaptiveSamples(int pixelWidth) {
        double viewRangeX = bounds.getRangeX();
        double baseSamples = pixelWidth;
        double zoomMultiplier = Math.max(1.0, DEFAULT_VIEW_RANGE / viewRangeX);
        zoomMultiplier = Math.min(10.0, zoomMultiplier);
        
        int sampleCount = (int) Math.round(baseSamples * zoomMultiplier);
        return Math.max(MIN_SAMPLES, Math.min(MAX_SAMPLES, sampleCount));
    }
    
    /**
     * Check if a value is valid (not NaN or infinite)
     */
    private boolean isValidValue(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }
}