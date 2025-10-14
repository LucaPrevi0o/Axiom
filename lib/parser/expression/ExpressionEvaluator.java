package lib.parser.expression;

import java.util.Map;

public class ExpressionEvaluator {
    
    /**
    * Evaluate the mathematical expression for a given value of {@code x}
    * @param expression The function expression as a string
    * @param x The value to substitute for {@code x} in the expression
    * @return The evaluated result ({@code double})
    * @throws Exception If the expression is invalid
    */
    public static double evaluate(String expression, double x) throws Exception {
        return evaluate(expression, x, null);
    }
    
    /**
    * Evaluate the mathematical expression for a given value of {@code x} with constants
    * @param expression The function expression as a string
    * @param x The value to substitute for {@code x} in the expression
    * @param constants Map of constant names to their values (can be null)
    * @return The evaluated result ({@code double})
    * @throws Exception If the expression is invalid
    */
    public static double evaluate(String expression, double x, Map<String, Double> constants) throws Exception {

        expression = expression.toLowerCase().trim();
        
        // Substitute constant values first (before substituting x)
        if (constants != null) {
            for (Map.Entry<String, Double> entry : constants.entrySet()) {
                String constantName = entry.getKey().toLowerCase();
                Double constantValue = entry.getValue();
                
                // Use word boundaries to avoid replacing parts of other identifiers
                // For example, "q" should match "q" but not the "q" in "sqrt"
                expression = expression.replaceAll("\\b" + constantName + "\\b", "(" + constantValue + ")");
            }
        }
        
        expression = expression.replace("x", "(" + x + ")");
        
        // Evaluate the expression
        return new ExpressionParser().parse(expression);
    }
}