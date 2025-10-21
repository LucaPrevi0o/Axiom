package lib.function.domain.domains;

import lib.function.domain.Domain;

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

    /**
     * Set the minimum bound of the domain
     * @param min The new minimum bound
     */
    @Override
    public void setMinBound(double min) { this.minBound = min; }

    /**
     * Set the maximum bound of the domain
     * @param max The new maximum bound
     */
    @Override
    public void setMaxBound(double max) { this.maxBound = max; }
    
    /**
     * Get an array of sample points inside this domain clipped to the
     * provided view range. Implementations should return points ordered
     * from smallest to largest.
     * For discrete domains this should return the
     * discrete values inside the intersection. For interval domains this
     * should generate up to {@code numSamples} uniformly spaced points inside the
     * intersection of this domain and the view range.
     * @param viewMin Minimum X value of the current view
     * @param viewMax Maximum X value of the current view
     * @param numSamples The desired number of sample points
     * @return An array of sample points inside the domain and view range
     */
    @Override
    public double[] getSamplePoints(double viewMin, double viewMax, int numSamples) {

        // Compute intersection between domain and view
        double start = Math.max(minBound, viewMin);
        double end = Math.min(maxBound, viewMax);

        if (Double.isNaN(start) || Double.isNaN(end)) return new double[0];

        // If the domain is unbounded, clamp to the view range
        if (Double.isInfinite(minBound)) start = viewMin;
        if (Double.isInfinite(maxBound)) end = viewMax;

        if (Double.isInfinite(start) || Double.isInfinite(end) || start > end) return new double[0];

        int samples = Math.max(2, Math.min(numSamples, 10000));
        double[] points = new double[samples];
        double step = (end - start) / (samples - 1);
        for (int i = 0; i < samples; i++) points[i] = start + i * step;
        return points;
    }

}

