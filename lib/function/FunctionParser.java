package lib.function;

import lib.graph.GraphFunction;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for parsing and analyzing function expressions.
 * Handles named functions, intersections, and expression transformations.
 */
public class FunctionParser {
    
    private static final Pattern NAMED_FUNCTION_PATTERN = 
        Pattern.compile("^\\s*([A-Za-z_]\\w*)\\s*\\(\\s*x\\s*\\)\\s*=.*$");
    
    private static final Pattern INTERSECTION_PATTERN = 
        Pattern.compile("^\\s*\\(.*=.*\\)\\s*$");
    
    /**
     * Result of parsing function entries
     */
    public static class ParseResult {
        private final Map<String, String> namedFunctions;
        private final List<GraphFunction> graphFunctions;
        
        public ParseResult(Map<String, String> namedFunctions, List<GraphFunction> graphFunctions) {
            this.namedFunctions = namedFunctions;
            this.graphFunctions = graphFunctions;
        }
        
        public Map<String, String> getNamedFunctions() {
            return namedFunctions;
        }
        
        public List<GraphFunction> getGraphFunctions() {
            return graphFunctions;
        }
    }
    
    /**
     * Parse a list of function entries into named functions and graph functions
     * @param entries List of function entries to parse
     * @return ParseResult containing named functions and graph functions
     */
    public static ParseResult parseEntries(List<FunctionEntry> entries) {
        Map<String, String> namedFunctions = new HashMap<>();
        List<GraphFunction> graphFunctions = new ArrayList<>();
        
        for (FunctionEntry entry : entries) {
            if (!entry.isEnabled()) continue;
            
            String expr = entry.getExpression().trim();
            Color color = entry.getColor();
            
            if (isNamedFunction(expr)) {
                parseNamedFunction(expr, color, namedFunctions, graphFunctions);
            } else if (isIntersection(expr)) {
                parseIntersection(expr, color, graphFunctions);
            } else {
                graphFunctions.add(new GraphFunction(expr, color));
            }
        }
        
        return new ParseResult(namedFunctions, graphFunctions);
    }
    
    /**
     * Check if an expression is a named function definition
     * @param expr Expression to check
     * @return true if named function
     */
    public static boolean isNamedFunction(String expr) {
        return NAMED_FUNCTION_PATTERN.matcher(expr).matches();
    }
    
    /**
     * Check if an expression is an intersection
     * @param expr Expression to check
     * @return true if intersection
     */
    public static boolean isIntersection(String expr) {
        return INTERSECTION_PATTERN.matcher(expr).matches();
    }
    
    /**
     * Parse a named function definition
     */
    private static void parseNamedFunction(String expr, Color color, 
                                           Map<String, String> namedFunctions,
                                           List<GraphFunction> graphFunctions) {
        int eqIdx = expr.indexOf('=');
        if (eqIdx <= 0) return;
        
        String left = expr.substring(0, eqIdx).trim();
        String name = left.substring(0, left.indexOf('(')).trim();
        String rhs = expr.substring(eqIdx + 1).trim();
        
        namedFunctions.put(name.toLowerCase(), rhs);
        
        // Add to graph functions for plotting
        if (isIntersection(rhs)) {
            // Named intersection: h(x)=(f=g)
            String inside = rhs.substring(1, rhs.length() - 1).trim();
            int innerEqIdx = inside.indexOf('=');
            if (innerEqIdx > 0) {
                String leftExpr = inside.substring(0, innerEqIdx).trim();
                String rightExpr = inside.substring(innerEqIdx + 1).trim();
                GraphFunction gf = GraphFunction.intersection(leftExpr, rightExpr, color);
                gf.setName(name);
                graphFunctions.add(gf);
            }
        } else {
            // Regular named function: f(x)=expression
            GraphFunction gf = new GraphFunction(rhs, color);
            gf.setName(name);
            graphFunctions.add(gf);
        }
    }
    
    /**
     * Parse an intersection expression
     */
    private static void parseIntersection(String expr, Color color,
                                          List<GraphFunction> graphFunctions) {
        String inside = expr.trim();
        inside = inside.substring(1, inside.length() - 1).trim();
        
        int eqIdx = inside.indexOf('=');
        if (eqIdx > 0) {
            String left = inside.substring(0, eqIdx).trim();
            String right = inside.substring(eqIdx + 1).trim();
            graphFunctions.add(GraphFunction.intersection(left, right, color));
        }
    }
    
    /**
     * Extract the name from a named function definition
     * @param expr Expression to parse
     * @return Function name or null if not a named function
     */
    public static String extractName(String expr) {
        if (!isNamedFunction(expr)) return null;
        
        int eqIdx = expr.indexOf('=');
        if (eqIdx <= 0) return null;
        
        String left = expr.substring(0, eqIdx).trim();
        int parenIdx = left.indexOf('(');
        if (parenIdx <= 0) return null;
        
        return left.substring(0, parenIdx).trim().toLowerCase();
    }
    
    /**
     * Extract the right-hand side expression from a named function
     * @param expr Expression to parse
     * @return RHS expression or null if not a named function
     */
    public static String extractRHS(String expr) {
        if (!isNamedFunction(expr)) return null;
        
        int eqIdx = expr.indexOf('=');
        if (eqIdx <= 0 || eqIdx >= expr.length() - 1) return null;
        
        return expr.substring(eqIdx + 1).trim();
    }
}
