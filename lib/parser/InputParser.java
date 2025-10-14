package lib.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for user input that determines the type of mathematical entity to create
 * Supports parsing of:
 * - Function definitions (e.g., "f(x)=x^2")
 * - Expressions (e.g., "x^2")
 * - Constants (future implementation)
 * - Number sets (future implementation)
 */
public class InputParser {

    /**
     * Represents the result of parsing user input
     */
    public static class ParseResult {

        private final InputType type;
        private final String name;
        private final String expression;
        
        public ParseResult(InputType type, String name, String expression) {

            this.type = type;
            this.name = name;
            this.expression = expression;
        }
        
        public InputType getType() { return type; }
        public String getName() { return name; }
        public String getExpression() { return expression; }
        
        public boolean hasName() { return name != null; }
        
        /**
         * Extract the start value from a range expression
         * @return The start value, or null if not a range
         */
        public Double getRangeStart() {
            if (type != InputType.RANGE) return null;
            String[] parts = expression.replace("[", "").replace("]", "").split(":");
            return Double.parseDouble(parts[0].trim());
        }
        
        /**
         * Extract the end value from a range expression
         * @return The end value, or null if not a range
         */
        public Double getRangeEnd() {
            if (type != InputType.RANGE) return null;
            String[] parts = expression.replace("[", "").replace("]", "").split(":");
            return Double.parseDouble(parts[1].trim());
        }
        
        /**
         * Extract the numbers from a number set expression
         * @return Array of numbers, or null if not a number set
         */
        public Double[] getNumberSetValues() {
            if (type != InputType.NUMBER_SET) return null;
            String content = expression.replace("{", "").replace("}", "").trim();
            if (content.isEmpty()) return new Double[0];
            
            String[] parts = content.split(",");
            Double[] values = new Double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                values[i] = Double.parseDouble(parts[i].trim());
            }
            return values;
        }
    }
    
    /**
     * Enum representing the type of input parsed
     */
    public enum InputType {
        EXPRESSION,         // Expression (e.g., "x^2", "sin(x)+2*x")
        RANGE,              // Range definition (e.g., "c=[1:5]")
        NUMBER_SET          // Number set definition (e.g., "s={1,2,3}")
    }
    
    // Regex patterns
    // Named function: "f(x)=x^2"
    private static final Pattern NAMED_EXPRESSION_PATTERN = 
        Pattern.compile("^([a-zA-Z]\\w*)\\s*\\(\\s*x\\s*\\)\\s*=\\s*(.+)$");

    // Unnamed function: any expression containing 'x' (e.g., "x^2", "sin(x)", "2*x+1")
    private static final Pattern UNNAMED_EXPRESSION_PATTERN = 
        Pattern.compile("^(.+)$");
    
    // Named range: "c=[1:5]", "range=[-2.5:10.3]", "r = [ -10 : 20 ]"
    private static final Pattern NAMED_RANGE_PATTERN = 
        Pattern.compile("^([a-zA-Z]\\w*)\\s*=\\s*\\[\\s*(-?\\d+(?:\\.\\d+)?)\\s*:\\s*(-?\\d+(?:\\.\\d+)?)\\s*\\]$");
    
    // Unnamed range: just "[1:5]" or "[-2.5:10.3]"
    private static final Pattern UNNAMED_RANGE_PATTERN = 
        Pattern.compile("^\\[\\s*(-?\\d+(?:\\.\\d+)?)\\s*:\\s*(-?\\d+(?:\\.\\d+)?)\\s*\\]$");
    
    // Named number set: "s={1,2,3}", "set={-2.5, 10, 3.14}", "nums = { 1 , 2 , 3 }"
    private static final Pattern NAMED_NUMBER_SET_PATTERN = 
        Pattern.compile("^([a-zA-Z]\\w*)\\s*=\\s*\\{\\s*(-?\\d+(?:\\.\\d+)?(?:\\s*,\\s*-?\\d+(?:\\.\\d+)?)*)\\s*\\}$");
    
    // Unnamed number set: just "{1,2,3}" or "{-2.5, 10, 3.14}"
    private static final Pattern UNNAMED_NUMBER_SET_PATTERN = 
        Pattern.compile("^\\{\\s*(-?\\d+(?:\\.\\d+)?(?:\\s*,\\s*-?\\d+(?:\\.\\d+)?)*)\\s*\\}$");
    
    /**
     * Parse user input and determine what type of entity should be created
     * @param input The input string to parse
     * @return ParseResult containing the type, name (if any), and expression
     * @throws IllegalArgumentException if input is null, empty, or doesn't match any known pattern
     */
    public static ParseResult parse(String input) {

        if (input == null || input.trim().isEmpty())
            throw new IllegalArgumentException("Input cannot be null or empty");
        
        String trimmedInput = input.trim();
        
        // Try to parse as a range definition (check this first as it's more specific)
        ParseResult rangeResult = parseRangeDefinition(trimmedInput);
        if (rangeResult != null) return rangeResult;
        
        // Try to parse as a number set definition
        ParseResult numberSetResult = parseNumberSetDefinition(trimmedInput);
        if (numberSetResult != null) return numberSetResult;
        
        // Try to parse as a function definition
        ParseResult expressionResult = parseExpressionDefinition(trimmedInput);
        if (expressionResult != null) return expressionResult;
        
        // No pattern matched - throw exception
        throw new IllegalArgumentException("Unrecognized input format: \"" + input + "\"");
    }
    
    /**
     * Parse expression definition to extract name and expression
     * Supports formats: 
     * - Named: "f(x)=x^2"
     * - Unnamed: "x^2" or "sin(x) + 2*x"
     * @param input The input string
     * @return ParseResult if it matches expression pattern, null otherwise
     */
    private static ParseResult parseExpressionDefinition(String input) {

        Matcher namedMatcher = NAMED_EXPRESSION_PATTERN.matcher(input);
        Matcher unnamedMatcher = UNNAMED_EXPRESSION_PATTERN.matcher(input);
        if (namedMatcher.matches()) {

            String name = namedMatcher.group(1);
            String expression = namedMatcher.group(2);
            return new ParseResult(InputType.EXPRESSION, name, expression);
        } else if (unnamedMatcher.matches()) {

            String expression = unnamedMatcher.group(1);
            return new ParseResult(InputType.EXPRESSION, null, expression);
        }
        
        return null;
    }
    
    /**
     * Parse range definition to extract name and range bounds
     * Supports formats:
     * - Named: "c=[1:5]" or "range=[-2.5:10.3]"
     * - Unnamed: "[1:5]" or "[-2.5:10.3]"
     * @param input The input string
     * @return ParseResult if it matches range pattern, null otherwise
     */
    private static ParseResult parseRangeDefinition(String input) {

        // Try named range first
        Matcher namedMatcher = NAMED_RANGE_PATTERN.matcher(input);
        Matcher unnamedMatcher = UNNAMED_RANGE_PATTERN.matcher(input);
        if (namedMatcher.matches()) {

            String name = namedMatcher.group(1);
            String start = namedMatcher.group(2);
            String end = namedMatcher.group(3);
            String rangeExpression = "[" + start + ":" + end + "]";
            return new ParseResult(InputType.RANGE, name, rangeExpression);
        } else if (unnamedMatcher.matches()) {
            
            String start = unnamedMatcher.group(1);
            String end = unnamedMatcher.group(2);
            String rangeExpression = "[" + start + ":" + end + "]";
            return new ParseResult(InputType.RANGE, null, rangeExpression);
        }
        
        return null;
    }
    
    /**
     * Parse number set definition to extract name and set values
     * Supports formats:
     * - Named: "s={1,2,3}" or "set={-2.5, 10, 3.14}"
     * - Unnamed: "{1,2,3}" or "{-2.5, 10, 3.14}"
     * @param input The input string
     * @return ParseResult if it matches number set pattern, null otherwise
     */
    private static ParseResult parseNumberSetDefinition(String input) {

        // Try named number set first
        Matcher namedMatcher = NAMED_NUMBER_SET_PATTERN.matcher(input);
        if (namedMatcher.matches()) {
            String name = namedMatcher.group(1);
            String values = namedMatcher.group(2);
            String setExpression = "{" + values + "}";
            return new ParseResult(InputType.NUMBER_SET, name, setExpression);
        }
        
        // Try unnamed number set
        Matcher unnamedMatcher = UNNAMED_NUMBER_SET_PATTERN.matcher(input);
        if (unnamedMatcher.matches()) {
            String values = unnamedMatcher.group(1);
            String setExpression = "{" + values + "}";
            return new ParseResult(InputType.NUMBER_SET, null, setExpression);
        }
        
        return null;
    }    /**
     * Parse function definition (legacy method for backward compatibility)
     * @param input The input string
     * @return String array [name, expression] or [null, expression]
     * @deprecated Use {@link #parse(String)} instead
     */
    @Deprecated
    public static String[] parseFunctionDefinitionLegacy(String input) {

        ParseResult result = parse(input);
        return new String[] { result.getName(), result.getExpression() };
    }
}
