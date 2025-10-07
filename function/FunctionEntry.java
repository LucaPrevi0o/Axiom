package function;
import javax.swing.*;
import java.awt.*;

public class FunctionEntry extends JPanel {
    
    private JTextField expressionField;
    private JCheckBox enableCheckbox;
    private JButton deleteButton;
    private JPanel colorIndicator;
    private Color functionColor;
    private FunctionPanel parent;
    
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
        expressionField.addActionListener(e -> parent.updateGraph());
        expressionField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { parent.updateGraph(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { parent.updateGraph(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { parent.updateGraph(); }
        });
        
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
        topPanel.add(new JLabel("f(x) ="));
        
        add(topPanel, BorderLayout.NORTH);
        add(expressionField, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.EAST);
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