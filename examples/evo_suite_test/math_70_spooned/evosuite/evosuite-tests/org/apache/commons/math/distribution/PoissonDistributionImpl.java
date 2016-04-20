package org.apache.commons.math.distribution;


public class PoissonDistributionImpl extends org.apache.commons.math.distribution.AbstractIntegerDistribution implements java.io.Serializable , org.apache.commons.math.distribution.PoissonDistribution {
	public static final int DEFAULT_MAX_ITERATIONS = 10000000;

	public static final double DEFAULT_EPSILON = 1.0E-12;

	private static final long serialVersionUID = -3349935121172596109L;

	private org.apache.commons.math.distribution.NormalDistribution normal;

	private double mean;

	private int maxIterations = DEFAULT_MAX_ITERATIONS;

	private double epsilon = DEFAULT_EPSILON;

	public PoissonDistributionImpl(double p) {
		this(p, new org.apache.commons.math.distribution.NormalDistributionImpl());
	}

	public PoissonDistributionImpl(double p ,double epsilon ,int maxIterations) {
		setMean(p);
		this.epsilon = epsilon;
		this.maxIterations = maxIterations;
	}

	public PoissonDistributionImpl(double p ,double epsilon) {
		setMean(p);
		this.epsilon = epsilon;
	}

	public PoissonDistributionImpl(double p ,int maxIterations) {
		setMean(p);
		this.maxIterations = maxIterations;
	}

	@java.lang.Deprecated
	public PoissonDistributionImpl(double p ,org.apache.commons.math.distribution.NormalDistribution z) {
		super();
		setNormalAndMeanInternal(z, p);
	}

	public double getMean() {
		return mean;
	}

	@java.lang.Deprecated
	public void setMean(double p) {
		setNormalAndMeanInternal(normal, p);
	}

	private void setNormalAndMeanInternal(org.apache.commons.math.distribution.NormalDistribution z, double p) {
		if (p <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("the Poisson mean must be positive ({0})", p);
		} 
		mean = p;
		normal = z;
		normal.setMean(p);
		normal.setStandardDeviation(java.lang.Math.sqrt(p));
	}

	public double probability(int x) {
		double ret;
		if ((x < 0) || (x == (java.lang.Integer.MAX_VALUE))) {
			ret = 0.0;
		} else {
			if (x == 0) {
				ret = java.lang.Math.exp(-(mean));
			} else {
				ret = (java.lang.Math.exp(((-(org.apache.commons.math.distribution.SaddlePointExpansion.getStirlingError(x))) - (org.apache.commons.math.distribution.SaddlePointExpansion.getDeviancePart(x, mean))))) / (java.lang.Math.sqrt(((org.apache.commons.math.util.MathUtils.TWO_PI) * x)));
			}
		}
		return ret;
	}

	@java.lang.Override
	public double cumulativeProbability(int x) throws org.apache.commons.math.MathException {
		if (x < 0) {
			return 0;
		} 
		if (x == (java.lang.Integer.MAX_VALUE)) {
			return 1;
		} 
		return org.apache.commons.math.special.Gamma.regularizedGammaQ((((double)(x)) + 1), mean, epsilon, maxIterations);
	}

	public double normalApproximateProbability(int x) throws org.apache.commons.math.MathException {
		return normal.cumulativeProbability((x + 0.5));
	}

	@java.lang.Override
	protected int getDomainLowerBound(double p) {
		return 0;
	}

	@java.lang.Override
	protected int getDomainUpperBound(double p) {
		return java.lang.Integer.MAX_VALUE;
	}

	@java.lang.Deprecated
	public void setNormal(org.apache.commons.math.distribution.NormalDistribution value) {
		setNormalAndMeanInternal(value, mean);
	}
}

