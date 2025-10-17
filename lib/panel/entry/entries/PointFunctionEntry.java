package lib.panel.entry.entries;

import lib.function.functions.PointFunction;
import lib.panel.entry.PlottableFunctionEntry;

public class PointFunctionEntry extends PlottableFunctionEntry<PointFunction> {

    /**
     * Constructor
     * @param function The PointFunction object
     * @param onVisibilityChanged Callback when visibility is toggled
     * @param onRemove Callback when remove button is clicked
     * @param onEdit Callback when edit button is clicked
     */
    public PointFunctionEntry(PointFunction function, Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {
        super(function, onVisibilityChanged, onRemove, onEdit);
    }

    /**
     * Update the expression label to reflect the current function expression
     */
    @Override
    protected String getExpressionLabel() { return (function.getName() == null ? "" : function.getName() + " = ") + function.getExpression(); }
}
