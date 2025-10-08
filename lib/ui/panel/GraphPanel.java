package lib.ui.panel;

import lib.constants.GraphConstants;
import lib.constants.RenderingConstants;
import lib.core.ExpressionEvaluator;
import lib.model.Function;
import lib.model.GraphBounds;
import lib.model.Parameter;
import lib.model.ParametricPointFunction;
import lib.model.ViewportManager;
import lib.rendering.GraphRenderer;
import lib.rendering.IntersectionFinder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    /**
     * Listener interface for parameter updates during point dragging
     */
    public interface ParameterUpdateListener {
        void onParameterUpdated(String parameterName, double newValue);
    }

    private List<Function> functions;
    private ExpressionEvaluator evaluator;
    private java.util.Map<String, String> userFunctions = new java.util.HashMap<>();
    private java.util.Map<String, Double> parameters = new java.util.HashMap<>();
    private java.util.Map<String, Parameter> parameterObjects = new java.util.HashMap<>();
    
    // Refactored components
    private GraphBounds bounds;
    private ViewportManager viewportManager;
    private IntersectionFinder intersectionFinder;
    private GraphRenderer renderer;
    
    // cache of named intersection points for user-defined intersection functions
    private java.util.Map<String, java.util.List<Point2D.Double>> namedIntersectionPoints = new java.util.HashMap<>();
    
    // Track previous dimensions for resize handling
    private int previousWidth = 0;
    private int previousHeight = 0;
    
    // Point dragging state
    private ParametricPointFunction draggedPoint = null;
    private String draggedParameterX = null;
    private String draggedParameterY = null;
    private Parameter draggedParamObjX = null;
    private Parameter draggedParamObjY = null;
    private boolean isDraggingPoint = false;
    
    // Callback to update parameter sliders in UI
    private ParameterUpdateListener parameterUpdateListener = null;

    /**
     * Constructor to set up the panel
     */
    public GraphPanel() {
        setBackground(Color.WHITE);
        
        // Initialize bounds
        double halfWidth = (GraphConstants.DEFAULT_VIEW_RANGE_X / GraphConstants.INITIAL_ZOOM) / 2.0;
        double halfHeight = (GraphConstants.DEFAULT_VIEW_RANGE_Y / GraphConstants.INITIAL_ZOOM) / 2.0;
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
     * Set up mouse listeners for zoom, pan, and point dragging
     */
    private void setupMouseListeners() {
        // Mouse wheel for zoom
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                handleZoom(e);
            }
        });
        
        // Mouse drag for pan or point dragging
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Check if clicking on a draggable point
                    ParametricPointFunction clickedPoint = findDraggablePointAt(e.getX(), e.getY());
                    
                    if (clickedPoint != null) {
                        // Start dragging the point
                        startDraggingPoint(clickedPoint);
                    } else {
                        // Start panning the view
                        viewportManager.startPan(e.getPoint());
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (isDraggingPoint) {
                        endDraggingPoint();
                    } else {
                        viewportManager.endPan();
                    }
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Update cursor when hovering over draggable points
                ParametricPointFunction hoverPoint = findDraggablePointAt(e.getX(), e.getY());
                if (hoverPoint != null) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDraggingPoint) {
                    handlePointDrag(e);
                } else if (viewportManager.isDragging()) {
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
    public void setFunctions(List<Function> functions) {
        this.functions = functions;
        // Invalidate caches when functions change
        for (Function function : functions) {
            function.invalidateCache();
        }
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
    }
    
    public void setParameters(java.util.Map<String, Double> parameters) {
        this.parameters = parameters == null ? new java.util.HashMap<>() : parameters;
        this.evaluator = new ExpressionEvaluator(this.userFunctions, this.parameters);
        
        // Update renderer and intersection finder with new evaluator
        this.intersectionFinder = new IntersectionFinder(evaluator);
        this.renderer = new GraphRenderer(evaluator, intersectionFinder, bounds);
    }
    
    /**
     * Set parameter objects for drag constraints
     * @param parameterObjects Map of parameter name to Parameter object
     */
    public void setParameterObjects(java.util.Map<String, Parameter> parameterObjects) {
        this.parameterObjects = parameterObjects == null ? new java.util.HashMap<>() : parameterObjects;
    }
    
    /**
     * Set listener for parameter updates during point dragging
     */
    public void setParameterUpdateListener(ParameterUpdateListener listener) {
        this.parameterUpdateListener = listener;
    }
    
    /**
     * Find a draggable point at the given screen coordinates
     * @param screenX Screen X coordinate
     * @param screenY Screen Y coordinate
     * @return ParametricPointFunction if found, null otherwise
     */
    private ParametricPointFunction findDraggablePointAt(int screenX, int screenY) {
        final int CLICK_THRESHOLD = RenderingConstants.INTERSECTION_POINT_RADIUS + 3;
        
        for (Function function : functions) {
            if (function instanceof ParametricPointFunction && function.isEnabled()) {
                ParametricPointFunction pointFunc = (ParametricPointFunction) function;
                
                // Only draggable if it uses parameters
                if (!pointFunc.isDraggable()) {
                    continue;
                }
                
                // Get the point's current position
                List<Point2D.Double> points = pointFunc.getPoints(bounds, getWidth(), getHeight());
                if (points.isEmpty()) {
                    continue;
                }
                
                Point2D.Double point = points.get(0);
                
                // Convert to screen coordinates
                int pointScreenX = bounds.xToScreen(point.x, getWidth());
                int pointScreenY = bounds.yToScreen(point.y, getHeight());
                
                // Check if click is within threshold
                double distance = Math.sqrt(
                    Math.pow(screenX - pointScreenX, 2) + 
                    Math.pow(screenY - pointScreenY, 2)
                );
                
                if (distance <= CLICK_THRESHOLD) {
                    return pointFunc;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Start dragging a point
     */
    private void startDraggingPoint(ParametricPointFunction point) {
        draggedPoint = point;
        isDraggingPoint = true;
        
        // Identify which parameters are used in X and Y coordinates
        draggedParameterX = point.getParameterInX();
        draggedParameterY = point.getParameterInY();
        
        // Find the Parameter objects for constraints
        draggedParamObjX = findParameter(draggedParameterX);
        draggedParamObjY = findParameter(draggedParameterY);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * Handle point dragging
     */
    private void handlePointDrag(MouseEvent e) {
        if (draggedPoint == null) {
            return;
        }
        
        // Convert screen coordinates to graph coordinates
        double graphX = bounds.screenToX(e.getX(), getWidth());
        double graphY = bounds.screenToY(e.getY(), getHeight());
        
        // Update parameters based on which coordinate they affect
        boolean updated = false;
        
        if (draggedParameterX != null && draggedParamObjX != null) {
            // Clamp to parameter bounds
            double newValue = Math.max(draggedParamObjX.getMinValue(), 
                              Math.min(draggedParamObjX.getMaxValue(), graphX));
            
            // Update parameter value
            parameters.put(draggedParameterX, newValue);
            draggedParamObjX.setCurrentValue(newValue);
            updated = true;
            
            // Notify listener (to update slider UI)
            if (parameterUpdateListener != null) {
                parameterUpdateListener.onParameterUpdated(draggedParameterX, newValue);
            }
        }
        
        if (draggedParameterY != null && draggedParamObjY != null) {
            // Clamp to parameter bounds
            double newValue = Math.max(draggedParamObjY.getMinValue(), 
                              Math.min(draggedParamObjY.getMaxValue(), graphY));
            
            // Update parameter value
            parameters.put(draggedParameterY, newValue);
            draggedParamObjY.setCurrentValue(newValue);
            updated = true;
            
            // Notify listener (to update slider UI)
            if (parameterUpdateListener != null) {
                parameterUpdateListener.onParameterUpdated(draggedParameterY, newValue);
            }
        }
        
        if (updated) {
            // Recreate evaluator with updated parameters
            this.evaluator = new ExpressionEvaluator(this.userFunctions, this.parameters);
            this.intersectionFinder = new IntersectionFinder(evaluator);
            this.renderer = new GraphRenderer(evaluator, intersectionFinder, bounds);
            
            // Invalidate cache to force recomputation
            draggedPoint.invalidateCache();
            
            repaint();
        }
    }
    
    /**
     * End point dragging
     */
    private void endDraggingPoint() {
        draggedPoint = null;
        draggedParameterX = null;
        draggedParameterY = null;
        draggedParamObjX = null;
        draggedParamObjY = null;
        isDraggingPoint = false;
    }
    
    /**
     * Find a Parameter object by name
     */
    private Parameter findParameter(String name) {
        if (name == null) {
            return null;
        }
        
        // We need access to the Parameter objects from FunctionPanel
        // For now, we'll create a list to store them
        // This will need to be connected to FunctionPanel
        return parameterObjects.get(name.toLowerCase());
    }
    
    /**
     * Get the expression evaluator
     */
    public ExpressionEvaluator getEvaluator() {
        return evaluator;
    }
    
    /**
     * Get the intersection finder
     */
    public IntersectionFinder getIntersectionFinder() {
        return intersectionFinder;
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