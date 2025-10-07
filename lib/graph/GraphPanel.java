package lib.graph;
import javax.swing.*;

import lib.expression.ExpressionEvaluator;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    private List<GraphFunction> functions;
    private ExpressionEvaluator evaluator;
    private java.util.Map<String, String> userFunctions = new java.util.HashMap<>();
    private java.util.Map<String, Double> parameters = new java.util.HashMap<>();
    
    // Refactored components
    private GraphBounds bounds;
    private ViewportManager viewportManager;
    private IntersectionFinder intersectionFinder;
    private GraphRenderer renderer;
    
    // cache of named intersection points for user-defined intersection functions
    private java.util.Map<String, java.util.List<Point2D.Double>> namedIntersectionPoints = new java.util.HashMap<>();
    
    // Graph defaults - increased range to show more of the graph initially
    private static final double DEFAULT_VIEW_RANGE_X = 40.0;
    private static final double DEFAULT_VIEW_RANGE_Y = 40.0;
    private static final double INITIAL_ZOOM = 1.0;
    
    // Track previous dimensions for resize handling
    private int previousWidth = 0;
    private int previousHeight = 0;

    /**
     * Constructor to set up the panel
     */
    public GraphPanel() {
        setBackground(Color.WHITE);
        
        // Initialize bounds
        double halfWidth = (DEFAULT_VIEW_RANGE_X / INITIAL_ZOOM) / 2.0;
        double halfHeight = (DEFAULT_VIEW_RANGE_Y / INITIAL_ZOOM) / 2.0;
        bounds = GraphBounds.centered(halfWidth, halfHeight);
        
        // Initialize components
        evaluator = new ExpressionEvaluator(userFunctions, parameters);
        functions = new ArrayList<>();
        viewportManager = new ViewportManager(bounds);
        intersectionFinder = new IntersectionFinder(evaluator);
        renderer = new GraphRenderer(evaluator, intersectionFinder, bounds);
        
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
        
        if (previousWidth > 0 && previousHeight > 0) {
            viewportManager.preserveZoomOnResize(w, h, previousWidth, previousHeight);
        }
        
        previousWidth = w;
        previousHeight = h;
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
                    viewportManager.startPan(e.getPoint());
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    viewportManager.endPan();
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (viewportManager.isDragging()) {
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
        boolean zoomIn = e.getWheelRotation() < 0;
        viewportManager.zoom(zoomIn, e.getX(), e.getY(), getWidth(), getHeight());
        repaint();
    }
    
    /**
     * Handle panning with mouse drag
     * @param e The mouse event
     */
    private void handlePan(MouseEvent e) {
        viewportManager.updatePan(e.getPoint(), getWidth(), getHeight());
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
        this.evaluator = new ExpressionEvaluator(this.userFunctions, this.parameters);
        
        // Update renderer and intersection finder with new evaluator
        this.intersectionFinder = new IntersectionFinder(evaluator);
        this.renderer = new GraphRenderer(evaluator, intersectionFinder, bounds);
        
        // Precompute intersection points for any named function whose RHS is an intersection
        namedIntersectionPoints.clear();
        for (java.util.Map.Entry<String, String> e : this.userFunctions.entrySet()) {
            String name = e.getKey().toLowerCase();
            String rhs = e.getValue();
            if (rhs != null && rhs.matches("^\\s*\\(.*=.*\\)\\s*$")) {
                String inside = rhs.trim();
                inside = inside.substring(1, inside.length() - 1).trim();
                int eqIdx = inside.indexOf('=');
                if (eqIdx > 0) {
                    String left = inside.substring(0, eqIdx).trim();
                    String right = inside.substring(eqIdx + 1).trim();
                    try {
                        java.util.List<Point2D.Double> pts = intersectionFinder.findIntersections(
                            left, right, bounds.getMinX(), bounds.getMaxX(), getWidth()
                        );
                        namedIntersectionPoints.put(name, pts);
                    } catch (Exception ex) {
                        // ignore
                    }
                }
            }
        }
        
        renderer.setNamedIntersectionPoints(namedIntersectionPoints);
    }
    
    public void setParameters(java.util.Map<String, Double> parameters) {
        this.parameters = parameters == null ? new java.util.HashMap<>() : parameters;
        this.evaluator = new ExpressionEvaluator(this.userFunctions, this.parameters);
        
        // Update renderer and intersection finder with new evaluator
        this.intersectionFinder = new IntersectionFinder(evaluator);
        this.renderer = new GraphRenderer(evaluator, intersectionFinder, bounds);
    }
    
    /**
     * Override paintComponent to draw the graphics
     * @param g The {@link Graphics} object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        renderer.render(g2, functions, getWidth(), getHeight());
    }
}