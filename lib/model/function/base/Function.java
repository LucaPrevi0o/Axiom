package lib.model.function.base;

/**
 * Root abstract class for all elements that can be added through "Add Function" button.
 * This includes both plottable elements (functions, points, equations, regions) 
 * and non-plottable definitions (constants, sets).
 * 
 * Every element in the system is a Function.
 */
public abstract class Function {
    
    protected final String name;
    protected boolean enabled;
    
    /**
     * Create a function with a name
     * @param name Function name (e.g., "f", "a", or null for anonymous)
     */
    protected Function(String name) {
        this.name = name;
        this.enabled = true;
    }
    
    /**
     * Get the function name (may be null for anonymous functions)
     */
    public String getName() {
        return name;
    }
    
    /**
     * Check if function is enabled for display
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Set whether function is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Get a display string for this function (for UI)
     * @return Human-readable representation
     */
    public abstract String getDisplayString();
    
    /**
     * Check if this function can be plotted on the graph
     * @return true if plottable, false if it's a definition only
     */
    public abstract boolean isPlottable();
    
}
