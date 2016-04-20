package org.apache.commons.math.distribution;


public class HypergeometricDistributionImpl extends org.apache.commons.math.distribution.AbstractIntegerDistribution implements java.io.Serializable , org.apache.commons.math.distribution.HypergeometricDistribution {
	private static final long serialVersionUID = -436928820673516179L;

	private int numberOfSuccesses;

	private int populationSize;

	private int sampleSize;

	public HypergeometricDistributionImpl(int populationSize ,int numberOfSuccesses ,int sampleSize) {
		super();
		if (numberOfSuccesses > populationSize) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("number of successes ({0}) must be less than or equal to population size ({1})", numberOfSuccesses, populationSize);
		} 
		if (sampleSize > populationSize) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("sample size ({0}) must be less than or equal to population size ({1})", sampleSize, populationSize);
		} 
		setPopulationSizeInternal(populationSize);
		setSampleSizeInternal(sampleSize);
		setNumberOfSuccessesInternal(numberOfSuccesses);
	}

	@java.lang.Override
	public double cumulativeProbability(int x) {
		double ret;
		int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
		if (x < (domain[0])) {
			ret = 0.0;
		} else {
			if (x >= (domain[1])) {
				ret = 1.0;
			} else {
				ret = innerCumulativeProbability(domain[0], x, 1, populationSize, numberOfSuccesses, sampleSize);
			}
		}
		return ret;
	}

	private int[] getDomain(int n, int m, int k) {
		return new int[]{ getLowerDomain(n, m, k) , getUpperDomain(m, k) };
	}

	@java.lang.Override
	protected int getDomainLowerBound(double p) {
		return getLowerDomain(populationSize, numberOfSuccesses, sampleSize);
	}

	@java.lang.Override
	protected int getDomainUpperBound(double p) {
		return getUpperDomain(sampleSize, numberOfSuccesses);
	}

	private int getLowerDomain(int n, int m, int k) {
		return java.lang.Math.max(0, (m - (n - k)));
	}

	public int getNumberOfSuccesses() {
		return numberOfSuccesses;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	private int getUpperDomain(int m, int k) {
		return java.lang.Math.min(k, m);
	}

	public double probability(int x) {
		double ret;
		int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
		if ((x < (domain[0])) || (x > (domain[1]))) {
			ret = 0.0;
		} else {
			double p = ((double)(sampleSize)) / ((double)(populationSize));
			double q = ((double)(((populationSize) - (sampleSize)))) / ((double)(populationSize));
			double p1 = org.apache.commons.math.distribution.SaddlePointExpansion.logBinomialProbability(x, numberOfSuccesses, p, q);
			double p2 = org.apache.commons.math.distribution.SaddlePointExpansion.logBinomialProbability(((sampleSize) - x), ((populationSize) - (numberOfSuccesses)), p, q);
			double p3 = org.apache.commons.math.distribution.SaddlePointExpansion.logBinomialProbability(sampleSize, populationSize, p, q);
			ret = java.lang.Math.exp(((p1 + p2) - p3));
		}
		return ret;
	}

	private double probability(int n, int m, int k, int x) {
		return java.lang.Math.exp((((org.apache.commons.math.util.MathUtils.binomialCoefficientLog(m, x)) + (org.apache.commons.math.util.MathUtils.binomialCoefficientLog((n - m), (k - x)))) - (org.apache.commons.math.util.MathUtils.binomialCoefficientLog(n, k))));
	}

	@java.lang.Deprecated
	public void setNumberOfSuccesses(int num) {
		setNumberOfSuccessesInternal(num);
	}

	private void setNumberOfSuccessesInternal(int num) {
		if (num < 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("number of successes must be non-negative ({0})", num);
		} 
		numberOfSuccesses = num;
	}

	@java.lang.Deprecated
	public void setPopulationSize(int size) {
		setPopulationSizeInternal(size);
	}

	private void setPopulationSizeInternal(int size) {
		if (size <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("population size must be positive ({0})", size);
		} 
		populationSize = size;
	}

	@java.lang.Deprecated
	public void setSampleSize(int size) {
		setSampleSizeInternal(size);
	}

	private void setSampleSizeInternal(int size) {
		if (size < 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("sample size must be positive ({0})", size);
		} 
		sampleSize = size;
	}

	public double upperCumulativeProbability(int x) {
		double ret;
		final int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
		if (x < (domain[0])) {
			ret = 1.0;
		} else {
			if (x > (domain[1])) {
				ret = 0.0;
			} else {
				ret = innerCumulativeProbability(domain[1], x, -1, populationSize, numberOfSuccesses, sampleSize);
			}
		}
		return ret;
	}

	private double innerCumulativeProbability(int x0, int x1, int dx, int n, int m, int k) {
		double ret = probability(n, m, k, x0);
		while (x0 != x1) {
			x0 += dx;
			ret += probability(n, m, k, x0);
		}
		return ret;
	}
}

