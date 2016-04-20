package org.apache.commons.math.distribution;


public class ExponentialDistributionImpl extends org.apache.commons.math.distribution.AbstractContinuousDistribution implements java.io.Serializable , org.apache.commons.math.distribution.ExponentialDistribution {
	public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;

	private static final long serialVersionUID = 2401296428283614780L;

	private double mean;

	private final double solverAbsoluteAccuracy;

	public ExponentialDistributionImpl(double mean) {
		this(mean, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
	}

	public ExponentialDistributionImpl(double mean ,double inverseCumAccuracy) {
		super();
		setMeanInternal(mean);
		solverAbsoluteAccuracy = inverseCumAccuracy;
	}

	@java.lang.Deprecated
	public void setMean(double mean) {
		setMeanInternal(mean);
	}

	private void setMeanInternal(double newMean) {
		if (newMean <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("mean must be positive ({0})", newMean);
		} 
		this.mean = newMean;
	}

	public double getMean() {
		return mean;
	}

	public double density(java.lang.Double x) {
		return density(x.doubleValue());
	}

	@java.lang.Override
	public double density(double x) {
		if (x < 0) {
			return 0;
		} 
		return (java.lang.Math.exp(((-x) / (mean)))) / (mean);
	}

	public double cumulativeProbability(double x) throws org.apache.commons.math.MathException {
		double ret;
		if (x <= 0.0) {
			ret = 0.0;
		} else {
			ret = 1.0 - (java.lang.Math.exp(((-x) / (mean))));
		}
		return ret;
	}

	@java.lang.Override
	public double inverseCumulativeProbability(double p) throws org.apache.commons.math.MathException {
		double ret;
		if ((p < 0.0) || (p > 1.0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", p, 0.0, 1.0);
		} else if (p == 1.0) {
			ret = java.lang.Double.POSITIVE_INFINITY;
		} else {
			ret = (-(mean)) * (java.lang.Math.log((1.0 - p)));
		}
		return ret;
	}

	@java.lang.Override
	protected double getDomainLowerBound(double p) {
		return 0;
	}

	@java.lang.Override
	protected double getDomainUpperBound(double p) {
		if (p < 0.5) {
			return mean;
		} else {
			return java.lang.Double.MAX_VALUE;
		}
	}

	@java.lang.Override
	protected double getInitialDomain(double p) {
		if (p < 0.5) {
			return (mean) * 0.5;
		} else {
			return mean;
		}
	}

	@java.lang.Override
	protected double getSolverAbsoluteAccuracy() {
		return solverAbsoluteAccuracy;
	}
}

