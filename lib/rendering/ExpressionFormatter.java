package lib.rendering;

import lib.util.HtmlEscaper;
import javax.swing.*;
import java.awt.*;
import java.util.regex.*;

/**
 * Handles formatting mathematical expressions for display,
 * including LaTeX rendering and HTML fallback.
 */
public class ExpressionFormatter {
    
    private static final float LATEX_FONT_SIZE = 18f;
    private static final int LATEX_ICON_MARGIN = 4;
    
    /**
     * Format an expression for display, attempting LaTeX rendering first
     * @param expression The mathematical expression to format
     * @param label The JLabel to update with the formatted expression
     */
    public void formatExpression(String expression, JLabel label) {
        if (expression == null || expression.trim().isEmpty()) {
            label.setText("<html><i>empty</i></html>");
            label.setIcon(null);
            return;
        }
        
        try {
            // Remove explicit multiplication signs for cleaner display
            String displayExpr = expression.replace("*", "");
            String latexExpr = convertToLatex(displayExpr);
            
            // Try LaTeX rendering using reflection (optional dependency)
            if (tryLatexRendering(latexExpr, label)) {
                return;
            }
        } catch (Exception e) {
            // Fall through to HTML rendering
        }
        
        // Fallback: HTML rendering
        renderAsHtml(expression, label);
    }
    
    /**
     * Try to render expression using JLaTeXMath library (if available)
     * @return true if successful, false otherwise
     */
    private boolean tryLatexRendering(String latexExpr, JLabel label) {
        try {
            Class<?> formulaCls = Class.forName("org.scilab.forge.jlatexmath.TeXFormula");
            Object formula = formulaCls.getConstructor(String.class).newInstance(latexExpr);
            
            Class<?> consts = Class.forName("org.scilab.forge.jlatexmath.TeXConstants");
            int styleDisplay = consts.getField("STYLE_DISPLAY").getInt(null);
            
            java.lang.reflect.Method createIcon = formulaCls.getMethod("createTeXIcon", int.class, float.class);
            Object icon = createIcon.invoke(formula, styleDisplay, LATEX_FONT_SIZE);
            
            label.setText("");
            label.setIcon((Icon) icon);
            
            // Set preferred size based on icon dimensions
            int width = (int) icon.getClass().getMethod("getIconWidth").invoke(icon);
            int height = (int) icon.getClass().getMethod("getIconHeight").invoke(icon);
            label.setPreferredSize(new Dimension(width, height));
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Render expression as HTML (fallback when LaTeX unavailable)
     */
    private void renderAsHtml(String expression, JLabel label) {
        label.setIcon(null);
        String displayExpr = expression.replace("*", "");
        String htmlExpr = convertToHtml(displayExpr);
        String escaped = HtmlEscaper.escape(htmlExpr);
        label.setText("<html><code>" + escaped + "</code></html>");
    }
    
    /**
     * Convert expression to LaTeX format
     */
    private String convertToLatex(String expr) {
        if (expr == null) return "";
        
        String result = expr.replaceAll("\\s+", " ").trim();
        
        // sqrt(...) -> \sqrt{...}
        result = replaceFunctionWithBraces(result, "sqrt", "\\sqrt");
        
        // abs(...) -> \left|...\right|
        result = replaceAbsFunction(result, true);
        
        // Trig and log functions: sin, cos, tan, log, ln
        String[] trigFuncs = {"sin", "cos", "tan", "log", "ln"};
        for (String func : trigFuncs) {
            result = replaceTrigFunction(result, func);
        }
        
        return result;
    }
    
    /**
     * Convert expression to HTML format
     */
    private String convertToHtml(String expr) {
        if (expr == null) return "";
        
        String result = expr.replaceAll("\\s+", " ").trim();
        
        // sqrt(x) -> √(x)
        result = result.replaceAll("(?i)\\bsqrt\\s*\\(([^)]*)\\)", "√($1)");
        
        // abs(x) -> |x|
        result = replaceAbsFunction(result, false);
        
        return result;
    }
    
    /**
     * Replace function calls with braces notation
     */
    private String replaceFunctionWithBraces(String input, String funcName, String latexFunc) {
        Pattern pattern = Pattern.compile("(?i)\\b" + funcName + "\\s*\\(([^)]*)\\)");
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String inner = matcher.group(1);
            String replacement = latexFunc + "{" + inner + "}";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    /**
     * Replace trigonometric function calls
     */
    private String replaceTrigFunction(String input, String funcName) {
        Pattern pattern = Pattern.compile("(?i)\\b" + funcName + "\\s*\\(([^)]*)\\)");
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String inner = matcher.group(1);
            String replacement = "\\" + funcName + "(" + inner + ")";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    /**
     * Replace abs() function with vertical bars
     * @param input The input string
     * @param latex If true, use LaTeX notation; otherwise use plain bars
     */
    private String replaceAbsFunction(String input, boolean latex) {
        StringBuilder output = new StringBuilder();
        int i = 0;
        
        while (i < input.length()) {
            int idx = indexOfWord(input, "abs", i);
            if (idx == -1) {
                output.append(input.substring(i));
                break;
            }
            
            output.append(input.substring(i, idx));
            
            int p = idx + 3;
            while (p < input.length() && Character.isWhitespace(input.charAt(p))) p++;
            
            if (p >= input.length() || input.charAt(p) != '(') {
                output.append(input.substring(idx, p));
                i = p;
                continue;
            }
            
            // Find matching closing parenthesis
            int start = p + 1;
            int depth = 1;
            int j = start;
            
            for (; j < input.length(); j++) {
                char c = input.charAt(j);
                if (c == '(') depth++;
                else if (c == ')') {
                    depth--;
                    if (depth == 0) break;
                }
            }
            
            if (j >= input.length() || depth != 0) {
                output.append(input.substring(idx));
                break;
            }
            
            String inner = input.substring(start, j);
            if (latex) {
                output.append("\\left|").append(inner).append("\\right|");
            } else {
                output.append("|").append(inner).append("|");
            }
            
            i = j + 1;
        }
        
        return output.toString();
    }
    
    /**
     * Find word in string, case-insensitive, with word boundary checking
     */
    private int indexOfWord(String str, String word, int fromIndex) {
        String lower = str.toLowerCase();
        String wordLower = word.toLowerCase();
        int idx = lower.indexOf(wordLower, fromIndex);
        
        while (idx != -1) {
            boolean leftOk = idx == 0 || !Character.isLetterOrDigit(lower.charAt(idx - 1));
            int after = idx + wordLower.length();
            boolean rightOk = after >= lower.length() || !Character.isLetterOrDigit(lower.charAt(after));
            
            if (leftOk && rightOk) return idx;
            idx = lower.indexOf(wordLower, idx + 1);
        }
        
        return -1;
    }
}