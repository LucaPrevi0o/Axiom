package graph;
import javax.swing.*;

import expression.ExpressionEvaluator;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    private List<GraphFunction> functions;
    private ExpressionEvaluator evaluator;
    private java.util.Map<String, String> userFunctions = new java.util.HashMap<>();
    
    // Graph bounds
    private double minX = -10;
    private double maxX = 10;
    private double minY = -10;
    private double maxY = 10;
    
    // Mouse dragging state
    private Point lastMousePoint;
    private boolean isDragging = false;

    /**
     * Constructor to set up the panel
     */
    public GraphPanel() {
        setBackground(Color.WHITE);
        evaluator = new ExpressionEvaluator(userFunctions);
        functions = new ArrayList<>();
        
        setupMouseListeners();
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
        
        // Get mouse position in graph coordinates
        double mouseX = screenToX(e.getX());
        double mouseY = screenToY(e.getY());
        
        // Calculate new bounds centered on mouse position
        double rangeX = (maxX - minX) * zoomFactor;
        double rangeY = (maxY - minY) * zoomFactor;
        
        double centerX = mouseX;
        double centerY = mouseY;
        
        minX = centerX - rangeX / 2;
        maxX = centerX + rangeX / 2;
        minY = centerY - rangeY / 2;
        maxY = centerY + rangeY / 2;
        
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
        
        // Vertical grid lines
        for (int i = (int) minX; i <= maxX; i++) {
            int x = xToScreen(i);
            g2.drawLine(x, 0, x, getHeight());
        }
        
        // Horizontal grid lines
        for (int i = (int) minY; i <= maxY; i++) {
            int y = yToScreen(i);
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
        
        // Draw tick marks and labels
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = (int) minX; i <= maxX; i++) {
            if (i != 0) {
                int x = xToScreen(i);
                g2.drawLine(x, yAxis - 5, x, yAxis + 5);
                g2.drawString(String.valueOf(i), x - 5, yAxis + 20);
            }
        }
        
        for (int i = (int) minY; i <= maxY; i++) {
            if (i != 0) {
                int y = yToScreen(i);
                g2.drawLine(xAxis - 5, y, xAxis + 5, y);
                g2.drawString(String.valueOf(i), xAxis + 10, y + 5);
            }
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
        
        // Sample points across the screen
        for (int screenX = 0; screenX < getWidth(); screenX++) {
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