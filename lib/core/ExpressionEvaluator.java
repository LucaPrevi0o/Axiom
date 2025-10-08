package lib.core;
import java.util.Map;

public class ExpressionEvaluator {

    private Map<String, String> userFunctions;
    private Map<String, Double> parameters;

    public ExpressionEvaluator() { this(null, null); }

    public ExpressionEvaluator(Map<String, String> userFunctions) { 
        this(userFunctions, null); 
    }
    
    public ExpressionEvaluator(Map<String, String> userFunctions, Map<String, Double> parameters) {
        this.userFunctions = userFunctions;
        this.parameters = parameters;
    }
    
    /**
     * Evaluate the mathematical expression for a given value of {@code x}
     * @param expression The function expression as a string
     * @param x The value to substitute for {@code x} in the expression
     * @return The evaluated result ({@code double})
     * @throws Exception If the expression is invalid
     */
    public double evaluate(String expression, double x) throws Exception {
    expression = expression.toLowerCase().trim();
    
    // Replace parameter names with their values
    if (parameters != null) {
        for (Map.Entry<String, Double> entry : parameters.entrySet()) {
            String paramName = entry.getKey().toLowerCase();
            String paramValue = String.valueOf(entry.getValue());
            // Replace whole-word occurrences of parameter name
            expression = expression.replaceAll("(?i)\\b" + paramName + "\\b", "(" + paramValue + ")");
        }
    }
    
    // Replace whole-word occurrences of x with a parenthesized numeric value so
    // expressions like x^2 evaluate correctly when x is negative (e.g. (-2)^2).
    String xVal = String.valueOf(x);
    expression = expression.replaceAll("(?i)\\bx\\b", "(" + xVal + ")");
        
        // Handle basic math functions
        expression = handleFunctions(expression);
        
        // Evaluate the expression
        return evaluateExpression(expression);
    }
    
    /**
     * Evaluate a constant expression (doesn't depend on x)
     * This is useful for evaluating expressions like "f(4)" or "a+b" where
    /**
     * Evaluate an expression that doesn't depend on x (constant evaluation).
     * This is used for point coordinates like P=(3,f(3)) where we need to evaluate
     * f(3) with a specific numeric argument. It handles:
     * - Literal numbers: "3" → 3.0
     * - Parameters: "a" → value of parameter a
     * - Function calls: "f(4)" → evaluate f at x=4
     * - Expressions: "a+f(4)" → evaluate with parameters and function calls
     * 
     * Key difference from evaluate(expression, x):
     * This method replaces parameters and evaluates function calls independently.
     * For example, in P=(a,f(a)), if a=3:
     * - a gets replaced with 3
     * - f(a) becomes f(3) and gets evaluated at x=3
     * 
     * In contrast, evaluate(expression, x) substitutes a single x value everywhere.
     * For example, f(x)+g(x) at x=5 evaluates both f and g at 5.
     * 
     * Example scenarios:
     * - P=(1,2): literal coordinates
     * - P=(a,0): a is a parameter, a and b are parameters, but x is not involved.
     * @param expression The expression to evaluate
     * @return The evaluated result
     * @throws Exception If the expression is invalid
     */
    public double evaluateConstant(String expression) throws Exception {
        expression = expression.toLowerCase().trim();
        
        // Replace parameter names with their values
        if (parameters != null) {
            for (Map.Entry<String, Double> entry : parameters.entrySet()) {
                String paramName = entry.getKey().toLowerCase();
                String paramValue = String.valueOf(entry.getValue());
                // Replace whole-word occurrences of parameter name
                expression = expression.replaceAll("(?i)\\b" + paramName + "\\b", "(" + paramValue + ")");
            }
        }
        
        // Don't replace x - let the parser handle it naturally
        // Don't call handleFunctions() - it's empty and ExpressionParser already handles user functions
        // This allows expressions like f(4) to work correctly
        
        // Evaluate the expression - parser will handle user functions
        return evaluateExpression(expression);
    }

    /**
     * Handle basic math functions in the expression
     * @param expr The expression string
     * @return The modified expression string ({@link String})
     */
    private String handleFunctions(String expr) {
        // This is a placeholder for function handling
        // You'll expand this to handle sin, cos, tan, log, etc.
        return expr;
    }
    
    /**
     * Evaluate the mathematical expression
     * @param expr The expression string
     * @return The evaluated result ({@code double})
     * @throws Exception If the expression is invalid
     */
    private double evaluateExpression(String expr) throws Exception {
        // Pass both userFunctions and parameters to the parser
        return new ExpressionParser(userFunctions, parameters).parse(expr);
    }
}