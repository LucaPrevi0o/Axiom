package lib.graph;
import javax.swing.*;

import lib.expression.ExpressionEvaluator;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    private List<GraphFunction> functions;
    private ExpressionEvaluator evaluator;
    private java.util.Map<String, String> userFunctions = new java.util.HashMap<>();
    
    // Graph defaults: use DEFAULT_VIEW_RANGE and INITIAL_ZOOM to control initial zoom level
    private static final double DEFAULT_VIEW_RANGE_X = 20.0; // default total width in graph units
    private static final double DEFAULT_VIEW_RANGE_Y = 20.0; // default total height in graph units
    private static final double INITIAL_ZOOM = 1.0; // 1.0 = 1x (no zoom). >1.0 zooms in, <1.0 zooms out

    // Graph bounds
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    
    // Mouse dragging state
    private Point lastMousePoint;
    private boolean isDragging = false;

    /**
     * Constructor to set up the panel
     */
    public GraphPanel() {
        setBackground(Color.WHITE);
        // initialize view to default zoom
        double halfWidth = (DEFAULT_VIEW_RANGE_X / INITIAL_ZOOM) / 2.0;
        double halfHeight = (DEFAULT_VIEW_RANGE_Y / INITIAL_ZOOM) / 2.0;
        this.minX = -halfWidth;
        this.maxX = halfWidth;
        this.minY = -halfHeight;
        this.maxY = halfHeight;

        evaluator = new ExpressionEvaluator(userFunctions);
        functions = new ArrayList<>();
        
        setupMouseListeners();
        // Preserve zoom ratio when the panel is resized
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                preserveZoomOnResize();
            }
        });
    }

    // Keep units-per-pixel constant when the component is resized by adjusting bounds
    private void preserveZoomOnResize() {
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());

        // current center in graph coordinates
        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;

        // current units per pixel
        double unitsPerPixelX = (maxX - minX) / Math.max(1, w);
        double unitsPerPixelY = (maxY - minY) / Math.max(1, h);

        // recompute half ranges preserving units per pixel
        double halfWidth = (unitsPerPixelX * w) / 2.0;
        double halfHeight = (unitsPerPixelY * h) / 2.0;

        minX = centerX - halfWidth;
        maxX = centerX + halfWidth;
        minY = centerY - halfHeight;
        maxY = centerY + halfHeight;
    }
    
    /**
     * Set up mouse listeners for zoom and pan
     */
    private void setupMouseListeners() {
        // Mouse wheel for zoom
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                handleZoom(e);
            }
        });
        
        // Mouse drag for pan
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    lastMousePoint = e.getPoint();
                    isDragging = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    isDragging = false;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    handlePan(e);
                }
            }
        });
    }
    
    /**
     * Handle zoom with mouse wheel
     * @param e The mouse wheel event
     */
    private void handleZoom(MouseWheelEvent e) {
        double zoomFactor = e.getWheelRotation() < 0 ? 0.95 : 1.05;

        // Screen position of mouse
        int sx = e.getX();
        int sy = e.getY();

        // Graph coordinate under mouse before zoom
        double mouseGraphX = screenToX(sx);
        double mouseGraphY = screenToY(sy);

        // New ranges after zoom
        double newRangeX = (maxX - minX) * zoomFactor;
        double newRangeY = (maxY - minY) * zoomFactor;

        // Compute new min/max so that mouseGraphX maps to same screen X (sx)
        double relX = (sx / (double) Math.max(1, getWidth()));
        double relY = (sy / (double) Math.max(1, getHeight()));

        double newMinX = mouseGraphX - relX * newRangeX;
        double newMaxX = newMinX + newRangeX;
        double newMaxY = mouseGraphY + (1 - relY) * newRangeY;
        double newMinY = newMaxY - newRangeY;

        minX = newMinX;
        maxX = newMaxX;
        minY = newMinY;
        maxY = newMaxY;

        repaint();
    }
    
    /**
     * Handle panning with mouse drag
     * @param e The mouse event
     */
    private void handlePan(MouseEvent e) {
        if (lastMousePoint == null) return;
        
        int dx = e.getX() - lastMousePoint.x;
        int dy = e.getY() - lastMousePoint.y;
        
        // Convert pixel movement to graph coordinate movement
        double graphDx = dx * (maxX - minX) / getWidth();
        double graphDy = -dy * (maxY - minY) / getHeight(); // Negative because screen Y is inverted
        
        minX -= graphDx;
        maxX -= graphDx;
        minY -= graphDy;
        maxY -= graphDy;
        
        lastMousePoint = e.getPoint();
        repaint();
    }
    
    /**
     * Set the functions to be graphed
     * @param functions The list of functions
     */
    public void setFunctions(List<GraphFunction> functions) {
        this.functions = functions;
    }

    public void setUserFunctions(java.util.Map<String, String> userFunctions) {
        this.userFunctions = userFunctions == null ? new java.util.HashMap<>() : userFunctions;
        this.evaluator = new ExpressionEvaluator(this.userFunctions);
    }
    
    /**
     * Override paintComponent to draw the graphics
     * @param g The {@link Graphics} object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawGrid(g2);
        drawAxes(g2);
        drawFunctions(g2);
    }

    /**
     * Draw the grid lines on the graph
     * @param g2 The {@link Graphics2D} object
     */
    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(1));
        // Compute nice tick spacing based on view range
        double xRange = maxX - minX;
        double yRange = maxY - minY;
        double xStep = niceStep(xRange, 8); // aim ~8 vertical grid lines
        double yStep = niceStep(yRange, 8); // aim ~8 horizontal grid lines

        // Vertical grid lines
        double startX = Math.floor(minX / xStep) * xStep;
        for (double gx = startX; gx <= maxX + 1e-12; gx += xStep) {
            int x = xToScreen(gx);
            g2.drawLine(x, 0, x, getHeight());
        }

        // Horizontal grid lines
        double startY = Math.floor(minY / yStep) * yStep;
        for (double gy = startY; gy <= maxY + 1e-12; gy += yStep) {
            int y = yToScreen(gy);
            g2.drawLine(0, y, getWidth(), y);
        }
    }
    
    /**
     * Draw the X and Y axes on the graph
     * @param g2 The {@link Graphics2D} object
     */
    private void drawAxes(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        
        // X-axis
        int yAxis = yToScreen(0);
        g2.drawLine(0, yAxis, getWidth(), yAxis);
        
        // Y-axis
        int xAxis = xToScreen(0);
        g2.drawLine(xAxis, 0, xAxis, getHeight());
        
        // Draw tick marks and labels using adaptive spacing
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        double xRange = maxX - minX;
        double yRange = maxY - minY;
        double xStep = niceStep(xRange, 8);
        double yStep = niceStep(yRange, 8);

        double startX = Math.floor(minX / xStep) * xStep;
        for (double gx = startX; gx <= maxX + 1e-12; gx += xStep) {
            if (Math.abs(gx) < 1e-12) continue; // skip origin (already drawn)
            int x = xToScreen(gx);
            g2.drawLine(x, yAxis - 5, x, yAxis + 5);
            String label = formatTickLabel(gx, xStep);
            g2.drawString(label, x - g2.getFontMetrics().stringWidth(label) / 2, yAxis + 20);
        }

        double startY = Math.floor(minY / yStep) * yStep;
        for (double gy = startY; gy <= maxY + 1e-12; gy += yStep) {
            if (Math.abs(gy) < 1e-12) continue;
            int y = yToScreen(gy);
            g2.drawLine(xAxis - 5, y, xAxis + 5, y);
            String label = formatTickLabel(gy, yStep);
            g2.drawString(label, xAxis + 10, y + 5);
        }
    }

    // Compute a "nice" step for ticks: 1,2,5 * 10^n scaled to range/targetTicks
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

    // Format tick label: show decimals only when step < 1, otherwise show integer-ish
    private String formatTickLabel(double value, double step) {
        double absStep = Math.abs(step);
        if (absStep >= 1.0) {
            long v = Math.round(value);
            return String.valueOf(v);
        } else {
            // decimal precision based on step magnitude
            int prec = (int) Math.max(0, Math.min(6, -Math.floor(Math.log10(absStep)) + 1));
            return String.format(java.util.Locale.US, "%." + prec + "f", value);
        }
    }
    
    /**
     * Draw all functions on the graph
     * @param g2 The {@link Graphics2D} object
     */
    private void drawFunctions(Graphics2D g2) {
        for (GraphFunction function : functions) {
            drawFunction(g2, function);
        }
    }
    
    /**
     * Draw a single function on the graph
     * @param g2 The {@link Graphics2D} object
     * @param function The function to draw
     */
    private void drawFunction(Graphics2D g2, GraphFunction function) {
        g2.setColor(function.getColor());
        g2.setStroke(new BasicStroke(2));
        
        Path2D path = new Path2D.Double();
        boolean firstPoint = true;
        // Adaptive sampling: choose number of samples proportional to viewport width
        // and inversely proportional to the visible X range (zoom level).
        double viewRangeX = maxX - minX;
        int pixelWidth = Math.max(2, getWidth());

        // Base samples: one sample per pixel
        double baseSamples = pixelWidth;

        // Zoom factor: when viewRangeX is smaller than a reference range, increase samples.
        double referenceRange = 20.0; // when range is 20 units, use ~1 sample/pixel
        double zoomMultiplier = Math.max(1.0, referenceRange / viewRangeX);

        // Limit the multiplier to avoid excessive sampling
        zoomMultiplier = Math.min(10.0, zoomMultiplier);

        int sampleCount = (int) Math.round(baseSamples * zoomMultiplier);
        // Cap absolute number of samples to avoid performance issues
        sampleCount = Math.max(50, Math.min(5000, sampleCount));

        // Step in screen pixels between successive samples
        double step = (double) pixelWidth / (double) sampleCount;

        for (double sx = 0.0; sx < pixelWidth; sx += step) {
            int screenX = (int) Math.round(sx);
            double x = screenToX(screenX);

            try {
                double y = evaluator.evaluate(function.getExpression(), x);

                // Check if y is within bounds
                if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                    int screenY = yToScreen(y);

                    if (firstPoint) {
                        path.moveTo(screenX, screenY);
                        firstPoint = false;
                    } else {
                        path.lineTo(screenX, screenY);
                    }
                }
            } catch (Exception e) {
                // On evaluation error, break the current segment
                firstPoint = true;
            }
        }
        
        g2.draw(path);
    }
    
    // Coordinate conversion methods
    private int xToScreen(double x) {
        return (int) ((x - minX) / (maxX - minX) * getWidth());
    }

    private int yToScreen(double y) {
        return (int) ((maxY - y) / (maxY - minY) * getHeight());
    }

    private double screenToX(int screenX) {
        return minX + (screenX * (maxX - minX) / getWidth());
    }

    private double screenToY(int screenY) {
        return maxY - (screenY * (maxY - minY) / getHeight());
    }
}