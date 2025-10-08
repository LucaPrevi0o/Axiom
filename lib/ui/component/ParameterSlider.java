package lib.ui.component;

import lib.constants.UIConstants;
import lib.model.Parameter;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * UI component that displays a parameter with a slider for dynamic control
 */
public class ParameterSlider extends JPanel {
    
    private final Parameter parameter;
    private final JSlider slider;
    private final JLabel valueLabel;
    private final JButton deleteButton;
    private final ParameterChangeListener listener;
    private final DecimalFormat decimalFormat;
    
    /**
     * Listener interface for parameter value changes
     */
    public interface ParameterChangeListener {
        void onParameterChanged(Parameter parameter);
        void onParameterDeleted(Parameter parameter);
    }
    
    /**
     * Create a parameter slider
     * @param parameter The parameter to control
     * @param listener Listener for value changes
     */
    public ParameterSlider(Parameter parameter, ParameterChangeListener listener) {
        this.parameter = parameter;
        this.listener = listener;
        this.decimalFormat = new DecimalFormat("0.##");
        
        // Create slider
        this.slider = new JSlider(0, UIConstants.SLIDER_STEPS);
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
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        setPreferredSize(new Dimension(250, 100));
        
        // Slider listener
        slider.addChangeListener(e -> {
            updateParameterFromSlider();
            updateValueLabel();
            if (listener != null && !slider.getValueIsAdjusting()) {
                listener.onParameterChanged(parameter);
            }
        });
        
        // Delete button listener
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
        
        // Top panel: name and value
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(parameter.getName() + " =");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(nameLabel, BorderLayout.WEST);
        topPanel.add(valueLabel, BorderLayout.CENTER);
        topPanel.add(deleteButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Add slider with some vertical padding
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.add(slider, BorderLayout.CENTER);
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(sliderPanel, BorderLayout.CENTER);
        
        // Bottom panel: min and max labels
        JPanel rangePanel = new JPanel(new BorderLayout());
        JLabel minLabel = new JLabel(decimalFormat.format(parameter.getMinValue()));
        JLabel maxLabel = new JLabel(decimalFormat.format(parameter.getMaxValue()));
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
        double range = parameter.getRange();
        double normalized = (parameter.getCurrentValue() - parameter.getMinValue()) / range;
        int sliderValue = (int) Math.round(normalized * UIConstants.SLIDER_STEPS);
        slider.setValue(sliderValue);
    }
    
    /**
     * Update parameter value from slider position
     */
    private void updateParameterFromSlider() {
        double normalized = (double) slider.getValue() / (double) UIConstants.SLIDER_STEPS;
        double value = parameter.getMinValue() + normalized * parameter.getRange();
        parameter.setCurrentValue(value);
    }
    
    /**
     * Update value label text
     */
    private void updateValueLabel() {
        valueLabel.setText(decimalFormat.format(parameter.getCurrentValue()));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    /**
     * Get the parameter
     * @return The parameter
     */
    public Parameter getParameter() {
        return parameter;
    }
}
