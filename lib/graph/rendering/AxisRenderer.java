package lib.graph.rendering;

import lib.graph.GraphBounds;
import java.awt.*;

/**
 * Handles rendering of coordinate axes, tick marks, and labels
 */
public class AxisRenderer {
    
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final int AXIS_STROKE_WIDTH = 2;
    private static final int TICK_LENGTH = 5;
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 10);
    
    /**
     * Draw X and Y axes with tick marks and labels
     * @param g2 Graphics context
     * @param bounds Graph bounds
     * @param width Panel width
     * @param height Panel height
     */
    public void drawAxes(Graphics2D g2, GraphBounds bounds, int width, int height) {
        g2.setColor(AXIS_COLOR);
        g2.setStroke(new BasicStroke(AXIS_STROKE_WIDTH));
        
        // Draw axis lines
        int yAxisScreen = bounds.yToScreen(0, height);
        int xAxisScreen = bounds.xToScreen(0, width);
        
        g2.drawLine(0, yAxisScreen, width, yAxisScreen);  // X-axis
        g2.drawLine(xAxisScreen, 0, xAxisScreen, height); // Y-axis
        
        // Draw ticks and labels
        double maxRange = Math.max(bounds.getRangeX(), bounds.getRangeY());
        double step = TickCalculator.calculateNiceStep(maxRange, 8);
        
        drawXAxisTicks(g2, bounds, width, height, step, yAxisScreen);
        drawYAxisTicks(g2, bounds, width, height, step, xAxisScreen);
    }
    
    /**
     * Draw X-axis tick marks and labels
     */
    private void drawXAxisTicks(Graphics2D g2, GraphBounds bounds, int width, int height,
                                 double step, int yAxisScreen) {
        g2.setFont(LABEL_FONT);
        FontMetrics fm = g2.getFontMetrics();
        
        double startX = Math.floor(bounds.getMinX() / step) * step;
        for (double x = startX; x <= bounds.getMaxX() + 1e-12; x += step) {
            if (Math.abs(x) < 1e-12) continue; // Skip origin
            
            int screenX = bounds.xToScreen(x, width);
            
            // Draw tick mark
            g2.drawLine(screenX, yAxisScreen - TICK_LENGTH, screenX, yAxisScreen + TICK_LENGTH);
            
            // Draw label
            String label = TickCalculator.formatTickLabel(x, step);
            int labelWidth = fm.stringWidth(label);
            g2.drawString(label, screenX - labelWidth / 2, yAxisScreen + 20);
        }
    }
    
    /**
     * Draw Y-axis tick marks and labels
     */
    private void drawYAxisTicks(Graphics2D g2, GraphBounds bounds, int width, int height,
                                 double step, int xAxisScreen) {
        g2.setFont(LABEL_FONT);
        
        double startY = Math.floor(bounds.getMinY() / step) * step;
        for (double y = startY; y <= bounds.getMaxY() + 1e-12; y += step) {
            if (Math.abs(y) < 1e-12) continue; // Skip origin
            
            int screenY = bounds.yToScreen(y, height);
            
            // Draw tick mark
            g2.drawLine(xAxisScreen - TICK_LENGTH, screenY, xAxisScreen + TICK_LENGTH, screenY);
            
            // Draw label
            String label = TickCalculator.formatTickLabel(y, step);
            g2.drawString(label, xAxisScreen + 10, screenY + 5);
        }
    }
}