package lib.rendering;

import lib.model.function.base.PlottableFunction;
import lib.model.function.composite.InequationFunction;
import lib.model.domain.GraphBounds;
import lib.constants.RenderingConstants;
import lib.core.evaluation.ExpressionEvaluator;
import lib.rendering.pipeline.*;
import lib.util.ValidationUtils;
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
    public void render(Graphics2D g2, List<PlottableFunction> functions, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw grid and axes
        gridRenderer.drawGrid(g2, bounds, width, height);
        axisRenderer.drawAxes(g2, bounds, width, height);
        
        // Draw all functions using polymorphism
        for (PlottableFunction function : functions) {
            if (!function.isEnabled()) continue;
            
            renderFunction(g2, function, width, height);
        }
    }
    
    /**
     * Render a single function using polymorphism.
     * The function itself knows how to compute its points.
     */
    private void renderFunction(Graphics2D g2, PlottableFunction function, int width, int height) {
        // Get points from the function (uses caching internally)
        List<Point2D.Double> points = function.getPoints(bounds, width, height);
        
        if (points.isEmpty()) return;
        
        g2.setColor(function.getColor());
        
        // Handle inequation functions specially (need filling)
        if (function instanceof InequationFunction) {
            renderRegion(g2, (InequationFunction) function, points, width, height);
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
     * Render an inequation function with filling
     */
    private void renderRegion(Graphics2D g2, InequationFunction function,
                             List<Point2D.Double> points, int width, int height) {
        // Draw the boundary curves
        g2.setColor(function.getColor());
        g2.setStroke(RenderingConstants.BORDER_STROKE);
        
        // Sample both curves
        int sampleCount = RenderingConstants.REGION_SAMPLE_COUNT;
        double xMin = bounds.getMinX();
        double xMax = bounds.getMaxX();
        double xStep = (xMax - xMin) / sampleCount;
        
        // Draw both boundary curves
        java.awt.geom.Path2D leftPath = new java.awt.geom.Path2D.Double();
        java.awt.geom.Path2D rightPath = new java.awt.geom.Path2D.Double();
        boolean leftStarted = false;
        boolean rightStarted = false;
        
        try {
            for (int i = 0; i <= sampleCount; i++) {
                double x = xMin + i * xStep;
                
                try {
                    double leftY = function.evaluateLeft(x);
                    double rightY = function.evaluateRight(x);
                    
                    if (ValidationUtils.areAllValid(leftY, rightY)) {
                        int sx = bounds.xToScreen(x, width);
                        int leftSy = bounds.yToScreen(leftY, height);
                        int rightSy = bounds.yToScreen(rightY, height);
                        
                        if (!leftStarted) {
                            leftPath.moveTo(sx, leftSy);
                            leftStarted = true;
                        } else {
                            leftPath.lineTo(sx, leftSy);
                        }
                        
                        if (!rightStarted) {
                            rightPath.moveTo(sx, rightSy);
                            rightStarted = true;
                        } else {
                            rightPath.lineTo(sx, rightSy);
                        }
                    }
                } catch (Exception e) {
                    // Skip this point
                }
            }
            
            // Draw the boundary curves
            g2.draw(leftPath);
            g2.draw(rightPath);
            
            // Fill the region between the curves
            Color fillColor = new Color(
                function.getColor().getRed(),
                function.getColor().getGreen(),
                function.getColor().getBlue(),
                RenderingConstants.FILL_ALPHA
            );
            g2.setColor(fillColor);
            
            // Create filled polygon between curves
            for (int i = 0; i < sampleCount; i++) {
                double x1 = xMin + i * xStep;
                double x2 = xMin + (i + 1) * xStep;
                
                try {
                    double leftY1 = function.evaluateLeft(x1);
                    double rightY1 = function.evaluateRight(x1);
                    double leftY2 = function.evaluateLeft(x2);
                    double rightY2 = function.evaluateRight(x2);
                    
                    if (ValidationUtils.areAllValid(leftY1, rightY1, leftY2, rightY2)) {
                        // Check if this region satisfies the inequality
                        boolean satisfies1 = function.satisfiesInequality(x1);
                        boolean satisfies2 = function.satisfiesInequality(x2);
                        
                        if (satisfies1 || satisfies2) {
                            // Create a quad between the two curves
                            int[] xPoints = new int[4];
                            int[] yPoints = new int[4];
                            
                            xPoints[0] = bounds.xToScreen(x1, width);
                            yPoints[0] = bounds.yToScreen(leftY1, height);
                            
                            xPoints[1] = bounds.xToScreen(x2, width);
                            yPoints[1] = bounds.yToScreen(leftY2, height);
                            
                            xPoints[2] = bounds.xToScreen(x2, width);
                            yPoints[2] = bounds.yToScreen(rightY2, height);
                            
                            xPoints[3] = bounds.xToScreen(x1, width);
                            yPoints[3] = bounds.yToScreen(rightY1, height);
                            
                            g2.fillPolygon(xPoints, yPoints, 4);
                        }
                    }
                } catch (Exception e) {
                    // Skip this segment
                }
            }
        } catch (Exception e) {
            // Error rendering region
        }
    }
}