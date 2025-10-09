package lib.ui.component.entry;

import lib.constants.UIConstants;
import lib.model.function.base.Function;
import lib.rendering.ExpressionFormatter;
import lib.ui.panel.FunctionPanel;
import javax.swing.*;
import java.awt.*;

/**
 * Abstract base class for all function entry UI components.
 * Provides common UI structure for displaying any Function object in the left panel.
 * 
 * Subclasses should implement:
 * - layoutSpecificComponents(): Add function-type-specific UI elements (sliders, color pickers, etc.)
 * - getFunction(): Return the Function model object this entry represents
 * - updateFromEdit(): Handle changes when the user edits the expression
 */
public abstract class AbstractFunctionEntry extends JPanel {
    
    protected final ExpressionFormatter formatter = new ExpressionFormatter();
    protected final FunctionPanel parent;
    
    // Store the expression string (Functions don't have getExpression())
    protected String expression;
    
    // Common UI Components
    protected JTextField expressionField;
    protected JLabel displayLabel;
    protected JButton deleteButton;
    protected JButton editButton;
    protected JPanel centerPanel;
    protected JPanel topPanel;
    
    // View modes
    protected static final String VIEW_MODE = "view";
    protected static final String EDIT_MODE = "edit";
    
    /**
     * Constructor for all function entries
     * @param expression Initial expression string
     * @param parent Parent FunctionPanel
     */
    public AbstractFunctionEntry(String expression, FunctionPanel parent) {
        this.expression = expression;
        this.parent = parent;
        initCommonComponents(expression);
        layoutCommonComponents();
    }
    
    /**
     * Initialize common components shared by all entry types
     */
    private void initCommonComponents(String expression) {
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getMaxHeight()));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        
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
        
        // Edit button
        editButton = new JButton("Edit");
        editButton.setPreferredSize(new Dimension(60, 25));
        editButton.addActionListener(e -> startEdit());
        
        // Delete button
        deleteButton = new JButton("×");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteButton.setPreferredSize(new Dimension(40, 25));
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> parent.removeFunctionEntry(this));
    }
    
    /**
     * Layout common components and call subclass to add specific ones
     */
    private void layoutCommonComponents() {
        setLayout(new BorderLayout(5, 5));
        
        // Top panel with buttons
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        // Subclasses add their specific controls here (color picker, checkbox, etc.)
        layoutSpecificComponents(topPanel);
        
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with card layout (view/edit modes)
        centerPanel = new JPanel(new CardLayout());
        centerPanel.add(displayLabel, VIEW_MODE);
        centerPanel.add(expressionField, EDIT_MODE);
        ((CardLayout) centerPanel.getLayout()).show(centerPanel, VIEW_MODE);
        add(centerPanel, BorderLayout.CENTER);
        
        // Subclasses can add additional components (e.g., slider below)
        layoutAdditionalComponents();
    }
    
    /**
     * Switch to edit mode
     */
    protected void startEdit() {
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, EDIT_MODE);
        expressionField.requestFocusInWindow();
        expressionField.selectAll();
    }
    
    /**
     * Commit edit and return to view mode
     */
    protected void commitEdit() {
        if (!expressionField.isVisible()) return;
        
        String newExpr = expressionField.getText().trim();
        
        // Let subclass handle the update logic
        boolean success = updateFromEdit(newExpr);
        
        if (success) {
            formatter.formatExpression(newExpr, displayLabel);
            CardLayout cl = (CardLayout) centerPanel.getLayout();
            cl.show(centerPanel, VIEW_MODE);
            parent.updateGraph();
        }
    }
    
    // ===== Abstract Methods - Subclasses Must Implement =====
    
    /**
     * Add function-type-specific components to the top panel
     * (e.g., color indicator, enable checkbox for plottable functions)
     * @param topPanel The top panel to add components to
     */
    protected abstract void layoutSpecificComponents(JPanel topPanel);
    
    /**
     * Layout any additional components beyond the standard top/center panels
     * (e.g., slider panel for constant functions)
     */
    protected void layoutAdditionalComponents() {
        // Default: no additional components
    }
    
    /**
     * Get the maximum height for this entry type
     * @return Maximum height in pixels
     */
    protected int getMaxHeight() {
        return UIConstants.MAX_FUNCTION_ENTRY_HEIGHT;
    }
    
    /**
     * Handle updates when user edits the expression
     * @param newExpression The new expression string
     * @return true if update was successful, false if entry needs to be recreated
     */
    protected abstract boolean updateFromEdit(String newExpression);
    
    /**
     * Get the Function model object this entry represents
     * @return The Function object
     */
    public abstract Function getFunction();
    
    // ===== Common Getters =====
    
    /**
     * Get the current expression text
     * @return Expression string
     */
    public String getExpression() {
        return expression;
    }
    
    /**
     * Set the expression text (updates both field and internal state)
     * @param expression New expression string
     */
    protected void setExpression(String expression) {
        this.expression = expression;
        expressionField.setText(expression);
        formatter.formatExpression(expression, displayLabel);
    }
}
