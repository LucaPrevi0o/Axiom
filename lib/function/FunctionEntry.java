package lib.function;
import javax.swing.*;
import java.awt.*;
import java.util.regex.*;
// JLaTeXMath is optional; use reflection so code compiles without the library

public class FunctionEntry extends JPanel {
    
    private JTextField expressionField;
    private JLabel latexLabel;
    private JCheckBox enableCheckbox;
    private JButton deleteButton;
    private JButton editButton;
    private JPanel colorIndicator;
    private Color functionColor;
    private FunctionPanel parent;
    private JPanel centerPanel;
    
    /**
     * Constructor to create a function entry
     * @param expression The initial expression
     * @param color The color for this function
     * @param parent The parent FunctionPanel
     */
    public FunctionEntry(String expression, Color color, FunctionPanel parent) {
        this.functionColor = color;
        this.parent = parent;
        
        initComponents(expression);
        layoutComponents();
    }
    
    /**
     * Initialize GUI components
     * @param expression The initial expression
     */
    private void initComponents(String expression) {
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        colorIndicator = new JPanel();
        colorIndicator.setBackground(functionColor);
        colorIndicator.setPreferredSize(new Dimension(20, 20));
        colorIndicator.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        // Open a color chooser when the color indicator is clicked
        colorIndicator.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        colorIndicator.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Color chosen = JColorChooser.showDialog(FunctionEntry.this, "Choose Function Color", functionColor);
                if (chosen != null) {
                    functionColor = chosen;
                    colorIndicator.setBackground(functionColor);
                    parent.updateGraph();
                }
            }
        });
        
        enableCheckbox = new JCheckBox();
        enableCheckbox.setSelected(true);
        enableCheckbox.addActionListener(e -> parent.updateGraph());
        
        expressionField = new JTextField(expression);
        expressionField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        // latexLabel shows the expression in non-edit mode
        latexLabel = new JLabel();
        latexLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        latexLabel.setOpaque(false);
        setLatexText(expression);

        // listeners: when editing commits, update graph and label
        expressionField.addActionListener(e -> commitEdit());
        expressionField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { parent.updateGraph(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { parent.updateGraph(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { parent.updateGraph(); }
        });
        expressionField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // commit edit on focus lost
                commitEdit();
            }
        });

        // Edit button toggles edit mode
        editButton = new JButton("Edit");
        editButton.setPreferredSize(new Dimension(60, 25));
        editButton.addActionListener(e -> startEdit());
        
    deleteButton = new JButton("×");
    deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
    deleteButton.setPreferredSize(new Dimension(40, 25));
    deleteButton.setFocusPainted(false);
    deleteButton.addActionListener(e -> parent.removeFunction(this));
    }
    
    /**
     * Layout the components in the entry
     */
    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));
        
        // top panel: color indicator, enable checkbox, then edit/delete buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.add(colorIndicator);
        topPanel.add(enableCheckbox);
        topPanel.add(editButton);
        topPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);

        // center panel holds either latexLabel (view mode) or expressionField (edit mode)
        centerPanel = new JPanel(new CardLayout());
        centerPanel.add(latexLabel, "view");
        centerPanel.add(expressionField, "edit");
        // start in view mode
        ((CardLayout) centerPanel.getLayout()).show(centerPanel, "view");
        add(centerPanel, BorderLayout.CENTER);
    }

    private void startEdit() {
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, "edit");
        expressionField.requestFocusInWindow();
        expressionField.selectAll();
    }

    private void commitEdit() {
        if (!expressionField.isVisible()) return;
        // update label and hide editor
        String newExpr = expressionField.getText();
        setLatexText(newExpr);
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, "view");
        parent.updateGraph();
    }

    private void setLatexText(String expr) {
        if (expr == null || expr.trim().isEmpty()) {
            latexLabel.setText("<html><i>empty</i></html>");
        } else {
            try {
                // Prepare a display-only expression: remove explicit '*' multiplication
                // signs so the rendered label looks cleaner (e.g. show "2x" instead of "2*x").
                // Do NOT modify the underlying expressionField text.
                String displayExpr = expr.replace("*", "");
                String latexExpr = mapFunctionsToLatex(displayExpr);

                // Use reflection to avoid hard dependency at compile time
                Class<?> formulaCls = Class.forName("org.scilab.forge.jlatexmath.TeXFormula");
                Object formula = formulaCls.getConstructor(String.class).newInstance(latexExpr);
                Class<?> consts = Class.forName("org.scilab.forge.jlatexmath.TeXConstants");
                int styleDisplay = consts.getField("STYLE_DISPLAY").getInt(null);
                java.lang.reflect.Method createIcon = formulaCls.getMethod("createTeXIcon", int.class, float.class);
                Object icon = createIcon.invoke(formula, styleDisplay, 18f);
                latexLabel.setText("");
                latexLabel.setIcon((javax.swing.Icon) icon);
                // reflectively retrieve icon size
                int iw = (int) icon.getClass().getMethod("getIconWidth").invoke(icon);
                int ih = (int) icon.getClass().getMethod("getIconHeight").invoke(icon);
                latexLabel.setPreferredSize(new Dimension(iw, ih));
            } catch (Throwable t) {
                // Log the error so we can diagnose why reflection failed
                System.err.println("JLaTeXMath render failed: " + t.getClass().getName() + ": " + t.getMessage());
                t.printStackTrace(System.err);
                // fallback: naive HTML rendering
                latexLabel.setIcon(null);
                String displayForHtml = mapFunctionsToHtml(expr.replace("*", ""));
                String esc = escapeHtml(displayForHtml);
                latexLabel.setText("<html><code>" + esc + "</code></html>");
            }
        }
    }

    // minimal HTML escaper to avoid external dependencies
    private String escapeHtml(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&': out.append("&amp;"); break;
                case '<': out.append("&lt;"); break;
                case '>': out.append("&gt;"); break;
                case '"': out.append("&quot;"); break;
                case '\'': out.append("&#39;"); break;
                default: out.append(c);
            }
        }
        return out.toString();
    }

    // Convert common function names to LaTeX-like display form for label only
    private String mapFunctionsToLatex(String s) {
        if (s == null) return "";
        String res = s;
        // Normalize whitespace
        res = res.replaceAll("\\s+", " ").trim();

        // sqrt(...) -> \sqrt{...}
        Pattern pSqrt = Pattern.compile("(?i)\\bsqrt\\s*\\(([^)]*)\\)");
        Matcher m = pSqrt.matcher(res);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String inner = m.group(1);
            String repl = "\\sqrt{" + inner + "}"; // becomes \sqrt{...}
            m.appendReplacement(sb, Matcher.quoteReplacement(repl));
        }
        m.appendTail(sb);
        res = sb.toString();

        // abs(...) -> \left| ... \right| (handle nested parentheses properly)
        res = replaceAbsWith(res, true);

        // trig and log functions: sin, cos, tan, log, ln -> \sin(...), etc.
        String[] funcs = {"sin", "cos", "tan", "log", "ln"};
        for (String f : funcs) {
            Pattern p = Pattern.compile("(?i)\\b" + f + "\\s*\\(([^)]*)\\)");
            m = p.matcher(res);
            sb = new StringBuffer();
            while (m.find()) {
                String inner = m.group(1);
                String repl = "\\" + f + "(" + inner + ")"; // e.g. \sin(x)
                m.appendReplacement(sb, Matcher.quoteReplacement(repl));
            }
            m.appendTail(sb);
            res = sb.toString();
        }

        return res;
    }

    // Provide an HTML-friendly display mapping for the fallback renderer
    private String mapFunctionsToHtml(String s) {
        if (s == null) return "";
        String r = s.replaceAll("\\s+", " ").trim();
        // sqrt(x) -> √(x)
        r = r.replaceAll("(?i)\\bsqrt\\s*\\(([^)]*)\\)", "√($1)");
    // abs(x) -> |x| (handle nested parentheses)
    r = replaceAbsWith(r, false);
        // leave trig/log names as-is but normalize spacing: sin(x)
        r = r.replaceAll("(?i)\\bsin\\s*\\(([^)]*)\\)", "sin($1)");
        r = r.replaceAll("(?i)\\bcos\\s*\\(([^)]*)\\)", "cos($1)");
        r = r.replaceAll("(?i)\\btan\\s*\\(([^)]*)\\)", "tan($1)");
        r = r.replaceAll("(?i)\\blog\\s*\\(([^)]*)\\)", "log($1)");
        r = r.replaceAll("(?i)\\bln\\s*\\(([^)]*)\\)", "ln($1)");
        return r;
    }

    // Replace abs(...) occurrences respecting nested parentheses.
    // If latex==true, replace with \left|...\right|, else with |...| for HTML fallback.
    private String replaceAbsWith(String input, boolean latex) {
        StringBuilder out = new StringBuilder();
        String s = input;
        int i = 0;
        while (i < s.length()) {
            int idx = indexOfWordIgnoreCase(s, "abs", i);
            if (idx == -1) {
                out.append(s.substring(i));
                break;
            }
            // append before abs
            out.append(s.substring(i, idx));
            int p = idx + 3; // position after 'abs'
            // skip spaces
            while (p < s.length() && Character.isWhitespace(s.charAt(p))) p++;
            if (p >= s.length() || s.charAt(p) != '(') {
                // not a function call, copy 'abs' literally
                out.append(s.substring(idx, p));
                i = p;
                continue;
            }
            // find matching closing parenthesis
            int start = p + 1;
            int depth = 1;
            int j = start;
            for (; j < s.length(); j++) {
                char c = s.charAt(j);
                if (c == '(') depth++;
                else if (c == ')') {
                    depth--;
                    if (depth == 0) break;
                }
            }
            if (j >= s.length() || depth != 0) {
                // unmatched, just copy rest and stop
                out.append(s.substring(idx));
                break;
            }
            String inner = s.substring(start, j);
            if (latex) {
                out.append("\\left|").append(inner).append("\\right|");
            } else {
                out.append("|").append(inner).append("|");
            }
            i = j + 1;
        }
        return out.toString();
    }

    // helper: find word 'word' case-insensitive starting from fromIndex where word boundary required
    private int indexOfWordIgnoreCase(String s, String word, int fromIndex) {
        String lower = s.toLowerCase();
        String w = word.toLowerCase();
        int idx = lower.indexOf(w, fromIndex);
        while (idx != -1) {
            boolean leftOk = idx == 0 || !Character.isLetterOrDigit(lower.charAt(idx - 1));
            int after = idx + w.length();
            boolean rightOk = after >= lower.length() || !Character.isLetterOrDigit(lower.charAt(after));
            if (leftOk && rightOk) return idx;
            idx = lower.indexOf(w, idx + 1);
        }
        return -1;
    }

    
    
    /**
     * Get the expression from this entry
     * @return The expression string
     */
    public String getExpression() {
        return expressionField.getText();
    }
    
    /**
     * Get the color for this function
     * @return The color
     */
    public Color getColor() {
        return functionColor;
    }
    
    /**
     * Check if this function is enabled
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enableCheckbox.isSelected();
    }
}