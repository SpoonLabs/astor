package org.apache.commons.math.distribution;


public class NormalDistributionImpl extends org.apache.commons.math.distribution.AbstractContinuousDistribution implements java.io.Serializable , org.apache.commons.math.distribution.NormalDistribution {
	public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;

	private static final long serialVersionUID = 8589540077390120676L;

	private static final double SQRT2PI = java.lang.Math.sqrt((2 * (java.lang.Math.PI)));

	private double mean = 0;

	private double standardDeviation = 1;

	private final double solverAbsoluteAccuracy;

	public NormalDistributionImpl(double mean ,double sd) {
		this(mean, sd, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
	}

	public NormalDistributionImpl(double mean ,double sd ,double inverseCumAccuracy) {
		super();
		setMeanInternal(mean);
		setStandardDeviationInternal(sd);
		solverAbsoluteAccuracy = inverseCumAccuracy;
	}

	public NormalDistributionImpl() {
		this(0.0, 1.0);
	}

	public double getMean() {
		return mean;
	}

	@java.lang.Deprecated
	public void setMean(double mean) {
		setMeanInternal(mean);
	}

	private void setMeanInternal(double newMean) {
		this.mean = newMean;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	@java.lang.Deprecated
	public void setStandardDeviation(double sd) {
		setStandardDeviationInternal(sd);
	}

	private void setStandardDeviationInternal(double sd) {
		if (sd <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("standard deviation must be positive ({0})", sd);
		} 
		standardDeviation = sd;
	}

	public double density(java.lang.Double x) {
		return density(x.doubleValue());
	}

	public double density(double x) {
		double x0 = x - (mean);
		return (java.lang.Math.exp((((-x0) * x0) / ((2 * (standardDeviation)) * (standardDeviation))))) / ((standardDeviation) * (SQRT2PI));
	}

	public double cumulativeProbability(double x) throws org.apache.commons.math.MathException {
		try {
			return 0.5 * (1.0 + (org.apache.commons.math.special.Erf.erf(((x - (mean)) / ((standardDeviation) * (java.lang.Math.sqrt(2.0)))))));
		} catch (org.apache.commons.math.MaxIterationsExceededException ex) {
			if (x < ((mean) - (20 * (standardDeviation)))) {
				return 0.0;
			} else if (x > ((mean) + (20 * (standardDeviation)))) {
				return 1.0;
			} else {
				throw ex;
			}
		}
	}

	@java.lang.Override
	protected double getSolverAbsoluteAccuracy() {
		return solverAbsoluteAccuracy;
	}

	@java.lang.Override
	public double inverseCumulativeProbability(final double p) throws org.apache.commons.math.MathException {
		if (p == 0) {
			return java.lang.Double.NEGATIVE_INFINITY;
		} 
		if (p == 1) {
			return java.lang.Double.POSITIVE_INFINITY;
		} 
		return super.inverseCumulativeProbability(p);
	}

	@java.lang.Override
	protected double getDomainLowerBound(double p) {
		double ret;
		if (p < 0.5) {
			ret = -(java.lang.Double.MAX_VALUE);
		} else {
			ret = mean;
		}
		return ret;
	}

	@java.lang.Override
	protected double getDomainUpperBound(double p) {
		double ret;
		if (p < 0.5) {
			ret = mean;
		} else {
			ret = java.lang.Double.MAX_VALUE;
		}
		return ret;
	}

	@java.lang.Override
	protected double getInitialDomain(double p) {
		double ret;
		if (p < 0.5) {
			ret = (mean) - (standardDeviation);
		} else if (p > 0.5) {
			ret = (mean) + (standardDeviation);
		} else {
			ret = mean;
		}
		return ret;
	}
}

