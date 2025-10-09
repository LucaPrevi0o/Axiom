package lib.core.factory;

import lib.model.function.base.PlottableFunction;
import lib.model.function.expression.RegularFunction;
import lib.model.function.geometric.PointFunction;
import lib.model.function.composite.EquationFunction;
import lib.model.function.composite.InequationFunction;
import lib.model.function.definition.SetFunction;
import lib.core.evaluation.ExpressionEvaluator;
import lib.core.parser.FunctionParser;
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
     * @return Appropriate PlottableFunction subclass instance
     */
    public PlottableFunction createFunction(String expression, Color color) {
        return createFunction(null, expression, color);
    }
    
    /**
     * Create a named function from an expression string
     * @param name Function name (or null for anonymous)
     * @param expression Expression to parse
     * @param color Display color
     * @return Appropriate PlottableFunction subclass instance
     */
    public PlottableFunction createFunction(String name, String expression, Color color) {
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
        return new RegularFunction(name, expression, color, evaluator);
    }
    
    /**
     * Create an intersection function from expression like "(x^2=2*x+1)"
     */
    private PlottableFunction createIntersectionFunction(String name, String expression, Color color) {
        String inside = expression.substring(1, expression.length() - 1).trim();
        int eqIdx = inside.indexOf('=');
        
        if (eqIdx > 0) {
            String leftExpr = inside.substring(0, eqIdx).trim();
            String rightExpr = inside.substring(eqIdx + 1).trim();
            return new EquationFunction(name, leftExpr, rightExpr, color, intersectionFinder);
        }
        
        // Fallback to regular function if parsing fails
        return new RegularFunction(name, expression, color, evaluator);
    }
    
    /**
     * Create a region function from expression like "(x^2>=2*x+1)"
     */
    private PlottableFunction createRegionFunction(String name, String expression, Color color) {
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
            return new InequationFunction(name, leftExpr, operator, rightExpr, color, evaluator);
        }
        
        // Fallback to regular function if parsing fails
        return new RegularFunction(name, expression, color, evaluator);
    }
    
    /**
     * Create a point function from coordinates
     * @param name Point name (e.g., "P", "A")
     * @param x X coordinate
     * @param y Y coordinate
     * @param color Display color
     * @return PointFunction with a single point
     */
    public PlottableFunction createPointFunction(String name, double x, double y, Color color) {
        double[] xValues = {x};
        double[] yValues = {y};
        return new PointFunction(name, xValues, yValues, color);
    }
    
    /**
     * Create a parametric point function from coordinate expressions
     * @param name Point name (e.g., "P", "A")
     * @param xExpr X coordinate expression (can be a number or parameter name)
     * @param yExpr Y coordinate expression (can be a number or parameter name)
     * @param color Display color
     * @return PointFunction that evaluates coordinates dynamically
     */
    public PlottableFunction createParametricPointFunction(String name, String xExpr, String yExpr, Color color) {
        return new PointFunction(name, xExpr, yExpr, color, evaluator);
    }
    
    /**
     * Create a set function from expression like "a={1,2,3}" or "b={1:10}"
     * Sets are non-plottable BaseFunction instances that serve as value containers
     * @param expression Set expression
     * @return SetFunction or null if parsing fails
     */
    public SetFunction createSetFunction(String expression) {
        expression = expression.trim();
        
        // Try explicit set first: a={1,2,3,4}
        if (FunctionParser.isExplicitSet(expression)) {
            Object[] result = FunctionParser.parseExplicitSet(expression);
            if (result != null && result.length == 2) {
                String name = (String) result[0];
                double[] values = (double[]) result[1];
                return SetFunction.fromExplicitValues(name, values);
            }
        }
        
        // Try range set: b={1:10}
        if (FunctionParser.isRangeSet(expression)) {
            Object[] result = FunctionParser.parseRangeSet(expression);
            if (result != null && result.length == 3) {
                String name = (String) result[0];
                int min = (Integer) result[1];
                int max = (Integer) result[2];
                return SetFunction.fromRange(name, min, max);
            }
        }
        
        return null;
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
