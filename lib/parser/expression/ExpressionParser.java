package lib.parser.expression;
/**
 * A mathematical expression parser that builds an Abstract Syntax Tree (AST).
 * 
 * Uses ExpressionTokenizer for string parsing and creates an AST representation.
 * Supports +, -, *, /, ^, parentheses, and basic functions like sin, cos, etc.
 * Also supports parameterized functions: root{N}(x) for N-th root, log{N}(x) for base-N logarithm.
 */
public class ExpressionParser {

    private ExpressionTokenizer tokenizer;
    
    /**
     * Parse the mathematical expression and return its AST representation
     * @param str The expression string
     * @return The root node of the AST
     * @throws Exception If the expression is invalid
     */
    public ExpressionNode parse(String str) throws Exception {

        tokenizer = new ExpressionTokenizer();
        tokenizer.init(str);
        
        ExpressionNode result = parseExpression();
        
        if (tokenizer.getPosition() < tokenizer.getLength())
            throw new Exception("Unexpected: " + (char) tokenizer.getCurrentChar());
        
        return result;
    }
    
    /**
     * Parse the expression
     * @return The AST node representing the expression
     * @throws Exception If the expression is invalid
     */
    private ExpressionNode parseExpression() throws Exception {

        ExpressionNode node = parseTerm();
        while (true)
            if (tokenizer.eat('+')) node = new ExpressionNode.BinaryOpNode(node, '+', parseTerm());
            else if (tokenizer.eat('-')) node = new ExpressionNode.BinaryOpNode(node, '-', parseTerm());
            else return node;
    }
    
    /**
     * Parse a term in the expression.
     * A term is a factor possibly followed by * or / and another term.
     * @return The AST node representing the term
     * @throws Exception If the expression is invalid
     */
    private ExpressionNode parseTerm() throws Exception {

        ExpressionNode node = parseFactor();
        while (true)
            if (tokenizer.eat('*')) node = new ExpressionNode.BinaryOpNode(node, '*', parseFactor());
            else if (tokenizer.eat('/')) node = new ExpressionNode.BinaryOpNode(node, '/', parseFactor());
            else return node;
    }
    
    /**
     * Parse a factor in the expression.
     * A factor can be a number, a parenthesized expression, a function call, or a unary +/-.
     * @return The AST node representing the factor
     * @throws Exception If the expression is invalid
     */
    private ExpressionNode parseFactor() throws Exception {

        if (tokenizer.eat('+')) return new ExpressionNode.UnaryOpNode('+', parseFactor());
        if (tokenizer.eat('-')) return new ExpressionNode.UnaryOpNode('-', parseFactor());
        
        ExpressionNode node;
        
        
        if (tokenizer.eat('(')) {

            node = parseExpression();
            tokenizer.eat(')');
        } else if (tokenizer.isNumberChar()) {

            String numStr = tokenizer.readNumber();
            node = new ExpressionNode.NumberNode(Double.parseDouble(numStr));
        } else if (tokenizer.isLetterChar()) {

            String func = tokenizer.readFunctionName();
            
            // Check if it's a constant (pi or e)
            if (func.equals("pi")) node = new ExpressionNode.NumberNode(Math.PI);
            else if (func.equals("e")) node = new ExpressionNode.NumberNode(Math.E);
            else {

                // It's a function - check for parameters
                String paramStr = tokenizer.readParameter();
                
                ExpressionNode argument = parseFactor();
                
                if (paramStr != null) node = new ExpressionNode.ParameterizedFunctionNode(func, argument, Double.parseDouble(paramStr));
                else node = new ExpressionNode.FunctionNode(func, argument);
            }
        } else throw new Exception("Unexpected: " + (char) tokenizer.getCurrentChar());

        if (tokenizer.eat('^')) node = new ExpressionNode.BinaryOpNode(node, '^', parseFactor());
        return node;
    }
}