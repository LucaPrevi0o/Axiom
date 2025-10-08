package lib.graph.rendering;

import lib.constants.MathConstants;
import lib.constants.RenderingConstants;
import lib.graph.GraphBounds;
import lib.util.FormattingUtils;
import java.awt.*;

/**
 * Handles rendering of coordinate axes, tick marks, and labels
 */
public class AxisRenderer {
    
    /**
     * Draw X and Y axes with tick marks and labels
     * @param g2 Graphics context
     * @param bounds Graph bounds
     * @param width Panel width
     * @param height Panel height
     */
    public void drawAxes(Graphics2D g2, GraphBounds bounds, int width, int height) {
        g2.setColor(RenderingConstants.AXIS_COLOR);
        g2.setStroke(RenderingConstants.AXIS_STROKE);
        
        // Draw axis lines
        int yAxisScreen = bounds.yToScreen(0, height);
        int xAxisScreen = bounds.xToScreen(0, width);
        
        g2.drawLine(0, yAxisScreen, width, yAxisScreen);  // X-axis
        g2.drawLine(xAxisScreen, 0, xAxisScreen, height); // Y-axis
        
        // Draw ticks and labels
        double maxRange = Math.max(bounds.getRangeX(), bounds.getRangeY());
        double step = FormattingUtils.calculateNiceStep(maxRange, RenderingConstants.TARGET_TICK_COUNT);
        
        drawXAxisTicks(g2, bounds, width, height, step, yAxisScreen);
        drawYAxisTicks(g2, bounds, width, height, step, xAxisScreen);
    }
    
    /**
     * Draw X-axis tick marks and labels
     */
    private void drawXAxisTicks(Graphics2D g2, GraphBounds bounds, int width, int height,
                                 double step, int yAxisScreen) {
        g2.setFont(RenderingConstants.LABEL_FONT);
        FontMetrics fm = g2.getFontMetrics();
        
        double startX = Math.floor(bounds.getMinX() / step) * step;
        for (double x = startX; x <= bounds.getMaxX() + MathConstants.EPSILON; x += step) {
            if (Math.abs(x) < MathConstants.EPSILON) continue; // Skip origin
            
            int screenX = bounds.xToScreen(x, width);
            
            // Draw tick mark
            g2.drawLine(screenX, yAxisScreen - RenderingConstants.TICK_LENGTH, 
                       screenX, yAxisScreen + RenderingConstants.TICK_LENGTH);
            
            // Draw label
            String label = FormattingUtils.formatTickLabel(x, step);
            int labelWidth = fm.stringWidth(label);
            g2.drawString(label, screenX - labelWidth / 2, yAxisScreen + 20);
        }
    }
    
    /**
     * Draw Y-axis tick marks and labels
     */
    private void drawYAxisTicks(Graphics2D g2, GraphBounds bounds, int width, int height,
                                 double step, int xAxisScreen) {
        g2.setFont(RenderingConstants.LABEL_FONT);
        
        double startY = Math.floor(bounds.getMinY() / step) * step;
        for (double y = startY; y <= bounds.getMaxY() + MathConstants.EPSILON; y += step) {
            if (Math.abs(y) < MathConstants.EPSILON) continue; // Skip origin
            
            int screenY = bounds.yToScreen(y, height);
            
            // Draw tick mark
            g2.drawLine(xAxisScreen - RenderingConstants.TICK_LENGTH, screenY, 
                       xAxisScreen + RenderingConstants.TICK_LENGTH, screenY);
            
            // Draw label
            String label = FormattingUtils.formatTickLabel(y, step);
            g2.drawString(label, xAxisScreen + 10, screenY + 5);
        }
    }
}