package lib.panel.plot;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JPanel;

/**
 * Manages the viewport bounds (min/max X/Y) for the plot panel
 * and handles zoom and pan interactions
 */
public class PlotViewport {
    
    // Graph bounds
    private double minX = -10;
    private double maxX = 10;
    private double minY = -10;
    private double maxY = 10;
    
    // Mouse interaction state
    private Point lastDragPoint = null;
    
    // Zoom settings
    private static final double ZOOM_FACTOR = 0.1; // 10% zoom per wheel tick
    
    /**
     * Constructor with default bounds (-10 to 10 on both axes)
     */
    public PlotViewport() {}
    
    /**
     * Constructor with custom bounds
     * 
     * @param minX Minimum X value
     * @param maxX Maximum X value
     * @param minY Minimum Y value
     * @param maxY Maximum Y value
     */
    public PlotViewport(double minX, double maxX, double minY, double maxY) {

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
    
    /**
     * Attach mouse listeners to a panel for zoom and pan functionality
     * 
     * @param panel The panel to attach listeners to
     */
    public void attachMouseListeners(JPanel panel) {

        MouseAdapter mouseAdapter = new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) lastDragPoint = e.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) lastDragPoint = null;
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {

                    handlePan(e.getPoint(), lastDragPoint, panel.getWidth(), panel.getHeight());
                    lastDragPoint = e.getPoint();
                    panel.repaint();
                }
            }
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                handleZoom(e.getWheelRotation(), e.getX(), e.getY(), panel.getWidth(), panel.getHeight());
                panel.repaint();
            }
        };
        
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
        panel.addMouseWheelListener(mouseAdapter);
    }
    
    /**
     * Handle zoom operation
     * 
     * @param wheelRotation Positive for zoom out, negative for zoom in
     * @param mouseX Mouse X position in screen coordinates
     * @param mouseY Mouse Y position in screen coordinates
     * @param panelWidth Width of the panel
     * @param panelHeight Height of the panel
     */
    private void handleZoom(int wheelRotation, int mouseX, int mouseY, int panelWidth, int panelHeight) {

        // Convert mouse position to graph coordinates
        double graphX = screenToX(mouseX, panelWidth);
        double graphY = screenToY(mouseY, panelHeight);
        
        // Calculate zoom amount
        double zoomAmount = wheelRotation > 0 ? (1 + ZOOM_FACTOR) : (1 - ZOOM_FACTOR);
        
        // Calculate current ranges
        double rangeX = maxX - minX;
        double rangeY = maxY - minY;
        
        // Calculate new ranges
        double newRangeX = rangeX * zoomAmount;
        double newRangeY = rangeY * zoomAmount;
        
        // Calculate how far the mouse is from the center (0-1 range)
        double mouseRatioX = (graphX - minX) / rangeX;
        double mouseRatioY = (graphY - minY) / rangeY;
        
        // Update bounds, keeping the point under the mouse fixed
        minX = graphX - newRangeX * mouseRatioX;
        maxX = graphX + newRangeX * (1 - mouseRatioX);
        minY = graphY - newRangeY * mouseRatioY;
        maxY = graphY + newRangeY * (1 - mouseRatioY);
    }
    
    /**
     * Handle pan operation
     * 
     * @param currentPoint Current mouse position
     * @param lastPoint Previous mouse position
     * @param panelWidth Width of the panel
     * @param panelHeight Height of the panel
     */
    private void handlePan(Point currentPoint, Point lastPoint, int panelWidth, int panelHeight) {

        // Calculate screen delta
        int deltaScreenX = currentPoint.x - lastPoint.x;
        int deltaScreenY = currentPoint.y - lastPoint.y;
        
        // Convert to graph coordinates delta
        double deltaGraphX = -(deltaScreenX * (maxX - minX) / panelWidth);
        double deltaGraphY = (deltaScreenY * (maxY - minY) / panelHeight);
        
        // Update bounds
        minX += deltaGraphX;
        maxX += deltaGraphX;
        minY += deltaGraphY;
        maxY += deltaGraphY;
    }
    
    /**
     * Reset viewport to default bounds
     */
    public void reset() {

        minX = -10;
        maxX = 10;
        minY = -10;
        maxY = 10;
    }
    
    /**
     * Convert screen X coordinate to graph coordinate
     * 
     * @param screenX Screen X coordinate
     * @param panelWidth Width of the panel
     * @return Graph X coordinate
     */
    private double screenToX(int screenX, int panelWidth) { return minX + (screenX * (maxX - minX) / panelWidth); }
    
    /**
     * Convert screen Y coordinate to graph coordinate
     * 
     * @param screenY Screen Y coordinate
     * @param panelHeight Height of the panel
     * @return Graph Y coordinate
     */
    private double screenToY(int screenY, int panelHeight) { return maxY - (screenY * (maxY - minY) / panelHeight); }
    
    // Getters and setters

    public double getMinX() { return minX; }

    public void setMinX(double minX) { this.minX = minX; }

    public double getMaxX() { return maxX; }

    public void setMaxX(double maxX) { this.maxX = maxX; }

    public double getMinY() { return minY; }

    public void setMinY(double minY) { this.minY = minY; }

    public double getMaxY() { return maxY; }

    public void setMaxY(double maxY) { this.maxY = maxY; }

    /**
     * Set all bounds at once
     * 
     * @param minX Minimum X value
     * @param maxX Maximum X value
     * @param minY Minimum Y value
     * @param maxY Maximum Y value
     */
    public void setBounds(double minX, double maxX, double minY, double maxY) {

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
}
