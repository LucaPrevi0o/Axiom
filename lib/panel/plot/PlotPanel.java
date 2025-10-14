package lib.panel.plot;
import javax.swing.*;

import lib.function.Function;
import lib.function.PlottableFunction;
import lib.parser.expression.ExpressionEvaluator;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel to graph mathematical functions
 */
public class PlotPanel extends JPanel {

    private List<PlottableFunction> functions = new ArrayList<>();
    private ViewPortManager viewport;

    /**
     * Constructor to set up the panel
     */
    public PlotPanel() {

        setBackground(Color.WHITE);
        viewport = new ViewPortManager();
        viewport.attachMouseListeners(this);
    }

    /**
     * Add a function to be graphed
     * 
     * @param function The Function object to add
     */
    public void addFunction(PlottableFunction function) {
        this.functions.add(function);
    }
    
    /**
     * Remove a function from the graph
     * 
     * @param function The Function object to remove
     */
    public void removeFunction(Function function) { this.functions.remove(function); }
    
    /**
     * Clear all functions from the graph
     */
    public void clearFunctions() { this.functions.clear(); }
    
    /**
     * Get all functions
     * 
     * @return List of all Function objects
     */
    public List<PlottableFunction> getFunctions() { return functions; }

    /**
     * Override paintComponent to draw the graphics
     * 
     * @param g The {@link Graphics} object
     */
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
     * 
     * @param g2 The {@link Graphics2D} object
     */
    private void drawGrid(Graphics2D g2) {

        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(1));

        // Vertical grid lines
        for (int i = (int) viewport.getMinX(); i <= viewport.getMaxX(); i++) {

            int x = xToScreen(i);
            g2.drawLine(x, 0, x, getHeight());
        }

        // Horizontal grid lines
        for (int i = (int) viewport.getMinY(); i <= viewport.getMaxY(); i++) {

            int y = yToScreen(i);
            g2.drawLine(0, y, getWidth(), y);
        }
    }

    /**
     * Draw the X and Y axes on the graph
     * 
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
        for (int i = (int) viewport.getMinX(); i <= viewport.getMaxX(); i++) {

            if (i != 0) {

                int x = xToScreen(i);
                g2.drawLine(x, yAxis - 5, x, yAxis + 5);
                g2.drawString(String.valueOf(i), x - 5, yAxis + 20);
            }
        }

        for (int i = (int) viewport.getMinY(); i <= viewport.getMaxY(); i++) {

            if (i != 0) {

                int y = yToScreen(i);
                g2.drawLine(xAxis - 5, y, xAxis + 5, y);
                g2.drawString(String.valueOf(i), xAxis + 10, y + 5);
            }
        }
    }

    /**
     * Draw the function on the graph
     * 
     * @param g2 The {@link Graphics2D} object
     */
    private void drawFunctions(Graphics2D g2) {

        g2.setStroke(new BasicStroke(2));

        for (PlottableFunction function : functions) {

            if (!function.isVisible()) continue;
            
            g2.setColor(function.getColor());
            Path2D path = computeFunctionPath(function);
            g2.draw(path);
        }
    }
    
    /**
     * Compute the path for a function by evaluating it at screen points
     * 
     * @param function The Function to compute points for
     * @return A Path2D representing the function curve
     */
    private Path2D computeFunctionPath(Function function) {
        
        Path2D path = new Path2D.Double();
        boolean firstPoint = true;

        // Sample points across the screen
        for (int screenX = 0; screenX < getWidth(); screenX++) {

            double x = screenToX(screenX);
            try {

                // Evaluate the function using ExpressionEvaluator
                double y = ExpressionEvaluator.evaluate(function.getExpression(), x);

                // Check if y is within bounds
                if (!Double.isNaN(y) && !Double.isInfinite(y)) {

                    int screenY = yToScreen(y);
                    if (firstPoint) {

                        path.moveTo(screenX, screenY);
                        firstPoint = false;
                    } else path.lineTo(screenX, screenY);
                } else firstPoint = true;
            } catch (Exception e) { firstPoint = true; }
        }
        
        return path;
    }

    // Coordinate conversion methods
    private int xToScreen(double x) {
        return (int) ((x - viewport.getMinX()) / (viewport.getMaxX() - viewport.getMinX()) * getWidth());
    }

    private int yToScreen(double y) {
        return (int) ((viewport.getMaxY() - y) / (viewport.getMaxY() - viewport.getMinY()) * getHeight());
    }

    private double screenToX(int screenX) {
        return viewport.getMinX() + (screenX * (viewport.getMaxX() - viewport.getMinX()) / getWidth());
    }

    private double screenToY(int screenY) {
        return viewport.getMaxY() - (screenY * (viewport.getMaxY() - viewport.getMinY()) / getHeight());
    }
    
    /**
     * Get the viewport for direct access to bounds
     * 
     * @return The PlotViewport instance
     */
    public ViewPortManager getViewport() { return viewport; }
}