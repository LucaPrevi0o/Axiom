package lib.function.domains;

import lib.function.Domain;

public class IntervalDomain extends Domain {

    private double minBound;
    private double maxBound;

    /**
     * Constructor
     * @param minBound The minimum bound of the interval
     * @param maxBound The maximum bound of the interval
     */
    public IntervalDomain(double minBound, double maxBound) {
        
        this.minBound = minBound;
        this.maxBound = maxBound;
    }

    /**
     * Check if x is in the domain
     * @param x The x value to check
     * @return True if x is in the domain, false otherwise
     */
    @Override
    public boolean contains(double x) { return x >= minBound && x <= maxBound; }

    /**
     * Get the minimum bound of the domain
     * @return The minimum bound
     */
    @Override
    public double getMinBound() { return minBound; }

    /**
     * Get the maximum bound of the domain
     * @return The maximum bound
     */
    @Override
    public double getMaxBound() { return maxBound; }
    
}
