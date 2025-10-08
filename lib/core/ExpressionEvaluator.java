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
        // Simple expression evaluator (you'll need to expand this)
        // For now, using a basic approach
        return new ExpressionParser(userFunctions).parse(expr);
    }
}