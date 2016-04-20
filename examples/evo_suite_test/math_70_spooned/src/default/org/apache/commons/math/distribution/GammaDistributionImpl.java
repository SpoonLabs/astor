package org.apache.commons.math.distribution;


public class GammaDistributionImpl extends org.apache.commons.math.distribution.AbstractContinuousDistribution implements java.io.Serializable , org.apache.commons.math.distribution.GammaDistribution {
	public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;

	private static final long serialVersionUID = -3239549463135430361L;

	private double alpha;

	private double beta;

	private final double solverAbsoluteAccuracy;

	public GammaDistributionImpl(double alpha ,double beta) {
		this(alpha, beta, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
	}

	public GammaDistributionImpl(double alpha ,double beta ,double inverseCumAccuracy) {
		super();
		setAlphaInternal(alpha);
		setBetaInternal(beta);
		solverAbsoluteAccuracy = inverseCumAccuracy;
	}

	public double cumulativeProbability(double x) throws org.apache.commons.math.MathException {
		double ret;
		if (x <= 0.0) {
			ret = 0.0;
		} else {
			ret = org.apache.commons.math.special.Gamma.regularizedGammaP(alpha, (x / (beta)));
		}
		return ret;
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

	@java.lang.Deprecated
	public void setAlpha(double alpha) {
		setAlphaInternal(alpha);
	}

	private void setAlphaInternal(double newAlpha) {
		if (newAlpha <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("alpha must be positive ({0})", newAlpha);
		} 
		this.alpha = newAlpha;
	}

	public double getAlpha() {
		return alpha;
	}

	@java.lang.Deprecated
	public void setBeta(double newBeta) {
		setBetaInternal(newBeta);
	}

	private void setBetaInternal(double newBeta) {
		if (newBeta <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("beta must be positive ({0})", newBeta);
		} 
		this.beta = newBeta;
	}

	public double getBeta() {
		return beta;
	}

	@java.lang.Override
	public double density(double x) {
		if (x < 0)
			return 0;
		
		return (((java.lang.Math.pow((x / (beta)), ((alpha) - 1))) / (beta)) * (java.lang.Math.exp(((-x) / (beta))))) / (java.lang.Math.exp(org.apache.commons.math.special.Gamma.logGamma(alpha)));
	}

	public double density(java.lang.Double x) {
		return density(x.doubleValue());
	}

	@java.lang.Override
	protected double getDomainLowerBound(double p) {
		return java.lang.Double.MIN_VALUE;
	}

	@java.lang.Override
	protected double getDomainUpperBound(double p) {
		double ret;
		if (p < 0.5) {
			ret = (alpha) * (beta);
		} else {
			ret = java.lang.Double.MAX_VALUE;
		}
		return ret;
	}

	@java.lang.Override
	protected double getInitialDomain(double p) {
		double ret;
		if (p < 0.5) {
			ret = ((alpha) * (beta)) * 0.5;
		} else {
			ret = (alpha) * (beta);
		}
		return ret;
	}

	@java.lang.Override
	protected double getSolverAbsoluteAccuracy() {
		return solverAbsoluteAccuracy;
	}
}

