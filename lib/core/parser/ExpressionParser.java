package lib.core.parser;

import lib.core.evaluation.ExpressionEvaluator;
import java.util.Map;

public class ExpressionParser {

    private int pos = -1;
    private int ch;
    private String str;
    private Map<String, String> userFunctions;
    private Map<String, Double> parameters;

    public ExpressionParser() { this(null, null); }

    public ExpressionParser(Map<String, String> userFunctions) {
        this(userFunctions, null);
    }
    
    public ExpressionParser(Map<String, String> userFunctions, Map<String, Double> parameters) {
        this.userFunctions = userFunctions;
        this.parameters = parameters;
    }
    
    /**
     * Parse and evaluate the mathematical expression
     * @param str The expression string
     * @return The evaluated result ({@code double})
     * @throws Exception If the expression is invalid
     */
    public double parse(String str) throws Exception {
        this.str = str;
        this.pos = -1;
        nextChar();
        double result = parseExpression();
        if (pos < str.length()) throw new Exception("Unexpected: " + (char) ch);
        return result;
    }
    
    /**
     * Advance to the next character in the expression
     */
    private void nextChar() {
        ch = (++pos < str.length()) ? str.charAt(pos) : -1;
    }
    
    /**
     * Eat the current character if it matches the expected character
     * @param charToEat The expected character to eat
     * @return {@code true} if the character was eaten, {@code false} otherwise
     */
    private boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    /**
     * Parse the expression
     * @return The parsed result ({@code double})
     * @throws Exception If the expression is invalid
     */
    private double parseExpression() throws Exception {
        double x = parseTerm();
        while (true) {
            if (eat('+')) x += parseTerm();
            else if (eat('-')) x -= parseTerm();
            else return x;
        }
    }
    
    /**
     * Parse a term in the expression
     * @return The parsed result ({@code double})
     * @throws Exception If the expression is invalid
     */
    private double parseTerm() throws Exception {
        double x = parseFactor();
        while (true) {
            if (eat('*')) x *= parseFactor();
            else if (eat('/')) x /= parseFactor();
            else return x;
        }
    }
    
    /**
     * Parse a factor in the expression
     * @return The parsed result ({@code double})
     * @throws Exception If the expression is invalid
     */
    private double parseFactor() throws Exception {
        if (eat('+')) return parseFactor();
        if (eat('-')) return -parseFactor();
        
        double x;
        int startPos = this.pos;
        
        if (eat('(')) {
            x = parseExpression();
            eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') {
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = Double.parseDouble(str.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') {
            while (ch >= 'a' && ch <= 'z') nextChar();
            String func = str.substring(startPos, this.pos);
            // Support constants like pi and e
            if (func.equals("pi")) {
                x = Math.PI;
            } else if (func.equals("e")) {
                x = Math.E;
            } else {
                // function application: func followed by factor (e.g., sin x or sin(x))
                x = parseFactor();
                // If this is a user-defined function, evaluate its expression with the provided argument
                if (userFunctions != null && userFunctions.containsKey(func)) {
                    String funcExpr = userFunctions.get(func);
                    // Evaluate the function expression using the same userFunctions map and parameters
                    x = new ExpressionEvaluator(userFunctions, parameters).evaluate(funcExpr, x);
                } else {
                    x = applyFunction(func, x);
                }
            }
        } else {
            throw new Exception("Unexpected: " + (char) ch);
        }
        
        if (eat('^')) x = Math.pow(x, parseFactor());
        
        return x;
    }
    
    /**
     * Apply a mathematical function to a value
     * @param func The function name as a string
     * @param x The value to apply the function to
     * @return The result of the function application ({@code double})
     * @throws Exception If the function is unknown
     */
    private double applyFunction(String func, double x) throws Exception {
        switch (func) {
            case "sqrt": return Math.sqrt(x);
            case "sin": return Math.sin(x);
            case "cos": return Math.cos(x);
            case "tan": return Math.tan(x);
            case "log": return Math.log10(x);
            case "ln": return Math.log(x);
            case "abs": return Math.abs(x);
            default: throw new Exception("Unknown function: " + func);
        }
    }
}