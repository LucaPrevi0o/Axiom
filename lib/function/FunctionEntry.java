package lib.function;

import lib.constants.UIConstants;
import lib.expression.ExpressionFormatter;
import javax.swing.*;
import java.awt.*;

/**
 * Represents a single function entry in the function panel
 * Now focused on UI state and event handling
 */
public class FunctionEntry extends JPanel {
    
    private final ExpressionFormatter formatter = new ExpressionFormatter();
    
    // UI Components
    private JTextField expressionField;
    private JLabel displayLabel;
    private JCheckBox enableCheckbox;
    private JButton deleteButton;
    private JButton editButton;
    private JPanel colorIndicator;
    private JPanel centerPanel;
    
    // State
    private Color functionColor;
    private FunctionPanel parent;
    
    // View modes
    private static final String VIEW_MODE = "view";
    private static final String EDIT_MODE = "edit";
    
    /**
     * Constructor
     * @param expression Initial expression
     * @param color Function color
     * @param parent Parent panel
     */
    public FunctionEntry(String expression, Color color, FunctionPanel parent) {
        this.functionColor = color;
        this.parent = parent;
        
        initComponents(expression);
        layoutComponents();
    }
    
    /**
     * Initialize components
     */
    private void initComponents(String expression) {
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.MAX_FUNCTION_ENTRY_HEIGHT));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Color indicator with click handler
        colorIndicator = createColorIndicator();
        
        // Enable checkbox
        enableCheckbox = new JCheckBox();
        enableCheckbox.setSelected(true);
        enableCheckbox.addActionListener(e -> parent.updateGraph());
        
        // Expression field (for editing)
        expressionField = new JTextField(expression);
        expressionField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        expressionField.addActionListener(e -> commitEdit());
        expressionField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                commitEdit();
            }
        });
        
        // Display label (for viewing)
        displayLabel = new JLabel();
        displayLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        formatter.formatExpression(expression, displayLabel);
        
        // Buttons
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
     * Create color indicator panel
     */
    private JPanel createColorIndicator() {
        JPanel panel = new JPanel();
        panel.setBackground(functionColor);
        panel.setPreferredSize(new Dimension(20, 20));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Color picker on click
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Color chosen = JColorChooser.showDialog(
                    FunctionEntry.this, 
                    "Choose Function Color", 
                    functionColor
                );
                if (chosen != null) {
                    functionColor = chosen;
                    colorIndicator.setBackground(functionColor);
                    parent.updateGraph();
                }
            }
        });
        
        return panel;
    }
    
    /**
     * Layout components
     */
    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.add(colorIndicator);
        topPanel.add(enableCheckbox);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with card layout (view/edit modes)
        centerPanel = new JPanel(new CardLayout());
        centerPanel.add(displayLabel, VIEW_MODE);
        centerPanel.add(expressionField, EDIT_MODE);
        ((CardLayout) centerPanel.getLayout()).show(centerPanel, VIEW_MODE);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    /**
     * Switch to edit mode
     */
    private void startEdit() {
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, EDIT_MODE);
        expressionField.requestFocusInWindow();
        expressionField.selectAll();
    }
    
    /**
     * Commit edit and return to view mode
     */
    private void commitEdit() {
        if (!expressionField.isVisible()) return;
        
        String newExpr = expressionField.getText().trim();
        
        // Check if this is now a parameter - if so, notify parent to convert
        if (FunctionParser.isParameter(newExpr)) {
            parent.convertToParameter(this, newExpr);
            return;
        }
        
        formatter.formatExpression(newExpr, displayLabel);
        
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, VIEW_MODE);
        parent.updateGraph();
    }
    
    // Getters
    public String getExpression() { return expressionField.getText(); }
    public Color getColor() { return functionColor; }
    public boolean isEnabled() { return enableCheckbox.isSelected(); }
}