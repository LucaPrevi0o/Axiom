package lib.ui.component.entry;

import lib.model.function.base.BaseFunction;
import lib.model.function.base.Function;
import lib.ui.panel.FunctionPanel;
import javax.swing.*;

/**
 * Entry for non-plottable functions (definitions, sets, implicit equations).
 * These entries don't have visual controls like color pickers or enable checkboxes.
 */
public class BaseFunctionEntry extends AbstractFunctionEntry {
    
    private BaseFunction function;
    
    /**
     * Constructor for non-plottable function entries
     * @param expression Expression string
     * @param function The BaseFunction to display
     * @param parent Parent FunctionPanel
     */
    public BaseFunctionEntry(String expression, BaseFunction function, FunctionPanel parent) {
        super(expression, parent);
        this.function = function;
    }
    
    @Override
    protected void layoutSpecificComponents(JPanel topPanel) {
        // No specific controls for base functions
        // Could add an icon or label indicating function type here if desired
    }
    
    @Override
    protected boolean updateFromEdit(String newExpression) {
        // For base functions, any change might require recreation
        // (could change from definition to set, etc.)
        return false;
    }
    
    @Override
    public Function getFunction() {
        return function;
    }
    
    /**
     * Get the underlying BaseFunction object
     * @return The BaseFunction
     */
    public BaseFunction getBaseFunction() {
        return function;
    }
    
    /**
     * Update the function object (used when expression hasn't changed but
     * other properties might have)
     * @param newFunction The new BaseFunction
     */
    public void setFunction(BaseFunction newFunction) {
        this.function = newFunction;
    }
}
