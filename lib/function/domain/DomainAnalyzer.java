package lib.function.domain;

import lib.function.domain.domains.IntervalDomain;
import lib.parser.expression.ExpressionNode;
import lib.parser.expression.ExpressionNode.BinaryOpNode;
import lib.parser.expression.ExpressionNode.FunctionNode;
import lib.parser.expression.ExpressionNode.NumberNode;
import lib.parser.expression.ExpressionNode.ParameterizedFunctionNode;
import lib.parser.expression.ExpressionNode.UnaryOpNode;

/**
 * Analyzes an expression AST to determine its mathematical domain.
 * Identifies restrictions like logarithm arguments must be positive,
 * square roots must be non-negative, etc.
 */
public class DomainAnalyzer {
    
    /**
     * Analyze an expression node to determine its domain
     * @param node The root node of the expression AST
     * @return The domain of the expression
     */
    public static IntervalDomain analyzeDomain(ExpressionNode node) {

        try {

            DomainRestrictions restrictions = analyzeRestrictions(node);
            return restrictions.toDomain();
        } catch (Exception e) {

            // If analysis fails, assume unrestricted domain
            return new IntervalDomain(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
    }
    
    /**
     * Recursively analyze restrictions in the expression tree
     * @param node The node to analyze
     * @return Domain restrictions found in this subtree
     */
    private static DomainRestrictions analyzeRestrictions(ExpressionNode node) {

        DomainRestrictions restrictions = new DomainRestrictions();
        
        if (node instanceof NumberNode) return restrictions;
        
        if (node instanceof BinaryOpNode) {

            BinaryOpNode binOp = (BinaryOpNode) node;

            // Analyze both operands
            restrictions.merge(analyzeRestrictions(binOp.getLeft()));
            restrictions.merge(analyzeRestrictions(binOp.getRight()));
            
            // Division: check for denominator = 0
            // Note: This is complex for general case, skip for now
            
            return restrictions;
        }

        if (node instanceof UnaryOpNode) {

            UnaryOpNode unaryOp = (UnaryOpNode) node;
            return analyzeRestrictions(unaryOp.getOperand());
        }

        if (node instanceof FunctionNode) {

            FunctionNode funcNode = (FunctionNode) node;
            String funcName = funcNode.getFunctionName();
            
            // Analyze the argument first
            restrictions.merge(analyzeRestrictions(funcNode.getArgument()));
            
            // Add function-specific restrictions
            switch (funcName) {
                case "ln":
                    // ln(x) requires x > 0
                    restrictions.requirePositive();
                    break;
                    
                case "sqrt":
                    // sqrt(x) requires x >= 0
                    restrictions.requireNonNegative();
                    break;
                    
                case "asin":
                case "acos":
                    // asin(x), acos(x) require -1 <= x <= 1
                    restrictions.requireRange(-1.0, 1.0);
                    break;
                    
                case "acot":
                    // acot(x) requires x != 0
                    restrictions.excludePoint(0.0);
                    break;
                    
                case "asec":
                case "acsc":
                    // asec(x), acsc(x) require |x| >= 1
                    restrictions.requireAbsGreaterOrEqual(1.0);
                    break;
                    
                case "acosh":
                    // acosh(x) requires x >= 1
                    restrictions.requireGreaterOrEqual(1.0);
                    break;
                    
                case "atanh":
                    // atanh(x) requires -1 < x < 1
                    restrictions.requireRange(-1.0, 1.0);
                    break;
                    
                case "acoth":
                    // acoth(x) requires |x| > 1
                    restrictions.requireAbsGreater(1.0);
                    break;
                    
                case "asech":
                    // asech(x) requires 0 < x <= 1
                    restrictions.requireRange(0.0, 1.0);
                    break;
                    
                case "acsch":
                    // acsch(x) requires x != 0
                    restrictions.excludePoint(0.0);
                    break;
            }
            
            return restrictions;
        }

        if (node instanceof ParameterizedFunctionNode) {

            ParameterizedFunctionNode paramFunc = (ParameterizedFunctionNode) node;
            String funcName = paramFunc.getFunctionName();
            
            // Analyze the argument first
            restrictions.merge(analyzeRestrictions(paramFunc.getArgument()));
            
            // Add function-specific restrictions
            switch (funcName) {
                case "log":
                    // log{base}(x) requires x > 0
                    restrictions.requirePositive();
                    break;
                    
                case "root":
                    // root{n}(x) requires x >= 0 if n is even
                    double n = paramFunc.getParameter();
                    if (n % 2 == 0) {
                        restrictions.requireNonNegative();
                    }
                    break;
            }
            
            return restrictions;
        }
        
        return restrictions;
    }
    
    /**
     * Internal class to track domain restrictions
     */
    private static class DomainRestrictions {

        private double minBound = Double.NEGATIVE_INFINITY;
        private double maxBound = Double.POSITIVE_INFINITY;
        private boolean excludeNegativeInfinity = false;
        private boolean excludePositiveInfinity = false;
        
        public void requirePositive() {

            minBound = Math.max(minBound, 0.0);
            excludeNegativeInfinity = true;
        }
        
        public void requireNonNegative() {

            minBound = Math.max(minBound, 0.0);
        }
        
        public void requireGreaterOrEqual(double value) {

            minBound = Math.max(minBound, value);
        }
        
        public void requireRange(double min, double max) {

            minBound = Math.max(minBound, min);
            maxBound = Math.min(maxBound, max);
        }
        
        public void excludePoint(double point) {
            // For now, we can't represent discrete exclusions in IntervalDomain
            // This would require a more complex domain representation
        }
        
        public void requireAbsGreaterOrEqual(double value) {

            // |x| >= value means x <= -value OR x >= value
            // For simplicity, we'll use the more permissive x >= value
            minBound = Math.max(minBound, value);
        }
        
        public void requireAbsGreater(double value) {

            // |x| > value means x < -value OR x > value
            // For simplicity, we'll use x >= value
            minBound = Math.max(minBound, value);
            excludeNegativeInfinity = true;
        }
        
        public void merge(DomainRestrictions other) {

            minBound = Math.max(minBound, other.minBound);
            maxBound = Math.min(maxBound, other.maxBound);
            excludeNegativeInfinity = excludeNegativeInfinity || other.excludeNegativeInfinity;
            excludePositiveInfinity = excludePositiveInfinity || other.excludePositiveInfinity;
        }
        
        public IntervalDomain toDomain() {

            // If restrictions are inconsistent, return empty/unrestricted domain
            if (minBound > maxBound) return new IntervalDomain(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            
            // Apply exclusions (convert open intervals to slightly shifted closed ones)
            double effectiveMin = excludeNegativeInfinity && !Double.isInfinite(minBound) 
                ? minBound + 1e-10 : minBound;
            double effectiveMax = excludePositiveInfinity && !Double.isInfinite(maxBound) 
                ? maxBound - 1e-10 : maxBound;
            
            return new IntervalDomain(effectiveMin, effectiveMax);
        }
    }
}
