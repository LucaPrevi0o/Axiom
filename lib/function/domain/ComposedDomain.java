package lib.function.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Represents a domain that is the UNION of multiple sub-domains.
 * For example: (-∞, -1] ∪ [1, ∞) for functions like asec(x)
 * Or: (-∞, 0) ∪ (0, ∞) for functions like 1/x
 */
public class ComposedDomain extends Domain {
    
    private List<Domain> domains;

    /**
     * Constructor
     * @param domains The domains to compose (union)
     */
    public ComposedDomain(Domain... domains) {
        this.domains = List.of(domains);
    }

    /**
     * Check if an X value is in ANY of the sub-domains (union)
     * @param x The value to check
     * @return True if {@code x} is in at least one sub-domain
     */
    @Override
    public boolean contains(double x) {
        return domains.stream().anyMatch(domain -> domain.contains(x));
    }

    /**
     * Get the minimum bound of the composed domain (leftmost point)
     * @return The minimum bound across all sub-domains
     */
    @Override
    public double getMinBound() {
        return domains.stream()
            .mapToDouble(Domain::getMinBound)
            .min()
            .orElse(Double.NaN);
    }

    /**
     * Get the maximum bound of the composed domain (rightmost point)
     * @return The maximum bound across all sub-domains
     */
    @Override
    public double getMaxBound() {
        return domains.stream()
            .mapToDouble(Domain::getMaxBound)
            .max()
            .orElse(Double.NaN);
    }

    @Override
    public void setMinBound(double min) {
        throw new UnsupportedOperationException("Cannot set min bound on ComposedDomain");
    }

    @Override
    public void setMaxBound(double max) {
        throw new UnsupportedOperationException("Cannot set max bound on ComposedDomain");
    }

    /**
     * Get an array of sample points from all sub-domains.
     * Returns the union of sample points from each sub-domain, properly ordered.
     * 
     * @param viewMin The minimum of the view range
     * @param viewMax The maximum of the view range
     * @param numSamples The maximum number of sample points to return
     * @return The array of sample points from all sub-domains
     */
    @Override
    public double[] getSamplePoints(double viewMin, double viewMax, int numSamples) {
        if (numSamples <= 0) {
            throw new IllegalArgumentException("numSamples must be > 0");
        }
        
        // Calculate samples per sub-domain (distribute fairly)
        int samplesPerDomain = Math.max(10, numSamples / domains.size());
        
        // Collect all sample points from all sub-domains
        LinkedHashSet<Double> allPoints = new LinkedHashSet<>();
        
        for (Domain domain : domains) {
            double[] samples = domain.getSamplePoints(viewMin, viewMax, samplesPerDomain);
            for (double sample : samples) {
                if (sample >= viewMin && sample <= viewMax && !Double.isNaN(sample)) {
                    allPoints.add(sample);
                }
            }
        }
        
        // Convert to sorted list
        List<Double> sortedPoints = new ArrayList<>(allPoints);
        sortedPoints.sort(Double::compare);
        
        // If we have too many points, subsample
        if (sortedPoints.size() <= numSamples) {
            return sortedPoints.stream().mapToDouble(Double::doubleValue).toArray();
        }
        
        // Subsample to get exactly numSamples points
        double[] result = new double[numSamples];
        double step = (double)(sortedPoints.size() - 1) / (numSamples - 1);
        
        for (int i = 0; i < numSamples; i++) {
            int index = (int) Math.round(i * step);
            index = Math.max(0, Math.min(index, sortedPoints.size() - 1));
            result[i] = sortedPoints.get(index);
        }
        
        return result;
    }
    
    /**
     * Get the list of sub-domains
     * @return List of sub-domains
     */
    public List<Domain> getSubDomains() {
        return new ArrayList<>(domains);
    }
    
    /**
     * Get the number of sub-domains
     * @return Number of sub-domains in this union
     */
    public int getSubDomainCount() {
        return domains.size();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ComposedDomain[");
        for (int i = 0; i < domains.size(); i++) {
            if (i > 0) sb.append(" ∪ ");
            sb.append(domains.get(i).toString());
        }
        sb.append("]");
        return sb.toString();
    }
}