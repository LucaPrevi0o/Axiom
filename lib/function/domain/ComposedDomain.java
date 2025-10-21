package lib.function.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ComposedDomain extends Domain {
    
    private List<Domain> domains;

    /**
     * Constructor
     * @param domains The domains to compose
     */
    public ComposedDomain(Domain... domains) {
        this.domains = List.of(domains);
    }

    /**
     * Check if an X value is in the domain
     * @param x The value to check
     * @return True if {@code x} is in the domain, false otherwise
     */
    @Override
    public boolean contains(double x) {
        return domains.stream().anyMatch(domain -> domain.contains(x));
    }

    /**
     * Get the minimum bound of the composed domain
     * @return The minimum bound
     */
    @Override
    public double getMinBound() {
        return domains.stream().mapToDouble(Domain::getMinBound).max().orElse(Double.NaN);
    }

    /**
     * Get the maximum bound of the composed domain
     * @return The maximum bound
     */
    @Override
    public double getMaxBound() {
        return domains.stream().mapToDouble(Domain::getMaxBound).min().orElse(Double.NaN);
    }

    @Override
    public void setMinBound(double min) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMinBound'");
    }

    @Override
    public void setMaxBound(double max) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMaxBound'");
    }

    /**
     * Get an array of sample points inside this domain clipped to the
     * provided view range. Implementations should return points ordered
     * from smallest to largest.
     * For discrete domains this should return the
     * discrete values inside the intersection. For interval domains this
     * should generate up to {@code numSamples} uniformly spaced points inside the
     * intersection of this domain and the view range.
     * @param viewMin The minimum of the view range
     * @param viewMax The maximum of the view range
     * @param numSamples The maximum number of sample points to return
     * @return The array of sample points
     */
    @Override
    public double[] getSamplePoints(double viewMin, double viewMax, int numSamples) {

        if (numSamples <= 0) throw new IllegalArgumentException("numSamples must be > 0");
        LinkedHashSet<Double> set = new LinkedHashSet<>();
        for (Domain domain : domains) {

            double[] samples = domain.getSamplePoints(viewMin, viewMax, numSamples);
            for (double s : samples)
                if (s >= viewMin && s <= viewMax && !Double.isNaN(s)) set.add(s);
        }

        List<Double> result = new ArrayList<>(set);
        result.sort(Double::compare);
        if (result.size() <= numSamples) return result.stream().mapToDouble(Double::doubleValue).toArray();

        double[] sampled = new double[numSamples];
        double step = (double)(result.size() - 1) / (numSamples - 1);
        for (int i = 0; i < numSamples; i++) {

            int index = (int)Math.round(i * step);
            index = Math.max(0, Math.min(index, result.size() - 1));
            sampled[i] = result.get(index);
        }
        return sampled;
    }
}
