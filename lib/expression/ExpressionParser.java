package lib.expression;
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
     * Parse a term in the expression
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
     * Parse a factor in the expression
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
            
            // Check for parameterized functions like root{N} or log{N}
            String paramStr = tokenizer.readParameter();
            double param = (paramStr != null) ? Double.parseDouble(paramStr) : -1;
            
            x = parseFactor();
            x = applyFunction(func, x, param);
        } else throw new Exception("Unexpected: " + (char) tokenizer.getCurrentChar());

        if (tokenizer.eat('^'))  x = Math.pow(x, parseFactor());
        return x;
    }
    
    /**
     * Apply a mathematical function to a value
     * @param func The function name as a string
     * @param x The value to apply the function to
     * @param param Optional parameter for parameterized functions (e.g., base for log, root index)
     * @return The result of the function application ({@code double})
     * @throws Exception If the function is unknown
     */
    private double applyFunction(String func, double x, double param) throws Exception {

        switch (func) {
            case "sin": return Math.sin(x);
            case "cos": return Math.cos(x);
            case "tan": return Math.tan(x);
            case "log": 
                if (param > 0) return Math.log(x) / Math.log(param);
                return Math.log10(x);
            case "ln": return Math.log(x);
            case "abs": return Math.abs(x);
            case "root":
                if (param > 0) return Math.pow(x, 1.0 / param);
                else throw new Exception("root requires a parameter: root{n}(x)");
            default: throw new Exception("Unknown function: " + func);
        }
    }
}