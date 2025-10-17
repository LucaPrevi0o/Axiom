package lib.function;

import lib.function.domains.IntervalDomain;

public abstract class Domain {
    
    /**
     * Parse a function expression to determine its domain
     * @param expression The function expression as a string
     * @return The parsed domain
     */
    public static Domain parse(String expression) {

        String trimmed = expression.trim().toLowerCase();
        switch (trimmed) {
            case "sin":
            case "cos":
            case "tan":
            case "csc":
            case "sec":
            case "cot":
            case "sinh":
            case "cosh":
            case "tanh":
            case "csch":
            case "sech":
            case "coth":
                return new IntervalDomain(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            case "arcsin":
            case "arccos":
            case "arccsc":
            case "arcsec":
            case "arccot":
            case "arsinh":
                return new IntervalDomain(-1, 1);
            case "arctan":
            case "arcoth":
            case "arcosh":
                return new IntervalDomain(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            case "log":
            case "ln":
            case "sqrt":
                return new IntervalDomain(0, Double.POSITIVE_INFINITY);
            default:
                return new IntervalDomain(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
    }
    
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
    
    /**
     * Set the minimum bound of the domain
     * @param min The new minimum bound
     */
    public abstract void setMinBound(double min);

    /**
     * Set the maximum bound of the domain
     * @param max The new maximum bound
     */
    public abstract void setMaxBound(double max);

    /**
     * Get an array of sample X points inside this domain clipped to the
     * provided view range. Implementations should return points ordered
     * from smallest to largest. For discrete domains this should return the
     * discrete values inside the intersection. For interval domains this
     * should generate up to numSamples uniformly spaced points inside the
     * intersection of this domain and the view range.
     *
     * @param viewMin Minimum X value of the current view
     * @param viewMax Maximum X value of the current view
     * @param numSamples Suggested maximum number of samples (e.g. panel width)
     * @return Array of X sample points (may be empty)
     */
    public abstract double[] getSamplePoints(double viewMin, double viewMax, int numSamples);

    @Override
    public String toString() { return "Domain[" + getMinBound() + ", " + getMaxBound() + "]"; }
}
