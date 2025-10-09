package lib.ui.component.entry;

import lib.constants.UIConstants;
import lib.model.function.base.Function;
import lib.model.function.definition.ConstantFunction;
import lib.ui.panel.FunctionPanel;
import javax.swing.*;
import java.awt.*;

public class ConstantFunctionEntry extends AbstractFunctionEntry {
    
    private ConstantFunction function;
    private JSlider slider;
    private JPanel sliderPanel;
    private JLabel valueLabel;
    private JLabel minLabel;
    private JLabel maxLabel;
    
    public ConstantFunctionEntry(String expression, ConstantFunction function, FunctionPanel parent) {
        super(expression, parent);
        this.function = function;
        if (function.hasSlider()) {
            initSliderComponents();
        }
    }
    
    private void initSliderComponents() {
        slider = new JSlider(0, 100);
        slider.setValue(getSliderPosition());
        slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
                updateFunctionFromSlider();
                updateValueLabel();
                parent.updateGraph();
            }
        });
        
        valueLabel = new JLabel();
        valueLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        updateValueLabel();
        
        minLabel = new JLabel(String.format("%.2f", function.getMinValue()));
        minLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        
        maxLabel = new JLabel(String.format("%.2f", function.getMaxValue()));
        maxLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
    }
    
    private int getSliderPosition() {
        double normalizedValue = (function.getCurrentValue() - function.getMinValue()) / (function.getMaxValue() - function.getMinValue());
        return (int) (normalizedValue * 100);
    }
    
    private void updateFunctionFromSlider() {
        double normalizedValue = slider.getValue() / 100.0;
        double value = function.getMinValue() + normalizedValue * (function.getMaxValue() - function.getMinValue());
        function.setCurrentValue(value);
    }
    
    private void updateValueLabel() {
        if (function.hasSlider()) {
            valueLabel.setText(String.format("%s = %.2f", function.getName(), function.getCurrentValue()));
        }
    }
    
    @Override
    protected void layoutSpecificComponents(JPanel topPanel) {
        if (function.hasSlider() && valueLabel != null) {
            topPanel.add(valueLabel);
        }
    }
    
    @Override
    protected void layoutAdditionalComponents() {
        if (!function.hasSlider()) return;
        sliderPanel = new JPanel(new BorderLayout(5, 0));
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        sliderPanel.add(minLabel, BorderLayout.WEST);
        sliderPanel.add(slider, BorderLayout.CENTER);
        sliderPanel.add(maxLabel, BorderLayout.EAST);
        add(sliderPanel, BorderLayout.SOUTH);
    }
    
    @Override
    protected int getMaxHeight() {
        return function.hasSlider() ? 120 : UIConstants.MAX_FUNCTION_ENTRY_HEIGHT;
    }
    
    @Override
    protected boolean updateFromEdit(String newExpression) {
        try {
            String[] parts = newExpression.split("=");
            if (parts.length != 2) return false;
            double value = Double.parseDouble(parts[1].trim());
            function.setCurrentValue(value);
            setExpression(newExpression);
            if (function.hasSlider()) {
                updateValueLabel();
                slider.setValue(getSliderPosition());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public Function getFunction() {
        return function;
    }
    
    public ConstantFunction getConstantFunction() {
        return function;
    }
    
    public void setValue(double value) {
        function.setCurrentValue(value);
        if (function.hasSlider()) {
            updateValueLabel();
            slider.setValue(getSliderPosition());
        }
    }
}