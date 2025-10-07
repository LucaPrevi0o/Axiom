package function;
import javax.swing.*;
import java.awt.*;

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
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.add(colorIndicator);
        topPanel.add(enableCheckbox);

        add(topPanel, BorderLayout.NORTH);

    // center panel holds either latexLabel (view mode) or expressionField (edit mode)
    centerPanel = new JPanel(new CardLayout());
    centerPanel.add(latexLabel, "view");
    centerPanel.add(expressionField, "edit");
    // start in view mode
    ((CardLayout) centerPanel.getLayout()).show(centerPanel, "view");
    add(centerPanel, BorderLayout.CENTER);

        // right-side panel: edit + delete
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        rightPanel.add(editButton);
        rightPanel.add(deleteButton);
        add(rightPanel, BorderLayout.EAST);
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
            // naive latex-like rendering using HTML; real LaTeX requires a library
            String esc = escapeHtml(expr);
            latexLabel.setText("<html><code>" + esc + "</code></html>");
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