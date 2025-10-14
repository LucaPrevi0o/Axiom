package lib.parser.expression;
/**
 * A mathematical expression parser and evaluator.
 * 
 * Uses ExpressionTokenizer for string parsing and focuses on numerical evaluation.
 * Supports +, -, *, /, ^, parentheses, and basic functions like sin, cos, etc.
 * Also supports parameterized functions: root{N}(x) for N-th root, log{N}(x) for base-N logarithm.
 */
public class ExpressionParser {

    private ExpressionTokenizer tokenizer;
    
    /**
     * Parse and evaluate the mathematical expression
     * @param str The expression string
     * @return The evaluated result ({@code double})
     * @throws Exception If the expression is invalid
     */
    public double parse(String str) throws Exception {

        tokenizer = new ExpressionTokenizer();
        tokenizer.init(str);
        
        double result = parseExpression();
        
        if (tokenizer.getPosition() < tokenizer.getLength())
            throw new Exception("Unexpected: " + (char) tokenizer.getCurrentChar());
        
        return result;
    }
    
    /**
     * Parse the expression
     * @return The parsed result ({@code double})
     * @throws Exception If the expression is invalid
     */
    private double parseExpression() throws Exception {

        double x = parseTerm();
        while (true) {

            if (tokenizer.eat('+')) x += parseTerm();
            else if (tokenizer.eat('-')) x -= parseTerm();
            else return x;
        }
    }
    
    /**
     * Parse a term in the expression.
     * A term is a factor possibly followed by * or / and another term.
     * @return The parsed result ({@code double})
     * @throws Exception If the expression is invalid
     */
    private double parseTerm() throws Exception {

        double x = parseFactor();
        while (true) {

            if (tokenizer.eat('*')) x *= parseFactor();
            else if (tokenizer.eat('/')) x /= parseFactor();
            else return x;
        }
    }
    
    /**
     * Parse a factor in the expression.
     * A factor can be a number, a parenthesized expression, a function call, or a unary +/-.
     * @return The parsed result ({@code double})
     * @throws Exception If the expression is invalid
     */
    private double parseFactor() throws Exception {

        if (tokenizer.eat('+')) return parseFactor();
        if (tokenizer.eat('-')) return -parseFactor();
        
        double x;
        
        
        if (tokenizer.eat('(')) {

            x = parseExpression();
            tokenizer.eat(')');
        } else if (tokenizer.isNumberChar()) {

            String numStr = tokenizer.readNumber();
            x = Double.parseDouble(numStr);
        } else if (tokenizer.isLetterChar()) {

            String func = tokenizer.readFunctionName();
            
            // Check if it's a constant (pi or e)
            if (func.equals("pi")) x = Math.PI;
            else if (func.equals("e")) x = Math.E;
            else {
                
                // It's a function - check for parameters
                String paramStr = tokenizer.readParameter();
                
                x = parseFactor();
                x = paramStr != null ? applyFunction(func, x, Double.parseDouble(paramStr)) : applyFunction(func, x);
            }
        } else throw new Exception("Unexpected: " + (char) tokenizer.getCurrentChar());

        if (tokenizer.eat('^'))  x = Math.pow(x, parseFactor());
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

            // Basic functions
            case "sin": return Math.sin(x);
            case "cos": return Math.cos(x);
            case "tan": return Math.tan(x);
            case "cot": return 1.0 / Math.tan(x);

            // Reciprocal functions
            case "sec": return 1.0 / Math.cos(x);
            case "csc": return 1.0 / Math.sin(x);

            // Inverse functions
            case "asin": return Math.asin(x);
            case "acos": return Math.acos(x);
            case "atan": return Math.atan(x);
            case "acot": return Math.atan(1.0 / x);

            // Inverse reciprocal functions
            case "asec": return Math.acos(1.0 / x);
            case "acsc": return Math.asin(1.0 / x);

            // Hyperbolic functions
            case "sinh": return Math.sinh(x);
            case "cosh": return Math.cosh(x);
            case "tanh": return Math.tanh(x);
            case "coth": return 1.0 / Math.tanh(x);

            // Hyperbolic reciprocal functions
            case "sech": return 1.0 / Math.cosh(x);
            case "csch": return 1.0 / Math.sinh(x);

            // Inverse hyperbolic functions
            case "asinh": return Math.log(x + Math.sqrt(x * x + 1));
            case "acosh": return Math.log(x + Math.sqrt(x * x - 1));
            case "atanh": return 0.5 * Math.log((1 + x) / (1 - x));
            case "acoth": return 0.5 * Math.log((x + 1) / (x - 1));

            // Inverse hyperbolic reciprocal functions
            case "asech": return Math.log(Math.sqrt(1 / (x * x) - 1) + 1 / x);
            case "acsch": return Math.log(1 / x + Math.sqrt(1 / (x * x) + 1));

            case "ln": return Math.log(x);
            case "abs": return Math.abs(x);
            default: throw new Exception("Unknown function: " + func);
        }
    }

    /**
     * Apply a parameterized mathematical function to a value
     * @param func The function name as a string
     * @param x The value to apply the function to
     * @param param The parameter for the function (e.g., base for log, root index)
     * @return The result of the function application ({@code double})
     * @throws Exception If the function is unknown
     */
    private double applyFunction(String func, double x, double param) throws Exception {

        switch (func) {
            
            case "log": return Math.log(x) / Math.log(param);
            case "root": return Math.pow(x, 1.0 / param);
            default: throw new Exception("Unknown function: " + func);
        }
    }
}