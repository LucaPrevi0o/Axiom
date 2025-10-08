package lib.util;

/**
 * Utility class for HTML escaping
 */
public class HtmlEscaper {
    
    /**
     * Escape special HTML characters
     * @param input Input string
     * @return Escaped string safe for HTML
     */
    public static String escape(String input) {
        if (input == null) return "";
        
        StringBuilder output = new StringBuilder(Math.max(16, input.length()));
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&': output.append("&amp;"); break;
                case '<': output.append("&lt;"); break;
                case '>': output.append("&gt;"); break;
                case '"': output.append("&quot;"); break;
                case '\'': output.append("&#39;"); break;
                default: output.append(c);
            }
        }
        
        return output.toString();
    }
}