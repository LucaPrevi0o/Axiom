package lib.rendering.pipeline;

import lib.constants.RenderingConstants;
import lib.core.evaluation.ExpressionEvaluator;
import lib.model.domain.GraphBounds;
import lib.rendering.IntersectionFinder;
import lib.util.ValidationUtils;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Handles rendering of regions defined by inequalities (e.g., f(x)>=g(x))
 */
public class RegionRenderer {
    
    private final ExpressionEvaluator evaluator;
    private final GraphBounds bounds;
    
    /**
     * Create a region renderer
     * @param evaluator Expression evaluator
     * @param intersectionFinder Intersection finder for boundaries
     * @param bounds Graph bounds
     */
    public RegionRenderer(ExpressionEvaluator evaluator, IntersectionFinder intersectionFinder, GraphBounds bounds) {
        this.evaluator = evaluator;
        this.bounds = bounds;
    }
    
    /**
     * Render a region defined by an inequality
     * @param g2 Graphics context
     * @param leftExpr Left-hand side expression
     * @param operator Comparison operator (">=", "<=", ">", "<")
     * @param rightExpr Right-hand side expression
     * @param color Region color
     * @param width Panel width
     * @param height Panel height
     */
    public void renderRegion(Graphics2D g2, String leftExpr, String operator, String rightExpr, 
                            Color color, int width, int height) {
        // Draw the border (where leftExpr = rightExpr)
        drawBorder(g2, leftExpr, rightExpr, color, width, height);
        
        // Fill the region based on the operator
        fillRegion(g2, leftExpr, operator, rightExpr, color, width, height);
    }
    
    /**
     * Draw the border where leftExpr = rightExpr
     */
    private void drawBorder(Graphics2D g2, String leftExpr, String rightExpr, 
                           Color color, int width, int height) {
        g2.setColor(color);
        g2.setStroke(RenderingConstants.BORDER_STROKE);
        
        Path2D path = new Path2D.Double();
        double step = (double) width / (double) RenderingConstants.REGION_SAMPLE_COUNT;
        boolean firstPoint = true;
        
        for (double sx = 0.0; sx < width; sx += step) {
            int screenX = (int) Math.round(sx);
            double x = bounds.screenToX(screenX, width);
            
            try {
                double leftY = evaluator.evaluate(leftExpr, x);
                double rightY = evaluator.evaluate(rightExpr, x);
                
                // Draw where they're approximately equal (the boundary)
                if (Math.abs(leftY - rightY) < 0.01) {
                    int screenY = bounds.yToScreen(leftY, height);
                    
                    if (firstPoint) {
                        path.moveTo(screenX, screenY);
                        firstPoint = false;
                    } else {
                        path.lineTo(screenX, screenY);
                    }
                }
            } catch (Exception e) {
                // Skip invalid points
            }
        }
        
        g2.draw(path);
    }
    
    /**
     * Fill the region based on the inequality operator
     */
    private void fillRegion(Graphics2D g2, String leftExpr, String operator, String rightExpr,
                           Color color, int width, int height) {
        // Create transparent fill color
        Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 
                                    RenderingConstants.FILL_ALPHA);
        g2.setColor(fillColor);
        
        // Sample points across the screen width
        double step = (double) width / (double) RenderingConstants.REGION_SAMPLE_COUNT;
        
        for (double sx = 0.0; sx < width; sx += step) {
            int screenX = (int) Math.round(sx);
            double x = bounds.screenToX(screenX, width);
            
            try {
                double leftY = evaluator.evaluate(leftExpr, x);
                double rightY = evaluator.evaluate(rightExpr, x);
                
                if (!ValidationUtils.isValidValue(leftY) || !ValidationUtils.isValidValue(rightY)) continue;
                
                // Determine if we should fill based on operator
                boolean shouldFill = false;
                double topY, bottomY;
                
                switch (operator) {
                    case ">=":
                    case ">":
                        // Fill where leftExpr >= rightExpr (above rightExpr, below leftExpr)
                        if (leftY >= rightY) {
                            shouldFill = true;
                            topY = leftY;
                            bottomY = rightY;
                        } else {
                            topY = rightY;
                            bottomY = leftY;
                        }
                        break;
                    case "<=":
                    case "<":
                        // Fill where leftExpr <= rightExpr (below rightExpr, above leftExpr)
                        if (leftY <= rightY) {
                            shouldFill = true;
                            topY = rightY;
                            bottomY = leftY;
                        } else {
                            topY = leftY;
                            bottomY = rightY;
                        }
                        break;
                    default:
                        continue;
                }
                
                if (shouldFill) {
                    int screenY1 = bounds.yToScreen(topY, height);
                    int screenY2 = bounds.yToScreen(bottomY, height);
                    
                    // Draw vertical line segment for this x position
                    g2.drawLine(screenX, screenY1, screenX, screenY2);
                }
            } catch (Exception e) {
                // Skip invalid points
            }
        }
    }
}
