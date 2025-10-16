package lib.panel.entry.entries;

import lib.function.functions.ExpressionFunction;
import lib.panel.entry.PlottableFunctionEntry;

public class ExpressionFunctionEntry extends PlottableFunctionEntry<ExpressionFunction> {

    /**
     * Constructor
     * @param function The ExpressionFunction object
     * @param onVisibilityChanged Callback when visibility is toggled
     * @param onRemove Callback when remove button is clicked
     * @param onEdit Callback when edit button is clicked
     */
    public ExpressionFunctionEntry(ExpressionFunction function, Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {

        super(function, onVisibilityChanged, onRemove, onEdit);
    }
}
