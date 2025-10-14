package lib.panel.entry.entries;

import lib.function.functions.RangeFunction;
import lib.panel.entry.FunctionEntry;

public class RangeFunctionEntry extends FunctionEntry<RangeFunction> {

    /**
     * Constructor
     * @param function The RangeFunction object
     * @param onVisibilityChanged Callback when visibility is toggled
     * @param onRemove Callback when remove button is clicked
     * @param onEdit Callback when edit button is clicked
     */
    public RangeFunctionEntry(RangeFunction function, Runnable onVisibilityChanged, Runnable onRemove, Runnable onEdit) {
        super(function, onVisibilityChanged, onRemove, onEdit);
    }
}
    