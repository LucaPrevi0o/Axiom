package lib.core;

import lib.model.GraphFunction;
import lib.model.Parameter;
import lib.ui.component.FunctionEntry;
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
    
    private static final Pattern REGION_PATTERN = 
        Pattern.compile("^\\s*\\(.*(?:>=|<=|>|<).*\\)\\s*$");
    
    private static final Pattern PARAMETER_PATTERN = 
        Pattern.compile("^\\s*([A-Za-z_]\\w*)\\s*=\\s*\\[\\s*(-?\\d+\\.?\\d*)\\s*:\\s*(-?\\d+\\.?\\d*)\\s*\\]\\s*$");
    
    /**
     * Result of parsing function entries
     */
    public static class ParseResult {
        private final Map<String, String> namedFunctions;
        private final List<GraphFunction> graphFunctions;
        private final List<Parameter> parameters;
        
        public ParseResult(Map<String, String> namedFunctions, List<GraphFunction> graphFunctions, List<Parameter> parameters) {
            this.namedFunctions = namedFunctions;
            this.graphFunctions = graphFunctions;
            this.parameters = parameters;
        }
        
        public Map<String, String> getNamedFunctions() {
            return namedFunctions;
        }
        
        public List<GraphFunction> getGraphFunctions() {
            return graphFunctions;
        }
        
        public List<Parameter> getParameters() {
            return parameters;
        }
    }
    
    /**
     * Parse a list of function entries into named functions and graph functions
     * @param entries List of function entries to parse
     * @return ParseResult containing named functions, graph functions, and parameters
     */
    public static ParseResult parseEntries(List<FunctionEntry> entries) {
        Map<String, String> namedFunctions = new HashMap<>();
        List<GraphFunction> graphFunctions = new ArrayList<>();
        List<Parameter> parameters = new ArrayList<>();
        
        for (FunctionEntry entry : entries) {
            if (!entry.isEnabled()) continue;
            
            String expr = entry.getExpression().trim();
            Color color = entry.getColor();
            
            if (isParameter(expr)) {
                Parameter param = parseParameter(expr);
                if (param != null) {
                    parameters.add(param);
                }
            } else if (isNamedFunction(expr)) {
                parseNamedFunction(expr, color, namedFunctions, graphFunctions);
            } else if (isRegion(expr)) {
                parseRegion(expr, color, graphFunctions);
            } else if (isIntersection(expr)) {
                parseIntersection(expr, color, graphFunctions);
            } else {
                graphFunctions.add(new GraphFunction(expr, color));
            }
        }
        
        return new ParseResult(namedFunctions, graphFunctions, parameters);
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
     * Check if an expression is a region (e.g., f(x)>=g(x))
     * @param expr Expression to check
     * @return true if region
     */
    public static boolean isRegion(String expr) {
        return REGION_PATTERN.matcher(expr).matches();
    }
    
    /**
     * Check if an expression is a parameter definition (e.g., c=[2:5])
     * @param expr Expression to check
     * @return true if parameter
     */
    public static boolean isParameter(String expr) {
        return PARAMETER_PATTERN.matcher(expr).matches();
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
        if (isRegion(rhs)) {
            // Named region: h(x)=(f>=g)
            String inside = rhs.substring(1, rhs.length() - 1).trim();
            
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
                GraphFunction gf = GraphFunction.region(leftExpr, operator, rightExpr, color);
                gf.setName(name);
                graphFunctions.add(gf);
            }
        } else if (isIntersection(rhs)) {
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
     * Parse a region expression (e.g., (f(x)>=g(x)))
     */
    private static void parseRegion(String expr, Color color,
                                    List<GraphFunction> graphFunctions) {
        String inside = expr.trim();
        inside = inside.substring(1, inside.length() - 1).trim();
        
        // Find the operator (>=, <=, >, <)
        String operator = null;
        int opIdx = -1;
        
        // Check for >= and <= first (two-character operators)
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
            String left = inside.substring(0, opIdx).trim();
            String right = inside.substring(opIdx + operator.length()).trim();
            graphFunctions.add(GraphFunction.region(left, operator, right, color));
        }
    }
    
    /**
     * Parse a parameter definition (e.g., c=[2:5])
     * @param expr Expression to parse
     * @return Parameter object or null if invalid
     */
    public static Parameter parseParameter(String expr) {
        java.util.regex.Matcher matcher = PARAMETER_PATTERN.matcher(expr);
        if (matcher.matches()) {
            try {
                String name = matcher.group(1);
                double min = Double.parseDouble(matcher.group(2));
                double max = Double.parseDouble(matcher.group(3));
                
                if (min >= max) {
                    return null; // Invalid range
                }
                
                return new Parameter(name, min, max);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
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
