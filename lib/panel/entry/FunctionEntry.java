package lib.panel.entry;
import javax.swing.*;

import lib.function.Function;

import java.awt.*;

/**
 * UI component representing a single function in the function list.
 * Displays function expression, color indicator, and visibility toggle.
 */
public abstract class FunctionEntry<T extends Function> extends JPanel {

    protected T function;
    protected JLabel expressionLabel;
    protected JTextField inputField;
    protected JButton removeButton;
    protected JButton editButton;
    
    /**
     * Constructor
     * @param function The Function object this item represents
     * @param onVisibilityChanged Callback when visibility is toggled
     * @param onRemove Callback when remove button is clicked
     */
    public FunctionEntry(T function, Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {

        this.function = function;
        
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        setBackground(Color.WHITE);
        
        // Set fixed height (increased to accommodate two rows)
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        setPreferredSize(new Dimension(280, 70));
        
        initComponents(onVisibilityChanged, onRemove, onEdit);
        layoutComponents();
    }
    
    /**
     * Initialize the components
     */
    protected void initComponents(Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {
        
        // Expression label
        expressionLabel = new JLabel((function.getName() == null ? "" : function.getName() + " = ") + function.getExpression());
        expressionLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Input field for editing expression
        inputField = new JTextField(function.getExpression());
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputField.setPreferredSize(new Dimension(200, 25));
        inputField.setVisible(false);
        
        // Remove button
        removeButton = new JButton("×");
        removeButton.setFont(new Font("Arial", Font.BOLD, 16));
        removeButton.setMargin(new Insets(0, 5, 0, 5));
        removeButton.setFocusPainted(false);
        removeButton.addActionListener(e -> {
            if (onRemove != null) { onRemove.run(); }
        });

        // Edit button
        editButton = new JButton("Edit");
        editButton.setFont(new Font("Arial", Font.PLAIN, 12));
        editButton.setMargin(new Insets(2, 5, 2, 5));
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> {
            
            boolean isEditing = inputField.isVisible();
            
            if (isEditing) {

                // Apply changes
                if (onEdit != null) onEdit.run(); 
                editButton.setText("Edit");
            } else editButton.setText("Save");
            
            // Toggle between label and input field
            inputField.setVisible(!isEditing);
            expressionLabel.setVisible(isEditing);
        });
    }
    
    /**
     * Layout the components
     */
    protected void layoutComponents() {

        // Top panel with interactive elements (checkbox, color, remove button)
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        topPanel.setBackground(Color.WHITE);
        
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftControls.setBackground(Color.WHITE);
        
        topPanel.add(leftControls, BorderLayout.WEST);
        topPanel.add(editButton, BorderLayout.CENTER);
        topPanel.add(removeButton, BorderLayout.EAST);
        
        // Bottom panel with text
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(expressionLabel);
        bottomPanel.add(inputField);
        
        // Add both panels to main layout
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
    }
    
    /**
     * Get the function associated with this item
     * @return The Function object
     */
    public Function getFunction() { return function; }
    
    /**
     * Get the text from the input field
     * @return The text in the input field
     */
    public String getInputFieldText() { return inputField.getText(); }
    
    /**
     * Update the expression label to reflect the current function expression
     */
    public void updateExpression() {

        expressionLabel.setText((function.getName() == null ? "" : function.getName() + " = ") + function.getExpression());
        inputField.setText(function.getExpression());
        revalidate();
        repaint();
    }
}
