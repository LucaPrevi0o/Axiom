package lib.ui.component;

import lib.constants.UIConstants;
import lib.model.Parameter;
import lib.rendering.ExpressionFormatter;
import lib.util.FormattingUtils;
import javax.swing.*;
import java.awt.*;

/**
 * UI component that displays a parameter with its definition and slider for dynamic control
 */
public class ParameterEntry extends JPanel {
    
    private final Parameter parameter;
    private final JSlider slider;
    private final JLabel valueLabel;
    private final JLabel definitionLabel;
    private final JButton deleteButton;
    private final ParameterChangeListener listener;
    private final ExpressionFormatter formatter;
    
    /**
     * Listener interface for parameter value changes
     */
    public interface ParameterChangeListener {
        void onParameterChanged(Parameter parameter);
        void onParameterDeleted(Parameter parameter);
    }
    
    /**
     * Create a parameter entry
     * @param parameter The parameter to control
     * @param listener Listener for value changes
     */
    public ParameterEntry(Parameter parameter, ParameterChangeListener listener) {
        this.parameter = parameter;
        this.listener = listener;
        this.formatter = new ExpressionFormatter();
        
        // Create definition label
        this.definitionLabel = new JLabel();
        String definition = parameter.getName() + "=[" + 
                          FormattingUtils.formatDecimal(parameter.getMinValue(), 2) + ":" + 
                          FormattingUtils.formatDecimal(parameter.getMaxValue(), 2) + "]";
        formatter.formatExpression(definition, definitionLabel);
        
        // Create slider
        this.slider = new JSlider(0, UIConstants.SLIDER_STEPS);
        
        // For discrete parameters, configure slider differently
        if (parameter.isDiscrete()) {
            int stepCount = parameter.getDiscreteStepCount();
            slider.setMaximum(stepCount - 1);
            slider.setSnapToTicks(true);
            slider.setPaintTicks(true);
            // Only show major tick labels if reasonable number of steps
            if (stepCount <= 10) {
                slider.setMajorTickSpacing(1);
                slider.setPaintLabels(true);
            }
        }
        
        updateSliderFromParameter();
        
        // Create value label
        this.valueLabel = new JLabel();
        updateValueLabel();
        
        // Create delete button
        this.deleteButton = new JButton("×");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteButton.setMargin(new Insets(0, 5, 0, 5));
        deleteButton.setFocusPainted(false);
        
        initComponents();
        layoutComponents();
    }
    
    /**
     * Initialize component listeners
     */
    private void initComponents() {
        setPreferredSize(new Dimension(250, 120));
        setMinimumSize(new Dimension(250, 120));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // Add slider listener
        slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
                updateParameterFromSlider();
                updateValueLabel();
                if (listener != null) {
                    listener.onParameterChanged(parameter);
                }
            }
        });
        
        // Add delete button listener
        deleteButton.addActionListener(e -> {
            if (listener != null) {
                listener.onParameterDeleted(parameter);
            }
        });
    }
    
    /**
     * Layout components
     */
    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));
        
        // Top panel: definition label, value, and delete button
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        
        // Left side: definition
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.add(definitionLabel);
        topPanel.add(leftPanel, BorderLayout.WEST);
        
        // Right side: value + delete button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JLabel equalsLabel = new JLabel("=");
        equalsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightPanel.add(equalsLabel);
        rightPanel.add(valueLabel);
        rightPanel.add(deleteButton);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Middle: slider with explicit size and padding
        JPanel sliderPanel = new JPanel(new BorderLayout());
        // Force slider to have a minimum height
        slider.setPreferredSize(new Dimension(200, 30));
        slider.setMinimumSize(new Dimension(100, 30));
        sliderPanel.add(slider, BorderLayout.CENTER);
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        sliderPanel.setPreferredSize(new Dimension(250, 40));
        add(sliderPanel, BorderLayout.CENTER);
        
        // Bottom panel: min and max labels
        JPanel rangePanel = new JPanel(new BorderLayout());
        JLabel minLabel = new JLabel(FormattingUtils.formatDecimal(parameter.getMinValue(), 2));
        JLabel maxLabel = new JLabel(FormattingUtils.formatDecimal(parameter.getMaxValue(), 2));
        minLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        maxLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        rangePanel.add(minLabel, BorderLayout.WEST);
        rangePanel.add(maxLabel, BorderLayout.EAST);
        
        add(rangePanel, BorderLayout.SOUTH);
    }
    
    /**
     * Update slider position from parameter value
     */
    private void updateSliderFromParameter() {
        if (parameter.isDiscrete()) {
            // For discrete parameters, slider has one position per integer value
            int sliderValue = (int) (parameter.getCurrentValue() - parameter.getMinValue());
            slider.setValue(sliderValue);
        } else {
            // For continuous parameters, use normalized position
            double range = parameter.getRange();
            double normalized = (parameter.getCurrentValue() - parameter.getMinValue()) / range;
            int sliderValue = (int) Math.round(normalized * UIConstants.SLIDER_STEPS);
            slider.setValue(sliderValue);
        }
    }
    
    /**
     * Update parameter value from slider position
     */
    private void updateParameterFromSlider() {
        if (parameter.isDiscrete()) {
            // For discrete parameters, slider position directly maps to integer value
            double value = parameter.getMinValue() + slider.getValue();
            parameter.setCurrentValue(value);
        } else {
            // For continuous parameters, normalize slider position
            double normalized = (double) slider.getValue() / (double) UIConstants.SLIDER_STEPS;
            double value = parameter.getMinValue() + normalized * parameter.getRange();
            parameter.setCurrentValue(value);
        }
    }
    
    /**
     * Update value label text
     */
    private void updateValueLabel() {
        if (parameter.isDiscrete()) {
            // Show integer value for discrete parameters
            valueLabel.setText(String.valueOf((int) parameter.getCurrentValue()));
        } else {
            // Show decimal value for continuous parameters
            valueLabel.setText(FormattingUtils.formatDecimal(parameter.getCurrentValue(), 2));
        }
        valueLabel.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    /**
     * Get the parameter
     * @return The parameter
     */
    public Parameter getParameter() {
        return parameter;
    }
    
    /**
     * Update the slider value externally (e.g., from point dragging)
     * @param newValue New parameter value
     */
    public void updateSliderValue(double newValue) {
        // Clamp to parameter range
        newValue = Math.max(parameter.getMinValue(), 
                   Math.min(parameter.getMaxValue(), newValue));
        
        // Update parameter
        parameter.setCurrentValue(newValue);
        
        // Update UI components without triggering listener
        updateSliderFromParameter();
        updateValueLabel();
    }
}
