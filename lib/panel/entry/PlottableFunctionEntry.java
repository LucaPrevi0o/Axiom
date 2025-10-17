package lib.panel.entry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lib.function.PlottableFunction;

public abstract class PlottableFunctionEntry<T extends PlottableFunction> extends FunctionEntry<T> {

    private JCheckBox visibilityCheckBox;
    private JPanel colorIndicator;
    
    /**
     * Constructor
     * 
     * @param function The PlottableFunction object
     */
    public PlottableFunctionEntry(T function, Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {

        super(function, onVisibilityChanged, onRemove, onEdit);
    }
    
    /**
     * Initialize the components
     * @param onVisibilityChanged Callback when visibility is toggled
     * @param onRemove Callback when remove button is clicked
     * @param onEdit Callback when edit button is clicked
     */
    @Override
    protected void initComponents(Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {

        // Visibility checkbox
        visibilityCheckBox = new JCheckBox();
        visibilityCheckBox.setSelected(function.isVisible());
        visibilityCheckBox.addActionListener(e -> {
            function.setVisible(visibilityCheckBox.isSelected());
            if (onVisibilityChanged != null) { onVisibilityChanged.run(); }
        });
        
        // Expression label
        expressionLabel = new JLabel(getExpressionLabel());
        expressionLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Input field for editing expression
        inputField = new JTextField(function.getExpression());
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputField.setPreferredSize(new Dimension(200, 25));
        inputField.setVisible(false);
        
        // Color indicator
        colorIndicator = new JPanel();
        colorIndicator.setBackground(function.getColor());
        colorIndicator.setPreferredSize(new Dimension(20, 20));
        colorIndicator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorIndicator.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        colorIndicator.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                Color newColor = JColorChooser.showDialog(
                    PlottableFunctionEntry.this,
                    "Choose Function Color",
                    function.getColor()
                );
                if (newColor != null) {

                    function.setColor(newColor);
                    colorIndicator.setBackground(newColor);
                    if (onVisibilityChanged != null) { onVisibilityChanged.run(); }
                }
            }
        });
        
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
    @Override
    protected void layoutComponents() {

        // Top panel with interactive elements (checkbox, color, remove button)
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        topPanel.setBackground(Color.WHITE);
        
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftControls.setBackground(Color.WHITE);
        leftControls.add(visibilityCheckBox);
        leftControls.add(colorIndicator);
        
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
     * Get the PlottableFunction object
     * 
     * @return The PlottableFunction
     */
    public PlottableFunction getFunction() { return function; }

    /**
     * Get the visibility checkbox
     * 
     * @return The JCheckBox
     */
    public JCheckBox getVisibilityCheckBox() { return visibilityCheckBox; }

    /**
     * Get the color indicator panel
     * 
     * @return The JPanel showing the color
     */
    public JPanel getColorIndicator() { return colorIndicator; }

    /**
     * Update the expression label to reflect the current function expression
     */
    @Override
    protected String getExpressionLabel() { return (function.getName() == null ? "" : function.getName() + "(x) = ") + function.getExpression(); }
}
