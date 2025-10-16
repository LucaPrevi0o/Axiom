package lib.function;

public abstract class Domain {
    
    /**
     * Check if x is in the domain
     * @param x The x value to check
     * @return True if x is in the domain, false otherwise
     */
    public abstract boolean contains(double x);
    
    /**
     * Get the minimum and maximum bounds of the domain
     * @return An array with [min, max] bounds
     */
    public abstract double getMinBound();

    /**
     * Get the maximum bound of the domain
     * @return The maximum bound
     */
    public abstract double getMaxBound();
}
