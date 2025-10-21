package lib.function.domain;

import lib.function.domain.domains.IntervalDomain;
import lib.parser.expression.ExpressionNode;
import lib.parser.expression.ExpressionNode.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes an expression AST to determine its mathematical domain.
 * Identifies restrictions like logarithm arguments must be positive,
 * square roots must be non-negative, division by zero, etc.
 */
public class DomainAnalyzer {
    
    /**
     * Analyze an expression node to determine its domain
     * @param node The root node of the expression AST
     * @return The domain of the expression (may be ComposedDomain for complex cases)
     */
    public static Domain analyzeDomain(ExpressionNode node) {
        try {
            List<DomainConstraint> constraints = new ArrayList<>();
            collectConstraints(node, constraints);
            
            if (constraints.isEmpty()) {
                // No restrictions - entire real line
                return new IntervalDomain(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            }
            
            // Merge all constraints into a single domain
            return mergeConstraints(constraints);
        } catch (Exception e) {
            // If analysis fails, return unrestricted domain
            System.err.println("Domain analysis failed: " + e.getMessage());
            return new IntervalDomain(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
    }
    
    /**
     * Recursively collect all domain constraints from the expression tree
     * @param node The node to analyze
     * @param constraints List to accumulate constraints
     */
    private static void collectConstraints(ExpressionNode node, List<DomainConstraint> constraints) {
        if (node instanceof NumberNode) {
            // Constants have no restrictions
            return;
        }
        
        if (node instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) node;
            
            // Analyze both operands
            collectConstraints(binOp.getLeft(), constraints);
            collectConstraints(binOp.getRight(), constraints);
            
            // Division: denominator cannot be zero
            // This is complex for general case - would need to solve right = 0
            // For now, we skip this (would require symbolic solver)
            
            return;
        }
        
        if (node instanceof UnaryOpNode) {
            UnaryOpNode unaryOp = (UnaryOpNode) node;
            collectConstraints(unaryOp.getOperand(), constraints);
            return;
        }
        
        if (node instanceof FunctionNode) {
            FunctionNode funcNode = (FunctionNode) node;
            String funcName = funcNode.getFunctionName();
            
            // First collect constraints from the argument
            collectConstraints(funcNode.getArgument(), constraints);
            
            // Then add function-specific constraints
            // Note: These assume the argument is just 'x'
            // For complex arguments, we'd need more sophisticated analysis
            
            switch (funcName) {
                case "ln":
                    // ln(x) requires x > 0
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.GREATER_THAN,
                        0.0,
                        false // not inclusive
                    ));
                    break;
                    
                case "sqrt":
                    // sqrt(x) requires x >= 0
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.GREATER_THAN,
                        0.0,
                        true // inclusive
                    ));
                    break;
                    
                case "asin":
                case "acos":
                    // asin(x), acos(x) require -1 <= x <= 1
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.GREATER_THAN,
                        -1.0,
                        true
                    ));
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.LESS_THAN,
                        1.0,
                        true
                    ));
                    break;
                    
                case "acot":
                case "acsch":
                    // acot(x), acsch(x) require x != 0
                    // This creates two intervals: (-inf, 0) U (0, inf)
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.NOT_EQUAL,
                        0.0,
                        false
                    ));
                    break;
                    
                case "asec":
                case "acsc":
                    // asec(x), acsc(x) require |x| >= 1
                    // This creates two intervals: (-inf, -1] U [1, inf)
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.ABS_GREATER_OR_EQUAL,
                        1.0,
                        true
                    ));
                    break;
                    
                case "acosh":
                    // acosh(x) requires x >= 1
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.GREATER_THAN,
                        1.0,
                        true
                    ));
                    break;
                    
                case "atanh":
                    // atanh(x) requires -1 < x < 1
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.GREATER_THAN,
                        -1.0,
                        false
                    ));
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.LESS_THAN,
                        1.0,
                        false
                    ));
                    break;
                    
                case "acoth":
                    // acoth(x) requires |x| > 1
                    // This creates two intervals: (-inf, -1) U (1, inf)
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.ABS_GREATER_OR_EQUAL,
                        1.0,
                        false
                    ));
                    break;
                    
                case "asech":
                    // asech(x) requires 0 < x <= 1
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.GREATER_THAN,
                        0.0,
                        false
                    ));
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.LESS_THAN,
                        1.0,
                        true
                    ));
                    break;
            }
            
            return;
        }
        
        if (node instanceof ParameterizedFunctionNode) {
            ParameterizedFunctionNode paramFunc = (ParameterizedFunctionNode) node;
            String funcName = paramFunc.getFunctionName();
            
            // Analyze the argument first
            collectConstraints(paramFunc.getArgument(), constraints);
            
            // Add function-specific restrictions
            switch (funcName) {
                case "log":
                    // log{base}(x) requires x > 0
                    constraints.add(new DomainConstraint(
                        DomainConstraint.Type.GREATER_THAN,
                        0.0,
                        false
                    ));
                    break;
                    
                case "root":
                    // root{n}(x) requires x >= 0 if n is even
                    double n = paramFunc.getParameter();
                    if (n % 2 == 0) {
                        constraints.add(new DomainConstraint(
                            DomainConstraint.Type.GREATER_THAN,
                            0.0,
                            true
                        ));
                    }
                    break;
            }
        }
    }
    
    /**
     * Merge a list of constraints into a single Domain
     * @param constraints List of constraints to merge
     * @return The resulting Domain (may be ComposedDomain)
     */
    private static Domain mergeConstraints(List<DomainConstraint> constraints) {
        // Start with entire real line
        double minBound = Double.NEGATIVE_INFINITY;
        double maxBound = Double.POSITIVE_INFINITY;
        boolean minInclusive = true;
        boolean maxInclusive = true;
        
        List<Domain> specialDomains = new ArrayList<>();
        
        for (DomainConstraint constraint : constraints) {
            switch (constraint.type) {
                case GREATER_THAN:
                    minBound = Math.max(minBound, constraint.value);
                    if (!constraint.inclusive) {
                        minInclusive = false;
                    }
                    break;
                    
                case LESS_THAN:
                    maxBound = Math.min(maxBound, constraint.value);
                    if (!constraint.inclusive) {
                        maxInclusive = false;
                    }
                    break;
                    
                case NOT_EQUAL:
                    // Creates two intervals: (-inf, value) U (value, inf)
                    if (minBound < constraint.value && constraint.value < maxBound) {
                        specialDomains.add(new IntervalDomain(minBound, constraint.value));
                        specialDomains.add(new IntervalDomain(constraint.value, maxBound));
                        // Return composed domain immediately
                        return new ComposedDomain(specialDomains.toArray(new Domain[0]));
                    }
                    break;
                    
                case ABS_GREATER_OR_EQUAL:
                    // |x| >= value means x <= -value OR x >= value
                    // Creates: (-inf, -value] U [value, inf)
                    Domain left = new IntervalDomain(Double.NEGATIVE_INFINITY, -constraint.value);
                    Domain right = new IntervalDomain(constraint.value, Double.POSITIVE_INFINITY);
                    return new ComposedDomain(left, right);
            }
        }
        
        // If we have special domains, return ComposedDomain
        if (!specialDomains.isEmpty()) {
            return new ComposedDomain(specialDomains.toArray(new Domain[0]));
        }
        
        // Apply epsilon shifts for open intervals
        if (!minInclusive && !Double.isInfinite(minBound)) {
            minBound += 1e-10;
        }
        if (!maxInclusive && !Double.isInfinite(maxBound)) {
            maxBound -= 1e-10;
        }
        
        // Check for empty domain
        if (minBound > maxBound) {
            // Return empty interval
            return new IntervalDomain(0, 0); // Empty domain
        }
        
        return new IntervalDomain(minBound, maxBound);
    }
    
    /**
     * Internal class representing a domain constraint
     */
    private static class DomainConstraint {
        enum Type {
            GREATER_THAN,        // x > value or x >= value
            LESS_THAN,           // x < value or x <= value
            NOT_EQUAL,           // x != value
            ABS_GREATER_OR_EQUAL // |x| >= value
        }
        
        final Type type;
        final double value;
        final boolean inclusive;
        
        DomainConstraint(Type type, double value, boolean inclusive) {
            this.type = type;
            this.value = value;
            this.inclusive = inclusive;
        }
    }
}