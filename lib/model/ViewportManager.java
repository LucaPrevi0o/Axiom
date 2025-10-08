package lib.model;

import lib.constants.GraphConstants;
import lib.util.ValidationUtils;
import java.awt.Point;

/**
 * Manages viewport operations including zoom, pan, and resize preservation.
 */
public class ViewportManager {
    
    private final GraphBounds bounds;
    private Point lastMousePoint;
    private boolean isDragging = false;
    
    /**
     * Create a viewport manager with the given bounds
     * @param bounds Graph bounds to manage
     */
    public ViewportManager(GraphBounds bounds) {
        this.bounds = bounds;
    }
    
    /**
     * Handle zoom operation at a specific screen position
     * @param zoomIn true to zoom in, false to zoom out
     * @param mouseScreenX Mouse X position in screen coordinates
     * @param mouseScreenY Mouse Y position in screen coordinates
     * @param screenWidth Width of the screen
     * @param screenHeight Height of the screen
     */
    public void zoom(boolean zoomIn, int mouseScreenX, int mouseScreenY, 
                    int screenWidth, int screenHeight) {
        double zoomFactor = zoomIn ? GraphConstants.ZOOM_IN_FACTOR : GraphConstants.ZOOM_OUT_FACTOR;
        
        // Graph coordinate under mouse before zoom
        double mouseGraphX = bounds.screenToX(mouseScreenX, screenWidth);
        double mouseGraphY = bounds.screenToY(mouseScreenY, screenHeight);
        
        // Zoom around the mouse position
        bounds.zoom(zoomFactor, mouseGraphX, mouseGraphY);
        
        // Enforce square aspect ratio after zoom
        bounds.enforceSquareAspectRatio(screenWidth, screenHeight);
    }
    
    /**
     * Start a pan operation
     * @param mousePoint Starting mouse position
     */
    public void startPan(Point mousePoint) {
        lastMousePoint = mousePoint;
        isDragging = true;
    }
    
    /**
     * Update pan with new mouse position
     * @param mousePoint Current mouse position
     * @param screenWidth Width of the screen
     * @param screenHeight Height of the screen
     */
    public void updatePan(Point mousePoint, int screenWidth, int screenHeight) {
        if (!isDragging || lastMousePoint == null) return;
        
        int dx = mousePoint.x - lastMousePoint.x;
        int dy = mousePoint.y - lastMousePoint.y;
        
        // Convert pixel movement to graph coordinate movement
        double graphDx = dx * bounds.getRangeX() / screenWidth;
        double graphDy = -dy * bounds.getRangeY() / screenHeight; // Negative because screen Y is inverted
        
        bounds.pan(-graphDx, -graphDy);
        
        // Enforce square aspect ratio after pan
        bounds.enforceSquareAspectRatio(screenWidth, screenHeight);
        
        lastMousePoint = mousePoint;
    }
    
    /**
     * End pan operation
     */
    public void endPan() {
        isDragging = false;
        lastMousePoint = null;
    }
    
    /**
     * Check if currently panning
     * @return true if dragging
     */
    public boolean isDragging() {
        return isDragging;
    }
    
    /**
     * Preserve zoom level when screen is resized
     * @param newWidth New screen width
     * @param newHeight New screen height
     * @param oldWidth Old screen width
     * @param oldHeight Old screen height
     */
    public void preserveZoomOnResize(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        if (!ValidationUtils.areValidDimensions(newWidth, newHeight) || 
            !ValidationUtils.areValidDimensions(oldWidth, oldHeight)) {
            return;
        }
        
        // Current center in graph coordinates
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        
        // Current units per pixel (use the larger to maintain square grid)
        double unitsPerPixelX = bounds.getRangeX() / Math.max(1, oldWidth);
        double unitsPerPixelY = bounds.getRangeY() / Math.max(1, oldHeight);
        double unitsPerPixel = Math.max(unitsPerPixelX, unitsPerPixelY);
        
        // Recompute ranges preserving square aspect ratio
        double newRangeX = unitsPerPixel * newWidth;
        double newRangeY = unitsPerPixel * newHeight;
        
        double minX = centerX - newRangeX / 2.0;
        double maxX = centerX + newRangeX / 2.0;
        double minY = centerY - newRangeY / 2.0;
        double maxY = centerY + newRangeY / 2.0;
        
        bounds.setBounds(minX, maxX, minY, maxY);
        
        // Enforce square aspect ratio
        bounds.enforceSquareAspectRatio(newWidth, newHeight);
    }
}
