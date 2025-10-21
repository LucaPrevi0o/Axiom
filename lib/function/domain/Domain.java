package lib.function.domain;

import lib.function.domain.domains.IntervalDomain;

public abstract class Domain {
    
    /**
     * Parse a function expression to determine its domain
     * @param expression The function expression as a string
     * @return The parsed domain
     */
    public static Domain parse(String expression) {

        String trimmed = expression.trim().toLowerCase();
        switch (trimmed) {
            default:
                return new IntervalDomain(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
    }
    
    /**
     * Check if an X value is in the domain
     * @param x The value to check
     * @return True if {@code x} is in the domain, false otherwise
     */
    public abstract boolean contains(double x);
    
    /**
     * Get the minimum and maximum bounds of the domain
     * @return The minimum bound
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
     * Get an array of sample points inside this domain clipped to the
     * provided view range. Implementations should return points ordered
     * from smallest to largest.
     * For discrete domains this should return the
     * discrete values inside the intersection. For interval domains this
     * should generate up to {@code numSamples} uniformly spaced points inside the
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
