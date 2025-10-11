package lib.expression;

public class ExpressionEvaluator {
    
    /**
    * Evaluate the mathematical expression for a given value of {@code x}
    * @param expression The function expression as a string
    * @param x The value to substitute for {@code x} in the expression
    * @return The evaluated result ({@code double})
    * @throws Exception If the expression is invalid
    */
    public static double evaluate(String expression, double x) throws Exception {

        expression = expression.toLowerCase().trim();
        // Wrap the value in parentheses to handle negative numbers correctly
        // e.g., x^2 with x=-3 becomes (-3)^2 instead of -3^2
        expression = expression.replace("x", "(" + x + ")");
        
        // Evaluate the expression
        return new ExpressionParser().parse(expression);
    }
}