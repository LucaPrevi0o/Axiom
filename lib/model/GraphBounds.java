package lib.model;

/**
 * Manages graph coordinate bounds and coordinate conversion between
 * graph space and screen space.
 */
public class GraphBounds {
    
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    
    /**
     * Create graph bounds with specified ranges
     * @param minX Minimum X coordinate
     * @param maxX Maximum X coordinate
     * @param minY Minimum Y coordinate
     * @param maxY Maximum Y coordinate
     */
    public GraphBounds(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
    
    /**
     * Create graph bounds centered at origin with specified half-widths
     * @param halfWidth Half of the X range
     * @param halfHeight Half of the Y range
     */
    public static GraphBounds centered(double halfWidth, double halfHeight) {
        return new GraphBounds(-halfWidth, halfWidth, -halfHeight, halfHeight);
    }
    
    // Getters
    public double getMinX() { return minX; }
    public double getMaxX() { return maxX; }
    public double getMinY() { return minY; }
    public double getMaxY() { return maxY; }
    
    // Setters
    public void setMinX(double minX) { this.minX = minX; }
    public void setMaxX(double maxX) { this.maxX = maxX; }
    public void setMinY(double minY) { this.minY = minY; }
    public void setMaxY(double maxY) { this.maxY = maxY; }
    
    /**
     * Set all bounds at once
     */
    public void setBounds(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
    
    /**
     * Get the X range (width)
     */
    public double getRangeX() {
        return maxX - minX;
    }
    
    /**
     * Get the Y range (height)
     */
    public double getRangeY() {
        return maxY - minY;
    }
    
    /**
     * Get the center X coordinate
     */
    public double getCenterX() {
        return (minX + maxX) / 2.0;
    }
    
    /**
     * Get the center Y coordinate
     */
    public double getCenterY() {
        return (minY + maxY) / 2.0;
    }
    
    /**
     * Convert graph X coordinate to screen X coordinate
     * @param x Graph X coordinate
     * @param screenWidth Width of the screen in pixels
     * @return Screen X coordinate
     */
    public int xToScreen(double x, int screenWidth) {
        return (int) ((x - minX) / getRangeX() * screenWidth);
    }
    
    /**
     * Convert graph Y coordinate to screen Y coordinate
     * @param y Graph Y coordinate
     * @param screenHeight Height of the screen in pixels
     * @return Screen Y coordinate
     */
    public int yToScreen(double y, int screenHeight) {
        return (int) ((maxY - y) / getRangeY() * screenHeight);
    }
    
    /**
     * Convert screen X coordinate to graph X coordinate
     * @param screenX Screen X coordinate
     * @param screenWidth Width of the screen in pixels
     * @return Graph X coordinate
     */
    public double screenToX(int screenX, int screenWidth) {
        return minX + (screenX * getRangeX() / screenWidth);
    }
    
    /**
     * Convert screen Y coordinate to graph Y coordinate
     * @param screenY Screen Y coordinate
     * @param screenHeight Height of the screen in pixels
     * @return Graph Y coordinate
     */
    public double screenToY(int screenY, int screenHeight) {
        return maxY - (screenY * getRangeY() / screenHeight);
    }
    
    /**
     * Pan (translate) the view by the specified graph deltas
     * @param deltaX Amount to pan in X direction (graph units)
     * @param deltaY Amount to pan in Y direction (graph units)
     */
    public void pan(double deltaX, double deltaY) {
        minX += deltaX;
        maxX += deltaX;
        minY += deltaY;
        maxY += deltaY;
    }
    
    /**
     * Zoom the view around a specific point
     * @param zoomFactor Factor to zoom by (< 1 zooms in, > 1 zooms out)
     * @param focusX X coordinate to zoom around (graph units)
     * @param focusY Y coordinate to zoom around (graph units)
     */
    public void zoom(double zoomFactor, double focusX, double focusY) {
        double newRangeX = getRangeX() * zoomFactor;
        double newRangeY = getRangeY() * zoomFactor;
        
        // Calculate how far the focus point is from minX/maxY as a fraction
        double relX = (focusX - minX) / getRangeX();
        double relY = (maxY - focusY) / getRangeY();
        
        // Set new bounds maintaining the same focus point position
        minX = focusX - relX * newRangeX;
        maxX = minX + newRangeX;
        maxY = focusY + relY * newRangeY;
        minY = maxY - newRangeY;
    }
    
    /**
     * Adjust bounds to maintain square aspect ratio (1:1)
     * The larger dimension will be reduced to match the smaller one
     * @param screenWidth Width of the screen in pixels
     * @param screenHeight Height of the screen in pixels
     */
    public void enforceSquareAspectRatio(int screenWidth, int screenHeight) {
        if (screenWidth <= 0 || screenHeight <= 0) return;
        
        // Calculate current units per pixel for both dimensions
        double unitsPerPixelX = getRangeX() / screenWidth;
        double unitsPerPixelY = getRangeY() / screenHeight;
        
        // Use the larger units per pixel to maintain square grid
        double unitsPerPixel = Math.max(unitsPerPixelX, unitsPerPixelY);
        
        // Calculate new ranges that maintain square aspect ratio
        double newRangeX = unitsPerPixel * screenWidth;
        double newRangeY = unitsPerPixel * screenHeight;
        
        // Center the new bounds around the current center
        double centerX = getCenterX();
        double centerY = getCenterY();
        
        minX = centerX - newRangeX / 2.0;
        maxX = centerX + newRangeX / 2.0;
        minY = centerY - newRangeY / 2.0;
        maxY = centerY + newRangeY / 2.0;
    }
}
