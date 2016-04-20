package org.apache.commons.math.distribution;


public class ChiSquaredDistributionImpl extends org.apache.commons.math.distribution.AbstractContinuousDistribution implements java.io.Serializable , org.apache.commons.math.distribution.ChiSquaredDistribution {
	public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;

	private static final long serialVersionUID = -8352658048349159782L;

	private org.apache.commons.math.distribution.GammaDistribution gamma;

	private final double solverAbsoluteAccuracy;

	public ChiSquaredDistributionImpl(double df) {
		this(df, new org.apache.commons.math.distribution.GammaDistributionImpl((df / 2.0) , 2.0));
	}

	@java.lang.Deprecated
	public ChiSquaredDistributionImpl(double df ,org.apache.commons.math.distribution.GammaDistribution g) {
		super();
		setGammaInternal(g);
		setDegreesOfFreedomInternal(df);
		solverAbsoluteAccuracy = DEFAULT_INVERSE_ABSOLUTE_ACCURACY;
	}

	public ChiSquaredDistributionImpl(double df ,double inverseCumAccuracy) {
		super();
		gamma = new org.apache.commons.math.distribution.GammaDistributionImpl((df / 2.0) , 2.0);
		setDegreesOfFreedomInternal(df);
		solverAbsoluteAccuracy = inverseCumAccuracy;
	}

	@java.lang.Deprecated
	public void setDegreesOfFreedom(double degreesOfFreedom) {
		setDegreesOfFreedomInternal(degreesOfFreedom);
	}

	private void setDegreesOfFreedomInternal(double degreesOfFreedom) {
		gamma.setAlpha((degreesOfFreedom / 2.0));
	}

	public double getDegreesOfFreedom() {
		return (gamma.getAlpha()) * 2.0;
	}

	public double density(java.lang.Double x) {
		return density(x.doubleValue());
	}

	@java.lang.Override
	public double density(double x) {
		return gamma.density(x);
	}

	public double cumulativeProbability(double x) throws org.apache.commons.math.MathException {
		return gamma.cumulativeProbability(x);
	}

	@java.lang.Override
	public double inverseCumulativeProbability(final double p) throws org.apache.commons.math.MathException {
		if (p == 0) {
			return 0.0;
		} 
		if (p == 1) {
			return java.lang.Double.POSITIVE_INFINITY;
		} 
		return super.inverseCumulativeProbability(p);
	}

	@java.lang.Override
	protected double getDomainLowerBound(double p) {
		return (java.lang.Double.MIN_VALUE) * (gamma.getBeta());
	}

	@java.lang.Override
	protected double getDomainUpperBound(double p) {
		double ret;
		if (p < 0.5) {
			ret = getDegreesOfFreedom();
		} else {
			ret = java.lang.Double.MAX_VALUE;
		}
		return ret;
	}

	@java.lang.Override
	protected double getInitialDomain(double p) {
		double ret;
		if (p < 0.5) {
			ret = (getDegreesOfFreedom()) * 0.5;
		} else {
			ret = getDegreesOfFreedom();
		}
		return ret;
	}

	@java.lang.Deprecated
	public void setGamma(org.apache.commons.math.distribution.GammaDistribution g) {
		setGammaInternal(g);
	}

	private void setGammaInternal(org.apache.commons.math.distribution.GammaDistribution g) {
		this.gamma = g;
	}

	@java.lang.Override
	protected double getSolverAbsoluteAccuracy() {
		return solverAbsoluteAccuracy;
	}
}

