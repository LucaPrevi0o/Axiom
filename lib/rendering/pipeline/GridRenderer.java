package lib.rendering.pipeline;

import lib.constants.MathConstants;
import lib.constants.RenderingConstants;
import lib.model.GraphBounds;
import lib.util.FormattingUtils;
import java.awt.*;

/**
 * Handles rendering of grid lines (both main and sub-grid)
 */
public class GridRenderer {
    
    /**
     * Draw grid lines on the graph
     * @param g2 Graphics context
     * @param bounds Graph bounds
     * @param width Panel width
     * @param height Panel height
     */
    public void drawGrid(Graphics2D g2, GraphBounds bounds, int width, int height) {
        double xRange = bounds.getRangeX();
        double yRange = bounds.getRangeY();
        double maxRange = Math.max(xRange, yRange);
        double step = FormattingUtils.calculateNiceStep(maxRange, RenderingConstants.TARGET_TICK_COUNT);
        
        // Calculate if sub-grid should be drawn
        double unitsPerPixel = maxRange / Math.max(width, height);
        double pixelsPerGridCell = step / unitsPerPixel;
        boolean drawSubGrid = pixelsPerGridCell >= RenderingConstants.MIN_PIXELS_FOR_SUBGRID;
        
        if (drawSubGrid) {
            drawSubGrid(g2, bounds, width, height, step);
        }
        
        drawMainGrid(g2, bounds, width, height, step);
    }
    
    /**
     * Draw sub-grid lines (5x subdivision)
     */
    private void drawSubGrid(Graphics2D g2, GraphBounds bounds, int width, int height, double mainStep) {
        g2.setColor(RenderingConstants.SUBGRID_COLOR);
        g2.setStroke(RenderingConstants.GRID_STROKE);
        
        double subStep = mainStep / 5.0;
        
        // Vertical sub-grid lines
        double startX = Math.floor(bounds.getMinX() / subStep) * subStep;
        for (double x = startX; x <= bounds.getMaxX() + MathConstants.EPSILON; x += subStep) {
            // Skip main grid lines
            if (Math.abs(x % mainStep) > 1e-10) {
                int screenX = bounds.xToScreen(x, width);
                g2.drawLine(screenX, 0, screenX, height);
            }
        }
        
        // Horizontal sub-grid lines
        double startY = Math.floor(bounds.getMinY() / subStep) * subStep;
        for (double y = startY; y <= bounds.getMaxY() + MathConstants.EPSILON; y += subStep) {
            // Skip main grid lines
            if (Math.abs(y % mainStep) > 1e-10) {
                int screenY = bounds.yToScreen(y, height);
                g2.drawLine(0, screenY, width, screenY);
            }
        }
    }
    
    /**
     * Draw main grid lines
     */
    private void drawMainGrid(Graphics2D g2, GraphBounds bounds, int width, int height, double step) {
        g2.setColor(RenderingConstants.MAIN_GRID_COLOR);
        g2.setStroke(RenderingConstants.GRID_STROKE);
        
        // Vertical lines
        double startX = Math.floor(bounds.getMinX() / step) * step;
        for (double x = startX; x <= bounds.getMaxX() + MathConstants.EPSILON; x += step) {
            int screenX = bounds.xToScreen(x, width);
            g2.drawLine(screenX, 0, screenX, height);
        }
        
        // Horizontal lines
        double startY = Math.floor(bounds.getMinY() / step) * step;
        for (double y = startY; y <= bounds.getMaxY() + MathConstants.EPSILON; y += step) {
            int screenY = bounds.yToScreen(y, height);
            g2.drawLine(0, screenY, width, screenY);
        }
    }
}