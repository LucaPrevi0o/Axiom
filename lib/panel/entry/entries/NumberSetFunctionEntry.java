package lib.panel.entry.entries;

import lib.function.functions.NumberSetFunction;
import lib.panel.entry.FunctionEntry;

public class NumberSetFunctionEntry extends FunctionEntry<NumberSetFunction> {

    /**
     * Constructor
     * @param function The NumberSetFunction object
     * @param onVisibilityChanged Callback when visibility is toggled
     * @param onRemove Callback when remove button is clicked
     * @param onEdit Callback when edit button is clicked
     */
    public NumberSetFunctionEntry(NumberSetFunction function, Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {
        super(function, onVisibilityChanged, onRemove, onEdit);
    }
    
}
