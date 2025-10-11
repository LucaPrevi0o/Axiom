package lib.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lib.PlottableFunction;

public class PlottableFunctionEntry extends FunctionEntry {

    private JCheckBox visibilityCheckBox;
    private JPanel colorIndicator;
    
    /**
     * Constructor
     * 
     * @param function The PlottableFunction object
     */
    public PlottableFunctionEntry(PlottableFunction function, Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {

        super(function, onVisibilityChanged, onRemove, onEdit);
        this.visibilityCheckBox = new JCheckBox();
        this.colorIndicator = new JPanel();
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
        visibilityCheckBox.setSelected(((PlottableFunction)function).isVisible());
        visibilityCheckBox.addActionListener(e -> {
            ((PlottableFunction)function).setVisible(visibilityCheckBox.isSelected());
            if (onVisibilityChanged != null) { onVisibilityChanged.run(); }
        });
        
        // Expression label
        expressionLabel = new JLabel((function.getName() == null ? "" : function.getName() + "(x) = ") + function.getExpression());
        expressionLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Input field for editing expression
        inputField = new JTextField(function.getExpression());
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputField.setPreferredSize(new Dimension(200, 25));
        inputField.setVisible(false);
        
        // Color indicator
        colorIndicator = new JPanel();
        colorIndicator.setBackground(((PlottableFunction)function).getColor());
        colorIndicator.setPreferredSize(new Dimension(20, 20));
        colorIndicator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
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
    public PlottableFunction getFunction() { return (PlottableFunction)function; }

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
}
