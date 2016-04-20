package org.apache.commons.math.distribution;


public class TDistributionImpl extends org.apache.commons.math.distribution.AbstractContinuousDistribution implements java.io.Serializable , org.apache.commons.math.distribution.TDistribution {
	public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;

	private static final long serialVersionUID = -5852615386664158222L;

	private double degreesOfFreedom;

	private final double solverAbsoluteAccuracy;

	public TDistributionImpl(double degreesOfFreedom ,double inverseCumAccuracy) {
		super();
		setDegreesOfFreedomInternal(degreesOfFreedom);
		solverAbsoluteAccuracy = inverseCumAccuracy;
	}

	public TDistributionImpl(double degreesOfFreedom) {
		this(degreesOfFreedom, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
	}

	@java.lang.Deprecated
	public void setDegreesOfFreedom(double degreesOfFreedom) {
		setDegreesOfFreedomInternal(degreesOfFreedom);
	}

	private void setDegreesOfFreedomInternal(double newDegreesOfFreedom) {
		if (newDegreesOfFreedom <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("degrees of freedom must be positive ({0})", newDegreesOfFreedom);
		} 
		this.degreesOfFreedom = newDegreesOfFreedom;
	}

	public double getDegreesOfFreedom() {
		return degreesOfFreedom;
	}

	@java.lang.Override
	public double density(double x) {
		final double n = degreesOfFreedom;
		final double nPlus1Over2 = (n + 1) / 2;
		return java.lang.Math.exp(((((org.apache.commons.math.special.Gamma.logGamma(nPlus1Over2)) - (0.5 * ((java.lang.Math.log(java.lang.Math.PI)) + (java.lang.Math.log(n))))) - (org.apache.commons.math.special.Gamma.logGamma((n / 2)))) - (nPlus1Over2 * (java.lang.Math.log((1 + ((x * x) / n)))))));
	}

	public double cumulativeProbability(double x) throws org.apache.commons.math.MathException {
		double ret;
		if (x == 0.0) {
			ret = 0.5;
		} else {
			double t = org.apache.commons.math.special.Beta.regularizedBeta(((degreesOfFreedom) / ((degreesOfFreedom) + (x * x))), (0.5 * (degreesOfFreedom)), 0.5);
			if (x < 0.0) {
				ret = 0.5 * t;
			} else {
				ret = 1.0 - (0.5 * t);
			}
		}
		return ret;
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
		return -(java.lang.Double.MAX_VALUE);
	}

	@java.lang.Override
	protected double getDomainUpperBound(double p) {
		return java.lang.Double.MAX_VALUE;
	}

	@java.lang.Override
	protected double getInitialDomain(double p) {
		return 0.0;
	}

	@java.lang.Override
	protected double getSolverAbsoluteAccuracy() {
		return solverAbsoluteAccuracy;
	}
}

