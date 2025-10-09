package lib.core.parser;

import lib.model.function.base.PlottableFunction;
import lib.model.function.definition.SetFunction;
import lib.model.domain.Parameter;
import lib.core.factory.FunctionFactory;
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
    
    // Continuous parameter: a=[1:5] (square brackets with colon)
    private static final Pattern PARAMETER_PATTERN = 
        Pattern.compile("^\\s*([A-Za-z_]\\w*)\\s*=\\s*\\[\\s*(-?\\d+\\.?\\d*)\\s*:\\s*(-?\\d+\\.?\\d*)\\s*\\]\\s*$");
    
    // Discrete parameter: a=[1..10] (square brackets with double dots for slider)
    private static final Pattern DISCRETE_PARAMETER_PATTERN = 
        Pattern.compile("^\\s*([A-Za-z_]\\w*)\\s*=\\s*\\[\\s*(-?\\d+)\\s*\\.\\.\\s*(-?\\d+)\\s*\\]\\s*$");
    
    // Explicit set: a={1,2,3,4} (curly brackets with comma-separated values)
    private static final Pattern EXPLICIT_SET_PATTERN = 
        Pattern.compile("^\\s*([A-Za-z_]\\w*)\\s*=\\s*\\{\\s*(-?\\d+(?:\\.\\d+)?)(?:\\s*,\\s*-?\\d+(?:\\.\\d+)?)*\\s*\\}\\s*$");
    
    // Range set: b={1:10} (curly brackets with colon for integer range)
    private static final Pattern RANGE_SET_PATTERN = 
        Pattern.compile("^\\s*([A-Za-z_]\\w*)\\s*=\\s*\\{\\s*(-?\\d+)\\s*:\\s*(-?\\d+)\\s*\\}\\s*$");
    
    // Pattern for point functions: P=(x,y) where x and y can be complex expressions
    // We use a simpler pattern and parse the coordinates manually to handle nested parentheses
    private static final Pattern POINT_PATTERN = 
        Pattern.compile("^\\s*([A-Za-z_]\\w*)\\s*=\\s*\\((.+)\\)\\s*$");
    
    /**
     * Result of parsing function entries
     */
    public static class ParseResult {
        private final Map<String, String> namedFunctions;
        private final List<PlottableFunction> functions;
        private final List<SetFunction> sets;
        private final List<Parameter> parameters;
        
        public ParseResult(Map<String, String> namedFunctions, List<PlottableFunction> functions, 
                          List<SetFunction> sets, List<Parameter> parameters) {
            this.namedFunctions = namedFunctions;
            this.functions = functions;
            this.sets = sets;
            this.parameters = parameters;
        }
        
        public Map<String, String> getNamedFunctions() {
            return namedFunctions;
        }
        
        public List<PlottableFunction> getFunctions() {
            return functions;
        }
        
        public List<SetFunction> getSets() {
            return sets;
        }
        
        public List<Parameter> getParameters() {
            return parameters;
        }
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
     * Check if an expression is a parameter definition (e.g., c=[2:5] or b=[1..10])
     * @param expr Expression to check
     * @return true if parameter
     */
    public static boolean isParameter(String expr) {
        return PARAMETER_PATTERN.matcher(expr).matches() || 
               DISCRETE_PARAMETER_PATTERN.matcher(expr).matches();
    }
    
    /**
     * Check if an expression is a set definition (explicit or range)
     * @param expr Expression to check
     * @return true if set
     */
    public static boolean isSet(String expr) {
        return EXPLICIT_SET_PATTERN.matcher(expr).matches() || 
               RANGE_SET_PATTERN.matcher(expr).matches();
    }
    
    /**
     * Check if an expression is an explicit set (e.g., a={1,2,3,4})
     * @param expr Expression to check
     * @return true if explicit set
     */
    public static boolean isExplicitSet(String expr) {
        return EXPLICIT_SET_PATTERN.matcher(expr).matches();
    }
    
    /**
     * Check if an expression is a range set (e.g., b={1:10})
     * @param expr Expression to check
     * @return true if range set
     */
    public static boolean isRangeSet(String expr) {
        return RANGE_SET_PATTERN.matcher(expr).matches();
    }
    
    /**
     * Check if an expression is a point definition (e.g., P=(1,2))
     * @param expr Expression to check
     * @return true if point
     */
    public static boolean isPoint(String expr) {
        return POINT_PATTERN.matcher(expr).matches();
    }
    
    /**
     * Parse a named function definition
     */
    private static void parseNamedFunction(String expr, Color color, 
                                           Map<String, String> namedFunctions,
                                           List<PlottableFunction> functions,
                                           FunctionFactory factory) {
        int eqIdx = expr.indexOf('=');
        if (eqIdx <= 0) return;
        
        String left = expr.substring(0, eqIdx).trim();
        String name = left.substring(0, left.indexOf('(')).trim();
        String rhs = expr.substring(eqIdx + 1).trim();
        
        namedFunctions.put(name.toLowerCase(), rhs);
        
        // Create the function using factory with the name
        PlottableFunction function = factory.createFunction(name, rhs, color);
        functions.add(function);
    }
    
    /**
     * Parse a point definition (e.g., P=(1,2) or P=(a,0) or P=(3,f(3)))
     */
    private static void parsePoint(String expr, Color color,
                                   List<PlottableFunction> functions,
                                   FunctionFactory factory) {
        java.util.regex.Matcher matcher = POINT_PATTERN.matcher(expr);
        if (matcher.matches()) {
            String name = matcher.group(1);
            String coords = matcher.group(2); // Everything inside the outer parentheses
            
            // Manually split coordinates by comma, handling nested parentheses
            int commaPos = findTopLevelComma(coords);
            if (commaPos == -1) {
                return;
            }
            
            String xStr = coords.substring(0, commaPos).trim();
            String yStr = coords.substring(commaPos + 1).trim();
            
            // Create a parametric point function using factory
            // This handles literal numbers, parameters, and function calls
            PlottableFunction function = factory.createParametricPointFunction(name, xStr, yStr, color);
            functions.add(function);
        }
    }
    
    /**
     * Find the position of the top-level comma (not inside parentheses)
     * Returns -1 if no top-level comma is found
     */
    private static int findTopLevelComma(String str) {
        int depth = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (c == ',' && depth == 0) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Parse a parameter definition (e.g., c=[2:5] or b=[1..10])
     * @param expr Expression to parse
     * @return Parameter object or null if invalid
     */
    public static Parameter parseParameter(String expr) {
        // Try discrete parameter first (double dot separator)
        java.util.regex.Matcher discreteMatcher = DISCRETE_PARAMETER_PATTERN.matcher(expr);
        if (discreteMatcher.matches()) {
            try {
                String name = discreteMatcher.group(1);
                int min = Integer.parseInt(discreteMatcher.group(2));
                int max = Integer.parseInt(discreteMatcher.group(3));
                
                if (min >= max) {
                    return null; // Invalid range
                }
                
                return new Parameter(name, min, max, true); // true = discrete
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        // Try continuous parameter (colon separator)
        java.util.regex.Matcher matcher = PARAMETER_PATTERN.matcher(expr);
        if (matcher.matches()) {
            try {
                String name = matcher.group(1);
                double min = Double.parseDouble(matcher.group(2));
                double max = Double.parseDouble(matcher.group(3));
                
                if (min >= max) {
                    return null; // Invalid range
                }
                
                return new Parameter(name, min, max, false); // false = continuous
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * Parse set name and values from explicit set (e.g., a={1,2,3,4})
     * @param expr Expression to parse
     * @return Object array [String name, double[] values] or null if invalid
     */
    public static Object[] parseExplicitSet(String expr) {
        java.util.regex.Matcher matcher = EXPLICIT_SET_PATTERN.matcher(expr);
        if (!matcher.matches()) {
            return null;
        }
        
        try {
            String name = matcher.group(1);
            
            // Extract all values from the braces
            int openBrace = expr.indexOf('{');
            int closeBrace = expr.lastIndexOf('}');
            if (openBrace == -1 || closeBrace == -1) {
                return null;
            }
            
            String content = expr.substring(openBrace + 1, closeBrace).trim();
            String[] parts = content.split("\\s*,\\s*");
            
            double[] values = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                values[i] = Double.parseDouble(parts[i]);
            }
            
            return new Object[] { name, values };
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Parse set name and range from range set (e.g., b={1:10})
     * @param expr Expression to parse
     * @return Object array [String name, int min, int max] or null if invalid
     */
    public static Object[] parseRangeSet(String expr) {
        java.util.regex.Matcher matcher = RANGE_SET_PATTERN.matcher(expr);
        if (!matcher.matches()) {
            return null;
        }
        
        try {
            String name = matcher.group(1);
            int min = Integer.parseInt(matcher.group(2));
            int max = Integer.parseInt(matcher.group(3));
            
            if (min >= max) {
                return null; // Invalid range
            }
            
            return new Object[] { name, min, max };
        } catch (NumberFormatException e) {
            return null;
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
