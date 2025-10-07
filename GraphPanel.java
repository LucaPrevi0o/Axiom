// GraphPanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class GraphPanel extends JPanel {

    private String functionExpression = "x^2";
    private ExpressionEvaluator evaluator;
    
    // Graph bounds
    private double minX = -10;
    private double maxX = 10;
    private double minY = -10;
    private double maxY = 10;

    /**
     * Constructor to set up the panel
     */
    public GraphPanel() {

        setBackground(Color.WHITE);
        evaluator = new ExpressionEvaluator();
    }
    
    /**
     * Set the function expression to be graphed
     * @param expression The function expression as a string
     */
    public void setFunction(String expression) { this.functionExpression = expression; }
    
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
        drawFunction(g2);
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
     * Draw the function on the graph
     * @param g2 The {@link Graphics2D} object
     */
    private void drawFunction(Graphics2D g2) {

        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));
        
        Path2D path = new Path2D.Double();
        boolean firstPoint = true;
        
        // Sample points across the screen
        for (int screenX = 0; screenX < getWidth(); screenX++) {
            double x = screenToX(screenX);
            
            try {

                double y = evaluator.evaluate(functionExpression, x);
                
                // Check if y is within bounds
                if (!Double.isNaN(y) && !Double.isInfinite(y)) {

                    int screenY = yToScreen(y);
                    if (firstPoint) {

                        path.moveTo(screenX, screenY);
                        firstPoint = false;
                    } else  path.lineTo(screenX, screenY);
                }
            } catch (Exception e) { firstPoint = true; }
        }
        
        g2.draw(path);
    }
    
    // Coordinate conversion methods
    private int xToScreen(double x) { return (int) ((x - minX) / (maxX - minX) * getWidth()); }

    private int yToScreen(double y) { return (int) ((maxY - y) / (maxY - minY) * getHeight()); }

    private double screenToX(int screenX) { return minX + (screenX * (maxX - minX) / getWidth()); }

    private double screenToY(int screenY) { return maxY - (screenY * (maxY - minY) / getHeight()); }
}