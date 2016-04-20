package org.apache.commons.math.distribution;


public class FDistributionImpl extends org.apache.commons.math.distribution.AbstractContinuousDistribution implements java.io.Serializable , org.apache.commons.math.distribution.FDistribution {
	public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;

	private static final java.lang.String NON_POSITIVE_DEGREES_OF_FREEDOM_MESSAGE = "degrees of freedom must be positive ({0})";

	private static final long serialVersionUID = -8516354193418641566L;

	private double numeratorDegreesOfFreedom;

	private double denominatorDegreesOfFreedom;

	private final double solverAbsoluteAccuracy;

	public FDistributionImpl(double numeratorDegreesOfFreedom ,double denominatorDegreesOfFreedom) {
		this(numeratorDegreesOfFreedom, denominatorDegreesOfFreedom, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
	}

	public FDistributionImpl(double numeratorDegreesOfFreedom ,double denominatorDegreesOfFreedom ,double inverseCumAccuracy) {
		super();
		setNumeratorDegreesOfFreedomInternal(numeratorDegreesOfFreedom);
		setDenominatorDegreesOfFreedomInternal(denominatorDegreesOfFreedom);
		solverAbsoluteAccuracy = inverseCumAccuracy;
	}

	@java.lang.Override
	public double density(double x) {
		final double nhalf = (numeratorDegreesOfFreedom) / 2;
		final double mhalf = (denominatorDegreesOfFreedom) / 2;
		final double logx = java.lang.Math.log(x);
		final double logn = java.lang.Math.log(numeratorDegreesOfFreedom);
		final double logm = java.lang.Math.log(denominatorDegreesOfFreedom);
		final double lognxm = java.lang.Math.log((((numeratorDegreesOfFreedom) * x) + (denominatorDegreesOfFreedom)));
		return java.lang.Math.exp((((((((nhalf * logn) + (nhalf * logx)) - logx) + (mhalf * logm)) - (nhalf * lognxm)) - (mhalf * lognxm)) - (org.apache.commons.math.special.Beta.logBeta(nhalf, mhalf))));
	}

	public double cumulativeProbability(double x) throws org.apache.commons.math.MathException {
		double ret;
		if (x <= 0.0) {
			ret = 0.0;
		} else {
			double n = numeratorDegreesOfFreedom;
			double m = denominatorDegreesOfFreedom;
			ret = org.apache.commons.math.special.Beta.regularizedBeta(((n * x) / (m + (n * x))), (0.5 * n), (0.5 * m));
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

	@java.lang.Override
	protected double getDomainLowerBound(double p) {
		return 0.0;
	}

	@java.lang.Override
	protected double getDomainUpperBound(double p) {
		return java.lang.Double.MAX_VALUE;
	}

	@java.lang.Override
	protected double getInitialDomain(double p) {
		double ret = 1.0;
		double d = denominatorDegreesOfFreedom;
		if (d > 2.0) {
			ret = d / (d - 2.0);
		} 
		return ret;
	}

	@java.lang.Deprecated
	public void setNumeratorDegreesOfFreedom(double degreesOfFreedom) {
		setNumeratorDegreesOfFreedomInternal(degreesOfFreedom);
	}

	private void setNumeratorDegreesOfFreedomInternal(double degreesOfFreedom) {
		if (degreesOfFreedom <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NON_POSITIVE_DEGREES_OF_FREEDOM_MESSAGE, degreesOfFreedom);
		} 
		this.numeratorDegreesOfFreedom = degreesOfFreedom;
	}

	public double getNumeratorDegreesOfFreedom() {
		return numeratorDegreesOfFreedom;
	}

	@java.lang.Deprecated
	public void setDenominatorDegreesOfFreedom(double degreesOfFreedom) {
		setDenominatorDegreesOfFreedomInternal(degreesOfFreedom);
	}

	private void setDenominatorDegreesOfFreedomInternal(double degreesOfFreedom) {
		if (degreesOfFreedom <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NON_POSITIVE_DEGREES_OF_FREEDOM_MESSAGE, degreesOfFreedom);
		} 
		this.denominatorDegreesOfFreedom = degreesOfFreedom;
	}

	public double getDenominatorDegreesOfFreedom() {
		return denominatorDegreesOfFreedom;
	}

	@java.lang.Override
	protected double getSolverAbsoluteAccuracy() {
		return solverAbsoluteAccuracy;
	}
}

