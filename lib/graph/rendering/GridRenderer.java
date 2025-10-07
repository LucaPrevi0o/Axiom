package lib.graph.rendering;

import lib.graph.GraphBounds;
import java.awt.*;

/**
 * Handles rendering of grid lines (both main and sub-grid)
 */
public class GridRenderer {
    
    private static final int MIN_PIXELS_FOR_SUBGRID = 50;
    private static final Color SUBGRID_COLOR = new Color(240, 240, 240);
    private static final Color MAIN_GRID_COLOR = new Color(200, 200, 200);
    
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
        double step = TickCalculator.calculateNiceStep(maxRange, 8);
        
        // Calculate if sub-grid should be drawn
        double unitsPerPixel = maxRange / Math.max(width, height);
        double pixelsPerGridCell = step / unitsPerPixel;
        boolean drawSubGrid = pixelsPerGridCell >= MIN_PIXELS_FOR_SUBGRID;
        
        if (drawSubGrid) {
            drawSubGrid(g2, bounds, width, height, step);
        }
        
        drawMainGrid(g2, bounds, width, height, step);
    }
    
    /**
     * Draw sub-grid lines (5x subdivision)
     */
    private void drawSubGrid(Graphics2D g2, GraphBounds bounds, int width, int height, double mainStep) {
        g2.setColor(SUBGRID_COLOR);
        g2.setStroke(new BasicStroke(1));
        
        double subStep = mainStep / 5.0;
        
        // Vertical sub-grid lines
        double startX = Math.floor(bounds.getMinX() / subStep) * subStep;
        for (double x = startX; x <= bounds.getMaxX() + 1e-12; x += subStep) {
            // Skip main grid lines
            if (Math.abs(x % mainStep) > 1e-10) {
                int screenX = bounds.xToScreen(x, width);
                g2.drawLine(screenX, 0, screenX, height);
            }
        }
        
        // Horizontal sub-grid lines
        double startY = Math.floor(bounds.getMinY() / subStep) * subStep;
        for (double y = startY; y <= bounds.getMaxY() + 1e-12; y += subStep) {
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
        g2.setColor(MAIN_GRID_COLOR);
        g2.setStroke(new BasicStroke(1));
        
        // Vertical lines
        double startX = Math.floor(bounds.getMinX() / step) * step;
        for (double x = startX; x <= bounds.getMaxX() + 1e-12; x += step) {
            int screenX = bounds.xToScreen(x, width);
            g2.drawLine(screenX, 0, screenX, height);
        }
        
        // Horizontal lines
        double startY = Math.floor(bounds.getMinY() / step) * step;
        for (double y = startY; y <= bounds.getMaxY() + 1e-12; y += step) {
            int screenY = bounds.yToScreen(y, height);
            g2.drawLine(0, screenY, width, screenY);
        }
    }
}