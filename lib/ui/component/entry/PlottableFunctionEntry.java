package lib.ui.component.entry;

import lib.model.function.base.Function;
import lib.model.function.base.PlottableFunction;
import lib.ui.panel.FunctionPanel;
import javax.swing.*;
import java.awt.*;

/**
 * Entry for plottable functions (expressions, parametric, polar, etc.).
 * Includes a color indicator and enable/disable checkbox.
 */
public class PlottableFunctionEntry extends AbstractFunctionEntry {
    
    private PlottableFunction function;
    
    // Visual control components
    private JPanel colorIndicator;
    private JCheckBox enableCheckbox;
    
    /**
     * Constructor for plottable function entries
     * @param function The PlottableFunction to display
     * @param parent Parent FunctionPanel
     */
    public PlottableFunctionEntry(PlottableFunction function, FunctionPanel parent) {
        super(function.getDisplayString(), parent);
        this.function = function;
        
        // Components will be initialized and added in layoutSpecificComponents
        // which is called after super() completes
        initVisualControls();
        
        // Now add them to the already-created topPanel
        topPanel.add(colorIndicator, BorderLayout.WEST);
        topPanel.add(enableCheckbox, BorderLayout.EAST);
    }
    
    @Override
    protected void layoutSpecificComponents(JPanel topPanel) {
        // This is called during super() constructor when function is still null
        // Components will be added after super() completes in the constructor
    }
    
    /**
     * Initialize visual control components
     */
    private void initVisualControls() {
        // Color indicator (clickable for color picker)
        colorIndicator = new JPanel();
        colorIndicator.setPreferredSize(new Dimension(20, 20));
        colorIndicator.setBackground(function.getColor());
        colorIndicator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorIndicator.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        colorIndicator.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showColorPicker();
            }
        });
        
        // Enable/disable checkbox
        enableCheckbox = new JCheckBox("", function.isEnabled());
        enableCheckbox.addActionListener(e -> {
            function.setEnabled(enableCheckbox.isSelected());
            parent.updateGraph();
        });
    }
    
    /**
     * Show color picker dialog
     */
    private void showColorPicker() {
        Color newColor = JColorChooser.showDialog(
            this,
            "Choose Function Color",
            function.getColor()
        );
        
        if (newColor != null) {
            // Color is immutable in PlottableFunction, so we need to recreate the function
            // For now, just update the UI indicator
            colorIndicator.setBackground(newColor);
            // Signal that the function needs to be recreated
            parent.updateGraph();
        }
    }
    
    @Override
    protected boolean updateFromEdit(String newExpression) {
        // PlottableFunctions are immutable, so any edit requires recreation
        // Update the expression field and signal recreation is needed
        setExpression(newExpression);
        return false; // Signal recreation needed
    }
    
    @Override
    public Function getFunction() {
        return function;
    }
    
    /**
     * Get the underlying PlottableFunction object
     * @return The PlottableFunction
     */
    public PlottableFunction getPlottableFunction() {
        return function;
    }
    
    /**
     * Update the function object (used when expression hasn't changed but
     * other properties might have)
     * @param newFunction The new PlottableFunction
     */
    public void setFunction(PlottableFunction newFunction) {
        this.function = newFunction;
        colorIndicator.setBackground(newFunction.getColor());
        enableCheckbox.setSelected(newFunction.isEnabled());
    }
    
    /**
     * Get the current color of the function
     * @return Color object
     */
    public Color getColor() {
        return function.getColor();
    }
    
    /**
     * Check if the function is enabled
     * @return true if enabled
     */
    public boolean isEnabled() {
        return function.isEnabled();
    }
}
