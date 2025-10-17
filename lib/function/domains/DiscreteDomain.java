package lib.function.domains;

import lib.function.Domain;

public class DiscreteDomain extends Domain {
    
    // Array di valori discreti
    private double[] values;

    /**
     * Constructor
     * @param values The array of discrete values
     */
    public DiscreteDomain(double[] values) { this.values = values; }

    /**
     * Get the array of discrete values
     * @return The array of discrete values
     */
    public double[] getValues() { return values; }

    /**
     * Check if x is in the domain
     * @param x The x value to check
     * @return True if x is in the domain, false otherwise
     */
    @Override
    public boolean contains(double x) {

        for (double val : values)
            if (Double.compare(val, x) == 0) return true;
        return false;
    }

    /**
     * Get the minimum bound of the domain
     * @return The minimum bound
     */
    @Override
    public double getMinBound() { return values[0]; }

    /**
     * Get the maximum bound of the domain
     * @return The maximum bound
     */
    @Override
    public double getMaxBound() { return values[values.length - 1]; }

    /**
     * Set the minimum bound of the domain (no-op for discrete domain)
     * @param min The new minimum bound
     */
    @Override
    public void setMinBound(double min) {}

    @Override
    public void setMaxBound(double max) {}

    @Override
    public double[] getSamplePoints(double viewMin, double viewMax, int numSamples) {

        if (values == null || values.length == 0) return new double[0];

        java.util.List<Double> list = new java.util.ArrayList<>();
        for (double v : values) {
            if (v >= viewMin && v <= viewMax) list.add(v);
        }

        double[] out = new double[list.size()];
        for (int i = 0; i < list.size(); i++) out[i] = list.get(i);
        return out;
    }
}
