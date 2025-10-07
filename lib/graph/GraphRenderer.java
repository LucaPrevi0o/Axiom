package lib.graph;

import lib.expression.ExpressionEvaluator;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

/**
 * Handles all rendering operations for the graph including grid, axes,
 * functions, and intersection points.
 */
public class GraphRenderer {
    
    private static final int INTERSECTION_POINT_RADIUS = 4;
    private static final double DEFAULT_VIEW_RANGE = 20.0;
    private static final int ADAPTIVE_SAMPLE_MIN = 50;
    private static final int ADAPTIVE_SAMPLE_MAX = 5000;
    
    private final ExpressionEvaluator evaluator;
    private final IntersectionFinder intersectionFinder;
    private final GraphBounds bounds;
    private Map<String, List<Point2D.Double>> namedIntersectionPoints;
    
    /**
     * Create a graph renderer
     * @param evaluator Expression evaluator
     * @param intersectionFinder Intersection finder
     * @param bounds Graph bounds
     */
    public GraphRenderer(ExpressionEvaluator evaluator, IntersectionFinder intersectionFinder, 
                        GraphBounds bounds) {
        this.evaluator = evaluator;
        this.intersectionFinder = intersectionFinder;
        this.bounds = bounds;
    }
    
    /**
     * Set the named intersection points cache
     * @param namedIntersectionPoints Map of function names to intersection points
     */
    public void setNamedIntersectionPoints(Map<String, List<Point2D.Double>> namedIntersectionPoints) {
        this.namedIntersectionPoints = namedIntersectionPoints;
    }
    
    /**
     * Draw the complete graph
     * @param g2 Graphics context
     * @param functions List of functions to draw
     * @param width Panel width
     * @param height Panel height
     */
    public void render(Graphics2D g2, List<GraphFunction> functions, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawGrid(g2, width, height);
        drawAxes(g2, width, height);
        drawFunctions(g2, functions, width, height);
        drawIntersections(g2, functions, width, height);
    }
    
    /**
     * Draw the grid lines on the graph
     * @param g2 The Graphics2D object
     * @param width Panel width
     * @param height Panel height
     */
    public void drawGrid(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(1));
        
        // Compute nice tick spacing based on view range
        double xRange = bounds.getRangeX();
        double yRange = bounds.getRangeY();
        double xStep = niceStep(xRange, 8);
        double yStep = niceStep(yRange, 8);
        
        // Vertical grid lines
        double startX = Math.floor(bounds.getMinX() / xStep) * xStep;
        for (double gx = startX; gx <= bounds.getMaxX() + 1e-12; gx += xStep) {
            int x = bounds.xToScreen(gx, width);
            g2.drawLine(x, 0, x, height);
        }
        
        // Horizontal grid lines
        double startY = Math.floor(bounds.getMinY() / yStep) * yStep;
        for (double gy = startY; gy <= bounds.getMaxY() + 1e-12; gy += yStep) {
            int y = bounds.yToScreen(gy, height);
            g2.drawLine(0, y, width, y);
        }
    }
    
    /**
     * Draw the X and Y axes on the graph
     * @param g2 The Graphics2D object
     * @param width Panel width
     * @param height Panel height
     */
    public void drawAxes(Graphics2D g2, int width, int height) {
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        
        // X-axis
        int yAxis = bounds.yToScreen(0, height);
        g2.drawLine(0, yAxis, width, yAxis);
        
        // Y-axis
        int xAxis = bounds.xToScreen(0, width);
        g2.drawLine(xAxis, 0, xAxis, height);
        
        // Draw tick marks and labels
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        double xRange = bounds.getRangeX();
        double yRange = bounds.getRangeY();
        double xStep = niceStep(xRange, 8);
        double yStep = niceStep(yRange, 8);
        
        // X-axis ticks
        double startX = Math.floor(bounds.getMinX() / xStep) * xStep;
        for (double gx = startX; gx <= bounds.getMaxX() + 1e-12; gx += xStep) {
            if (Math.abs(gx) < 1e-12) continue; // skip origin
            int x = bounds.xToScreen(gx, width);
            g2.drawLine(x, yAxis - 5, x, yAxis + 5);
            String label = formatTickLabel(gx, xStep);
            g2.drawString(label, x - g2.getFontMetrics().stringWidth(label) / 2, yAxis + 20);
        }
        
        // Y-axis ticks
        double startY = Math.floor(bounds.getMinY() / yStep) * yStep;
        for (double gy = startY; gy <= bounds.getMaxY() + 1e-12; gy += yStep) {
            if (Math.abs(gy) < 1e-12) continue; // skip origin
            int y = bounds.yToScreen(gy, height);
            g2.drawLine(xAxis - 5, y, xAxis + 5, y);
            String label = formatTickLabel(gy, yStep);
            g2.drawString(label, xAxis + 10, y + 5);
        }
    }
    
    /**
     * Draw all functions on the graph
     * @param g2 The Graphics2D object
     * @param functions List of functions to draw
     * @param width Panel width
     * @param height Panel height
     */
    public void drawFunctions(Graphics2D g2, List<GraphFunction> functions, int width, int height) {
        for (GraphFunction function : functions) {
            if (!function.isIntersection()) {
                drawFunction(g2, function, width, height);
            }
        }
    }
    
    /**
     * Draw a single function on the graph
     * @param g2 The Graphics2D object
     * @param function The function to draw
     * @param width Panel width
     * @param height Panel height
     */
    public void drawFunction(Graphics2D g2, GraphFunction function, int width, int height) {
        g2.setColor(function.getColor());
        g2.setStroke(new BasicStroke(2));
        
        Path2D path = new Path2D.Double();
        
        // Check if this function references a named intersection
        String expr = function.getExpression();
        if (expr != null && namedIntersectionPoints != null) {
            if (tryDrawNamedIntersection(g2, path, expr, width, height)) {
                return;
            }
        }
        
        // Draw regular function
        drawRegularFunction(g2, path, function, width, height);
    }
    
    /**
     * Try to draw a named intersection function
     * @return true if successfully drawn as named intersection
     */
    private boolean tryDrawNamedIntersection(Graphics2D g2, Path2D path, String expr, 
                                             int width, int height) {
        // Patterns: name(x), name(x)-C (vertical shift), name(x-C) (horizontal shift)
        Pattern p1 = Pattern.compile("^\\s*([A-Za-z_]\\w*)\\s*\\(\\s*x\\s*\\)\\s*([+-]\\s*\\d+(?:\\.\\d+)?)?\\s*$");
        Pattern p2 = Pattern.compile("^\\s*([A-Za-z_]\\w*)\\s*\\(\\s*x\\s*([+-]\\s*\\d+(?:\\.\\d+)?)\\)\\s*$");
        
        Matcher m1 = p1.matcher(expr);
        Matcher m2 = p2.matcher(expr);
        
        if (m1.matches() || m2.matches()) {
            String name = m1.matches() ? m1.group(1) : m2.group(1);
            String vShiftStr = m1.matches() ? m1.group(2) : null;
            String hShiftStr = m2.matches() ? m2.group(2) : null;
            
            if (name != null) {
                List<Point2D.Double> pts = namedIntersectionPoints.get(name.toLowerCase());
                if (pts != null) {
                    drawTransformedPoints(g2, path, pts, vShiftStr, hShiftStr, width, height);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Draw transformed intersection points
     */
    private void drawTransformedPoints(Graphics2D g2, Path2D path, List<Point2D.Double> pts,
                                       String vShiftStr, String hShiftStr, int width, int height) {
        double vShift = 0.0;
        double hShift = 0.0;
        
        if (vShiftStr != null) {
            vShift = Double.parseDouble(vShiftStr.replaceAll("\\s+", ""));
        }
        if (hShiftStr != null) {
            hShift = Double.parseDouble(hShiftStr.replaceAll("\\s+", ""));
        }
        
        // Sort points by x and draw polyline
        List<Point2D.Double> copy = new java.util.ArrayList<>(pts);
        copy.sort((a, b) -> Double.compare(a.x, b.x));
        
        boolean first = true;
        for (Point2D.Double p : copy) {
            double tx = p.x + hShift;
            double ty = p.y + vShift;
            int sx = bounds.xToScreen(tx, width);
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
     * Draw a regular mathematical function
     */
    private void drawRegularFunction(Graphics2D g2, Path2D path, GraphFunction function,
                                     int width, int height) {
        int sampleCount = calculateAdaptiveSamples(width);
        double step = (double) width / (double) sampleCount;
        boolean firstPoint = true;
        
        for (double sx = 0.0; sx < width; sx += step) {
            int screenX = (int) Math.round(sx);
            double x = bounds.screenToX(screenX, width);
            
            try {
                double y = evaluator.evaluate(function.getExpression(), x);
                
                if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                    int screenY = bounds.yToScreen(y, height);
                    
                    if (firstPoint) {
                        path.moveTo(screenX, screenY);
                        firstPoint = false;
                    } else {
                        path.lineTo(screenX, screenY);
                    }
                }
            } catch (Exception e) {
                firstPoint = true;
            }
        }
        
        g2.draw(path);
    }
    
    /**
     * Draw intersection points for intersection functions
     * @param g2 Graphics context
     * @param functions List of functions
     * @param width Panel width
     * @param height Panel height
     */
    public void drawIntersections(Graphics2D g2, List<GraphFunction> functions, 
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
                g2.fillOval(sx - INTERSECTION_POINT_RADIUS, sy - INTERSECTION_POINT_RADIUS,
                           INTERSECTION_POINT_RADIUS * 2, INTERSECTION_POINT_RADIUS * 2);
            }
        }
    }
    
    /**
     * Calculate adaptive sample count based on zoom level
     */
    private int calculateAdaptiveSamples(int pixelWidth) {
        double viewRangeX = bounds.getRangeX();
        double baseSamples = pixelWidth;
        double referenceRange = DEFAULT_VIEW_RANGE;
        double zoomMultiplier = Math.max(1.0, referenceRange / viewRangeX);
        zoomMultiplier = Math.min(10.0, zoomMultiplier);
        
        int sampleCount = (int) Math.round(baseSamples * zoomMultiplier);
        return Math.max(ADAPTIVE_SAMPLE_MIN, Math.min(ADAPTIVE_SAMPLE_MAX, sampleCount));
    }
    
    /**
     * Compute a "nice" step for ticks
     */
    private double niceStep(double range, int targetTicks) {
        if (range <= 0) return 1.0;
        double rawStep = range / Math.max(1, targetTicks);
        double exp = Math.floor(Math.log10(rawStep));
        double base = Math.pow(10, exp);
        double fraction = rawStep / base;
        double niceFraction;
        if (fraction <= 1.0) niceFraction = 1.0;
        else if (fraction <= 2.0) niceFraction = 2.0;
        else if (fraction <= 5.0) niceFraction = 5.0;
        else niceFraction = 10.0;
        return niceFraction * base;
    }
    
    /**
     * Format tick label
     */
    private String formatTickLabel(double value, double step) {
        double absStep = Math.abs(step);
        if (absStep >= 1.0) {
            long v = Math.round(value);
            return String.valueOf(v);
        } else {
            int prec = (int) Math.max(0, Math.min(6, -Math.floor(Math.log10(absStep)) + 1));
            return String.format(java.util.Locale.US, "%." + prec + "f", value);
        }
    }
}
