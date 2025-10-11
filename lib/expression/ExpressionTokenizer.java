package lib.expression;
/**
 * Tokenizes mathematical expressions into a processable format.
 * 
 * Handles character navigation, whitespace skipping, and token extraction.
 */
public class ExpressionTokenizer {

    private int pos = -1;
    private int ch;
    private String str;
    
    /**
     * Initialize the tokenizer with an expression string
     * @param str The expression string to tokenize
     */
    public void init(String str) {

        this.str = str;
        this.pos = -1;
        nextChar();
    }
    
    /**
     * Get the current character
     * @return The current character as an int
     */
    public int getCurrentChar() { return ch; }
    
    /**
     * Get the current position
     * @return The current position in the string
     */
    public int getPosition() { return pos; }

    /**
     * Get the expression string
     * @return The expression string
     */
    public String getString() { return str; }

    /**
     * Get the length of the expression string
     * @return The length of the string
     */
    public int getLength() { return str.length(); }
    /**
     * Advance to the next character in the expression
     */
    public void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
    
    /**
     * Eat the current character if it matches the expected character
     * @param charToEat The expected character to eat
     * @return {@code true} if the character was eaten, {@code false} otherwise
     */
    public boolean eat(int charToEat) {

        while (ch == ' ') nextChar();
        if (ch == charToEat) {

            nextChar();
            return true;
        }
        return false;
    }
    
    /**
     * Check if current character is a digit or decimal point
     * @return {@code true} if current character is a number character
     */
    public boolean isNumberChar() { return (ch >= '0' && ch <= '9') || ch == '.'; }
    
    /**
     * Check if current character is a lowercase letter
     * @return {@code true} if current character is a letter
     */
    public boolean isLetterChar() { return ch >= 'a' && ch <= 'z'; }

    /**
     * Read a number from the current position
     * @return The number as a string
     */
    public String readNumber() {

        int startPos = this.pos;
        while (isNumberChar()) nextChar();
        return str.substring(startPos, this.pos);
    }
    
    /**
     * Read a function name from the current position
     * @return The function name as a string
     */
    public String readFunctionName() {

        int startPos = this.pos;
        while (isLetterChar()) nextChar();
        return str.substring(startPos, this.pos);
    }
    
    /**
     * Read a parameter value (for parameterized functions)
     * @return The parameter value as a string
     * @throws Exception If the parameter format is invalid
     */
    public String readParameter() throws Exception {

        if (!eat('{')) return null;
        int paramStart = this.pos;
        while (isNumberChar()) nextChar();
        String param = str.substring(paramStart, this.pos);
        if (!eat('}')) throw new Exception("Expected '}' after parameter");
        return param;
    }
}
