package lib.rendering;

import lib.constants.RenderingConstants;
import lib.core.ExpressionEvaluator;
import lib.model.*;
import lib.rendering.pipeline.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Main coordinator for graph rendering.
 * Now uses polymorphism - each Function subclass knows how to compute its own points.
 */
public class GraphRenderer {
    
    private final GridRenderer gridRenderer;
    private final AxisRenderer axisRenderer;
    private final GraphBounds bounds;
    
    /**
     * Create a graph renderer
     */
    public GraphRenderer(ExpressionEvaluator evaluator, IntersectionFinder intersectionFinder,
                        GraphBounds bounds) {
        this.bounds = bounds;
        
        // Create specialized renderers
        this.gridRenderer = new GridRenderer();
        this.axisRenderer = new AxisRenderer();
    }
    
    /**
     * Main render method - coordinates all rendering using polymorphism
     */
    public void render(Graphics2D g2, List<Function> functions, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw grid and axes
        gridRenderer.drawGrid(g2, bounds, width, height);
        axisRenderer.drawAxes(g2, bounds, width, height);
        
        // Draw all functions using polymorphism
        for (Function function : functions) {
            if (!function.isEnabled()) continue;
            
            renderFunction(g2, function, width, height);
        }
    }
    
    /**
     * Render a single function using polymorphism.
     * The function itself knows how to compute its points.
     */
    private void renderFunction(Graphics2D g2, Function function, int width, int height) {
        // Get points from the function (uses caching internally)
        List<Point2D.Double> points = function.getPoints(bounds, width, height);
        
        if (points.isEmpty()) return;
        
        g2.setColor(function.getColor());
        
        // Handle region functions specially (need filling)
        if (function.isRegion() && function instanceof RegionFunction) {
            renderRegion(g2, (RegionFunction) function, points, width, height);
            return;
        }
        
        // Render as continuous curve or discrete points
        if (function.isContinuous()) {
            renderContinuousCurve(g2, points, width, height);
        } else {
            renderDiscretePoints(g2, points, width, height);
        }
    }
    
    /**
     * Render a continuous curve by connecting points
     */
    private void renderContinuousCurve(Graphics2D g2, List<Point2D.Double> points, 
                                       int width, int height) {
        g2.setStroke(RenderingConstants.FUNCTION_STROKE);
        
        Point2D.Double prevPoint = null;
        for (Point2D.Double point : points) {
            int x = bounds.xToScreen(point.x, width);
            int y = bounds.yToScreen(point.y, height);
            
            if (prevPoint != null) {
                int prevX = bounds.xToScreen(prevPoint.x, width);
                int prevY = bounds.yToScreen(prevPoint.y, height);
                g2.drawLine(prevX, prevY, x, y);
            }
            
            prevPoint = point;
        }
    }
    
    /**
     * Render discrete points (for intersections, scatter plots, etc.)
     */
    private void renderDiscretePoints(Graphics2D g2, List<Point2D.Double> points,
                                      int width, int height) {
        int radius = RenderingConstants.INTERSECTION_POINT_RADIUS;
        
        for (Point2D.Double point : points) {
            int x = bounds.xToScreen(point.x, width);
            int y = bounds.yToScreen(point.y, height);
            
            g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }
    }
    
    /**
     * Render a region function with filling
     */
    private void renderRegion(Graphics2D g2, RegionFunction function,
                             List<Point2D.Double> points, int width, int height) {
        // Draw the boundary curve
        g2.setStroke(RenderingConstants.BORDER_STROKE);
        renderContinuousCurve(g2, points, width, height);
        
        // Fill the region where the inequality is satisfied
        Color fillColor = new Color(
            function.getColor().getRed(),
            function.getColor().getGreen(),
            function.getColor().getBlue(),
            RenderingConstants.FILL_ALPHA
        );
        g2.setColor(fillColor);
        
        // Sample and fill the region
        int sampleCount = RenderingConstants.REGION_SAMPLE_COUNT;
        double xMin = bounds.getMinX();
        double xMax = bounds.getMaxX();
        double yMin = bounds.getMinY();
        double yMax = bounds.getMaxY();
        double xStep = (xMax - xMin) / sampleCount;
        
        for (int i = 0; i < sampleCount; i++) {
            double x = xMin + i * xStep;
            
            if (function.satisfiesInequality(x)) {
                // Draw a vertical line representing this x-slice
                int screenX = bounds.xToScreen(x, width);
                int topY = bounds.yToScreen(yMax, height);
                int bottomY = bounds.yToScreen(yMin, height);
                g2.drawLine(screenX, topY, screenX, bottomY);
            }
        }
    }
}