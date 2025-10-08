package lib.core;

import lib.model.*;
import lib.rendering.IntersectionFinder;
import java.awt.Color;

/**
 * Factory for creating Function instances based on expression patterns.
 * Encapsulates the logic for determining which concrete Function class to instantiate.
 */
public class FunctionFactory {
    
    private final ExpressionEvaluator evaluator;
    private final IntersectionFinder intersectionFinder;
    
    /**
     * Create a function factory
     * @param evaluator Expression evaluator
     * @param intersectionFinder Intersection finder
     */
    public FunctionFactory(ExpressionEvaluator evaluator, IntersectionFinder intersectionFinder) {
        this.evaluator = evaluator;
        this.intersectionFinder = intersectionFinder;
    }
    
    /**
     * Create a function from an expression string
     * @param expression Expression to parse
     * @param color Display color
     * @return Appropriate Function subclass instance
     */
    public Function createFunction(String expression, Color color) {
        return createFunction(null, expression, color);
    }
    
    /**
     * Create a named function from an expression string
     * @param name Function name (or null for anonymous)
     * @param expression Expression to parse
     * @param color Display color
     * @return Appropriate Function subclass instance
     */
    public Function createFunction(String name, String expression, Color color) {
        expression = expression.trim();
        
        // Check for region: (f(x) >= g(x))
        if (isRegion(expression)) {
            return createRegionFunction(name, expression, color);
        }
        
        // Check for intersection: (f(x) = g(x))
        if (isIntersection(expression)) {
            return createIntersectionFunction(name, expression, color);
        }
        
        // Default: regular expression function
        return new ExpressionFunction(name, expression, color, evaluator);
    }
    
    /**
     * Create an intersection function from expression like "(x^2=2*x+1)"
     */
    private Function createIntersectionFunction(String name, String expression, Color color) {
        String inside = expression.substring(1, expression.length() - 1).trim();
        int eqIdx = inside.indexOf('=');
        
        if (eqIdx > 0) {
            String leftExpr = inside.substring(0, eqIdx).trim();
            String rightExpr = inside.substring(eqIdx + 1).trim();
            return new IntersectionFunction(name, leftExpr, rightExpr, color, intersectionFinder);
        }
        
        // Fallback to regular function if parsing fails
        return new ExpressionFunction(name, expression, color, evaluator);
    }
    
    /**
     * Create a region function from expression like "(x^2>=2*x+1)"
     */
    private Function createRegionFunction(String name, String expression, Color color) {
        String inside = expression.substring(1, expression.length() - 1).trim();
        
        // Find the operator
        String operator = null;
        int opIdx = -1;
        
        if (inside.contains(">=")) {
            operator = ">=";
            opIdx = inside.indexOf(">=");
        } else if (inside.contains("<=")) {
            operator = "<=";
            opIdx = inside.indexOf("<=");
        } else if (inside.contains(">")) {
            operator = ">";
            opIdx = inside.indexOf(">");
        } else if (inside.contains("<")) {
            operator = "<";
            opIdx = inside.indexOf("<");
        }
        
        if (operator != null && opIdx > 0) {
            String leftExpr = inside.substring(0, opIdx).trim();
            String rightExpr = inside.substring(opIdx + operator.length()).trim();
            return new RegionFunction(name, leftExpr, operator, rightExpr, color, evaluator);
        }
        
        // Fallback to regular function if parsing fails
        return new ExpressionFunction(name, expression, color, evaluator);
    }
    
    /**
     * Check if expression is an intersection pattern: (expr=expr)
     */
    private boolean isIntersection(String expr) {
        return expr.matches("^\\s*\\(.*=.*\\)\\s*$") && !isRegion(expr);
    }
    
    /**
     * Check if expression is a region pattern: (expr>=expr), (expr<=expr), etc.
     */
    private boolean isRegion(String expr) {
        return expr.matches("^\\s*\\(.*(?:>=|<=|>|<).*\\)\\s*$");
    }
}
