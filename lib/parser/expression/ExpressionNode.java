package lib.parser.expression;

/**
 * Represents a node in the Abstract Syntax Tree (AST) of a mathematical expression.
 * Each node represents an operation, value, or function in the expression.
 */
public abstract class ExpressionNode {
    
    /**
     * Evaluate this node to produce a numerical result
     * @return The evaluated result as a double
     * @throws Exception If evaluation fails
     */
    public abstract double evaluate() throws Exception;
    
    /**
     * Represents a constant numeric value
     */
    public static class NumberNode extends ExpressionNode {

        private final double value;

        public NumberNode(double value) { this.value = value; }

        @Override
        public double evaluate() { return value; }
    }
    
    /**
     * Represents a binary operation (e.g., +, -, *, /, ^)
     */
    public static class BinaryOpNode extends ExpressionNode {

        private final ExpressionNode left;
        private final ExpressionNode right;
        private final char operator;
        
        public BinaryOpNode(ExpressionNode left, char operator, ExpressionNode right) {

            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        
        public ExpressionNode getLeft() { return left; }
        public ExpressionNode getRight() { return right; }
        public char getOperator() { return operator; }
        
        @Override
        public double evaluate() throws Exception {

            double leftVal = left.evaluate();
            double rightVal = right.evaluate();
            
            switch (operator) {

                case '+': return leftVal + rightVal;
                case '-': return leftVal - rightVal;
                case '*': return leftVal * rightVal;
                case '/': return leftVal / rightVal;
                case '^': return Math.pow(leftVal, rightVal);
                default: throw new Exception("Unknown operator: " + operator);
            }
        }
    }
    
    /**
     * Represents a unary operation (e.g., unary minus)
     */
    public static class UnaryOpNode extends ExpressionNode {

        private final ExpressionNode operand;
        private final char operator;
        
        public UnaryOpNode(char operator, ExpressionNode operand) {

            this.operator = operator;
            this.operand = operand;
        }
        
        public ExpressionNode getOperand() { return operand; }
        public char getOperator() { return operator; }
        
        @Override
        public double evaluate() throws Exception {

            double val = operand.evaluate();
            
            switch (operator) {

                case '-': return -val;
                case '+': return val;
                default: throw new Exception("Unknown unary operator: " + operator);
            }
        }
    }
    
    /**
     * Represents a function call (e.g., sin, cos, ln)
     */
    public static class FunctionNode extends ExpressionNode {

        private final String functionName;
        private final ExpressionNode argument;
        
        public FunctionNode(String functionName, ExpressionNode argument) {

            this.functionName = functionName;
            this.argument = argument;
        }
        
        public String getFunctionName() { return functionName; }
        public ExpressionNode getArgument() { return argument; }
        
        @Override
        public double evaluate() throws Exception {

            double x = argument.evaluate();
            
            switch (functionName) {

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
                default: throw new Exception("Unknown function: " + functionName);
            }
        }
    }
    
    /**
     * Represents a parameterized function call (e.g., log{10}, root{3})
     */
    public static class ParameterizedFunctionNode extends ExpressionNode {

        private final String functionName;
        private final ExpressionNode argument;
        private final double parameter;
        
        public ParameterizedFunctionNode(String functionName, ExpressionNode argument, double parameter) {

            this.functionName = functionName;
            this.argument = argument;
            this.parameter = parameter;
        }
        
        public String getFunctionName() { return functionName; }
        public ExpressionNode getArgument() { return argument; }
        public double getParameter() { return parameter; }
        
        @Override
        public double evaluate() throws Exception {

            double x = argument.evaluate();
            
            switch (functionName) {

                case "log": return Math.log(x) / Math.log(parameter);
                case "root": return Math.pow(x, 1.0 / parameter);
                default: throw new Exception("Unknown parameterized function: " + functionName);
            }
        }
    }
}
