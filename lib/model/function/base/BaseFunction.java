package lib.model.function.base;

/**
 * Base class for non-plottable function-like entities.
 * These are definitions that provide values but are not rendered on the graph.
 * 
 * Examples: Constants (with sliders), Sets (discrete value collections)
 */
public abstract class BaseFunction extends Function {
    
    /**
     * Create a base function with a name
     * @param name Function name (e.g., "a", "A", etc.)
     */
    protected BaseFunction(String name) {
        super(name);
    }
    
    @Override
    public final boolean isPlottable() {
        return false;
    }
    
}

