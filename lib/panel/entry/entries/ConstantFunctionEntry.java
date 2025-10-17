package lib.panel.entry.entries;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lib.function.functions.ConstantFunction;
import lib.panel.entry.FunctionEntry;

/**
 * UI component for displaying and editing a ConstantFunction.
 * Features a slider to adjust the constant value within its defined range.
 */
public class ConstantFunctionEntry extends FunctionEntry<ConstantFunction> {

    private JSlider valueSlider;
    private JLabel valueLabel;
    private JLabel rangeLabel;
    private static final int SLIDER_PRECISION = 1000; // Number of steps for the slider
    
    /**
     * Constructor
     * @param function The ConstantFunction object
     * @param onVisibilityChanged Callback when visibility is toggled (not used for constants)
     * @param onRemove Callback when remove button is clicked
     * @param onEdit Callback when edit button is clicked
     */
    public ConstantFunctionEntry(ConstantFunction function, Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {

        super(function, onVisibilityChanged, onRemove, onEdit);
        
        // Override the size set by parent - constants need more height for the slider
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        setPreferredSize(new Dimension(280, 90));
    }
    
    /**
     * Initialize the components
     * @param onVisibilityChanged Callback when visibility is toggled
     * @param onRemove Callback when remove button is clicked
     * @param onEdit Callback when edit button is clicked
     */
    @Override
    protected void initComponents(Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {
        
        // Call parent to initialize common components (expression label, remove button, etc.)
        super.initComponents(onVisibilityChanged, onRemove, onEdit);
        
        // Value label (displays current value)
        valueLabel = new JLabel(formatValue(function.getValue()));
        valueLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        valueLabel.setForeground(new Color(0, 102, 204));
        
        // Range label (displays min:max range)
        rangeLabel = new JLabel(function.getRangeString());
        rangeLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        rangeLabel.setForeground(Color.GRAY);
        
        // Configure input field for editing
        inputField.setText(String.valueOf(function.getValue()));
        inputField.setPreferredSize(new Dimension(100, 25));
        
        // Slider for value adjustment
        valueSlider = new JSlider(0, SLIDER_PRECISION);
        valueSlider.setValue(valueToSlider(function.getValue()));
        valueSlider.setPaintTicks(false);
        valueSlider.setPaintLabels(false);
        valueSlider.setPreferredSize(new Dimension(200, 30));
        
        // Add change listener to update the function value
        valueSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                double newValue = sliderToValue(valueSlider.getValue());
                function.setValue(newValue);
                valueLabel.setText(formatValue(function.getValue()));
                inputField.setText(String.valueOf(function.getValue()));
                
                // Trigger callback if provided (to refresh plots, etc.)
                if (onVisibilityChanged != null) onVisibilityChanged.run();
            }
        });
        
        for (var listener : editButton.getActionListeners()) editButton.removeActionListener(listener);
        
        // Add custom edit button listener
        editButton.addActionListener(e -> {

            boolean isEditing = inputField.isVisible();
            if (isEditing) {

                // Save mode: parse input and update value/range
                try {

                    String input = inputField.getText().trim();

                    // Parse the range
                    String rangeContent = input.substring(2, input.length() - 2).trim();
                    String[] parts = rangeContent.split(":");
                    
                    if (parts.length != 2) throw new IllegalArgumentException("Invalid range format");
                    
                    double newMin = Double.parseDouble(parts[0].trim());
                    double newMax = Double.parseDouble(parts[1].trim());
                    
                    if (newMin >= newMax) throw new IllegalArgumentException("Min must be less than max");
                
                    // Update the function's range
                    function.getDomain().setMinBound(newMin);
                    function.getDomain().setMaxBound(newMax);
                    
                    // Update slider and labels
                    valueSlider.setValue(valueToSlider(function.getValue()));
                    rangeLabel.setText(function.getRangeString());
                    valueLabel.setText(formatValue(function.getValue()));
                    if (onEdit != null) onEdit.run();
                } catch (IllegalArgumentException ex) {

                    // If parsing fails, revert to current range expression
                    inputField.setText(formatRangeExpression());
                }
                
                editButton.setText("Edit");
            } else {

                // Edit mode: show the full range expression [[min:max]]
                inputField.setText(formatRangeExpression());
                editButton.setText("Save");
            }
            
            // Toggle visibility
            inputField.setVisible(!isEditing);
            valueLabel.setVisible(isEditing);
        });
    }
    
    /**
     * Layout components in the panel
     */
    @Override
    protected void layoutComponents() {

        removeAll(); // Clear default layout
        setLayout(new BorderLayout(5, 5));
        
        // Top panel with interactive elements (consistent with parent layout)
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        topPanel.setBackground(Color.WHITE);
        
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftControls.setBackground(Color.WHITE);
        // No visibility toggle for constants, but keep the structure consistent
        
        topPanel.add(leftControls, BorderLayout.WEST);
        topPanel.add(editButton, BorderLayout.CENTER);
        topPanel.add(removeButton, BorderLayout.EAST);
        
        // Bottom panel with expression/value and slider
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
        bottomPanel.setBackground(Color.WHITE);
        
        // Expression line (name = value)
        JPanel expressionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        expressionPanel.setBackground(Color.WHITE);
        
        // Create a combined label for name and value
        JLabel nameLabel = new JLabel(function.getName() != null ? function.getName() + " = " : "constant = ");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        expressionPanel.add(nameLabel);
        expressionPanel.add(valueLabel);
        expressionPanel.add(inputField);
        
        bottomPanel.add(expressionPanel, BorderLayout.NORTH);
        
        // Slider panel
        JPanel sliderPanel = new JPanel(new BorderLayout(5, 0));
        sliderPanel.setBackground(Color.WHITE);
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        sliderPanel.add(valueSlider, BorderLayout.CENTER);
        
        // Range label
        JPanel rangeLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rangeLabelPanel.setBackground(Color.WHITE);
        rangeLabelPanel.add(rangeLabel);
        sliderPanel.add(rangeLabelPanel, BorderLayout.SOUTH);
        
        bottomPanel.add(sliderPanel, BorderLayout.CENTER);
        
        // Add both panels to main layout
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
    }
    
    /**
     * Convert a function value to slider position
     * @param value The function value
     * @return The slider position (0 to SLIDER_PRECISION)
     */
    private int valueToSlider(double value) {

        double min = function.getDomain().getMinBound();
        double max = function.getDomain().getMaxBound();
        double normalized = (value - min) / (max - min);
        return (int) Math.round(normalized * SLIDER_PRECISION);
    }
    
    /**
     * Convert a slider position to function value
     * @param sliderValue The slider position (0 to SLIDER_PRECISION)
     * @return The function value
     */
    private double sliderToValue(int sliderValue) {

        double min = function.getDomain().getMinBound();
        double max = function.getDomain().getMaxBound();
        double normalized = (double) sliderValue / SLIDER_PRECISION;
        return min + normalized * (max - min);
    }
    
    /**
     * Format a value for display
     * @param value The value to format
     * @return Formatted string
     */
    private String formatValue(double value) {

        // Use different precision based on the magnitude
        if (Math.abs(value) < 0.01 && value != 0) return String.format("%.6f", value);
        else if (Math.abs(value) < 1) return String.format("%.4f", value);
        else if (Math.abs(value) < 100) return String.format("%.3f", value);
        else return String.format("%.2f", value);
    }
    
    /**
     * Format the range expression for editing
     * @return Range expression in format "[[min:max]]"
     */
    private String formatRangeExpression() { return "[[" + function.getDomain().getMinBound() + ":" + function.getDomain().getMaxBound() + "]]"; }
    
    /**
     * Get the current function
     * @return The ConstantFunction object
     */
    public ConstantFunction getFunction() { return function; }
    
    /**
     * Update the slider and labels when the function is modified externally
     */
    public void refresh() {

        valueSlider.setValue(valueToSlider(function.getValue()));
        valueLabel.setText(formatValue(function.getValue()));
        rangeLabel.setText(function.getRangeString());
    }
}
