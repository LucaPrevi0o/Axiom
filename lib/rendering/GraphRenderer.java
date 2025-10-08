package lib.rendering;

import lib.constants.RenderingConstants;
import lib.core.ExpressionEvaluator;
import lib.model.GraphBounds;
import lib.model.GraphFunction;
import lib.rendering.pipeline.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

/**
 * Main coordinator for graph rendering
 * Now delegates to specialized renderers
 */
public class GraphRenderer {
    
    private final GridRenderer gridRenderer;
    private final AxisRenderer axisRenderer;
    private final FunctionPlotter functionPlotter;
    private final RegionRenderer regionRenderer;
    private final IntersectionFinder intersectionFinder;
    private final GraphBounds bounds;
    
    private Map<String, List<Point2D.Double>> namedIntersectionPoints;
    
    /**
     * Create a graph renderer
     */
    public GraphRenderer(ExpressionEvaluator evaluator, IntersectionFinder intersectionFinder,
                        GraphBounds bounds) {
        this.bounds = bounds;
        this.intersectionFinder = intersectionFinder;
        
        // Create specialized renderers
        this.gridRenderer = new GridRenderer();
        this.axisRenderer = new AxisRenderer();
        this.functionPlotter = new FunctionPlotter(evaluator, bounds);
        this.regionRenderer = new RegionRenderer(evaluator, intersectionFinder, bounds);
    }
    
    /**
     * Set named intersection points cache
     */
    public void setNamedIntersectionPoints(Map<String, List<Point2D.Double>> points) {
        this.namedIntersectionPoints = points;
    }
    
    /**
     * Main render method - coordinates all rendering
     */
    public void render(Graphics2D g2, List<GraphFunction> functions, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Delegate to specialized renderers
        gridRenderer.drawGrid(g2, bounds, width, height);
        axisRenderer.drawAxes(g2, bounds, width, height);
        
        // Draw regions first (behind functions)
        drawRegions(g2, functions, width, height);
        
        // Draw functions and intersections on top
        drawFunctions(g2, functions, width, height);
        drawIntersections(g2, functions, width, height);
    }
    
    /**
     * Draw all regions
     */
    private void drawRegions(Graphics2D g2, List<GraphFunction> functions,
                            int width, int height) {
        for (GraphFunction function : functions) {
            if (function.isRegion()) {
                String leftExpr = function.getLhsExpr();
                String rightExpr = function.getRhsExpr();
                String operator = function.getRegionOperator();
                Color color = function.getColor();
                
                regionRenderer.renderRegion(g2, leftExpr, operator, rightExpr, color, width, height);
            }
        }
    }
    
    /**
     * Draw all functions
     */
    private void drawFunctions(Graphics2D g2, List<GraphFunction> functions, 
                               int width, int height) {
        for (GraphFunction function : functions) {
            if (!function.isIntersection() && !function.isRegion()) {
                String expr = function.getExpression();
                Color color = function.getColor();
                
                // Check if this is a named intersection reference
                if (expr != null && namedIntersectionPoints != null) {
                    if (tryDrawNamedIntersection(g2, expr, color, width, height)) {
                        continue;
                    }
                }
                
                // Draw regular function
                functionPlotter.plotFunction(g2, expr, color, width, height);
            }
        }
    }
    
    /**
     * Try to draw a named intersection function
     */
    private boolean tryDrawNamedIntersection(Graphics2D g2, String expr, Color color,
                                            int width, int height) {
        // Check for patterns like: name(x), name(x)+C, name(x-C)
        java.util.regex.Pattern p1 = java.util.regex.Pattern.compile(
            "^\\s*([A-Za-z_]\\w*)\\s*\\(\\s*x\\s*\\)\\s*([+-]\\s*\\d+(?:\\.\\d+)?)?\\s*$"
        );
        java.util.regex.Matcher m = p1.matcher(expr);
        
        if (m.matches()) {
            String name = m.group(1);
            String shiftStr = m.group(2);
            
            List<Point2D.Double> points = namedIntersectionPoints.get(name.toLowerCase());
            if (points != null) {
                drawTransformedPoints(g2, points, shiftStr, color, width, height);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Draw transformed intersection points
     */
    private void drawTransformedPoints(Graphics2D g2, List<Point2D.Double> points,
                                      String shiftStr, Color color, int width, int height) {
        double shift = 0.0;
        if (shiftStr != null) {
            shift = Double.parseDouble(shiftStr.replaceAll("\\s+", ""));
        }
        
        g2.setColor(color);
        g2.setStroke(RenderingConstants.FUNCTION_STROKE);
        
        // Sort and draw
        List<Point2D.Double> sorted = new java.util.ArrayList<>(points);
        sorted.sort((a, b) -> Double.compare(a.x, b.x));
        
        java.awt.geom.Path2D path = new java.awt.geom.Path2D.Double();
        boolean first = true;
        
        for (Point2D.Double p : sorted) {
            double ty = p.y + shift;
            int sx = bounds.xToScreen(p.x, width);
            int sy = bounds.yToScreen(ty, height);
            
            if (first) {
                path.moveTo(sx, sy);
                first = false;
            } else {
                path.lineTo(sx, sy);
            }
        }
        
        g2.draw(path);
    }
    
    /**
     * Draw intersection points
     */
    private void drawIntersections(Graphics2D g2, List<GraphFunction> functions,
                                   int width, int height) {
        for (GraphFunction gf : functions) {
            if (!gf.isIntersection()) continue;
            
            g2.setColor(gf.getColor());
            
            List<Point2D.Double> intersections = intersectionFinder.findIntersections(
                gf.getLhsExpr(), gf.getRhsExpr(),
                bounds.getMinX(), bounds.getMaxX(), width
            );
            
            for (Point2D.Double point : intersections) {
                int sx = bounds.xToScreen(point.x, width);
                int sy = bounds.yToScreen(point.y, height);
                int r = RenderingConstants.INTERSECTION_POINT_RADIUS;
                g2.fillOval(sx - r, sy - r, r * 2, r * 2);
            }
        }
    }
}