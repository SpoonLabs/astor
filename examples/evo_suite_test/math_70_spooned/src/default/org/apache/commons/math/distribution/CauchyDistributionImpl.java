package org.apache.commons.math.distribution;


public class CauchyDistributionImpl extends org.apache.commons.math.distribution.AbstractContinuousDistribution implements java.io.Serializable , org.apache.commons.math.distribution.CauchyDistribution {
	public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;

	private static final long serialVersionUID = 8589540077390120676L;

	private double median = 0;

	private double scale = 1;

	private final double solverAbsoluteAccuracy;

	public CauchyDistributionImpl() {
		this(0.0, 1.0);
	}

	public CauchyDistributionImpl(double median ,double s) {
		this(median, s, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
	}

	public CauchyDistributionImpl(double median ,double s ,double inverseCumAccuracy) {
		super();
		setMedianInternal(median);
		setScaleInternal(s);
		solverAbsoluteAccuracy = inverseCumAccuracy;
	}

	public double cumulativeProbability(double x) {
		return 0.5 + ((java.lang.Math.atan(((x - (median)) / (scale)))) / (java.lang.Math.PI));
	}

	public double getMedian() {
		return median;
	}

	public double getScale() {
		return scale;
	}

	@java.lang.Override
	public double density(double x) {
		final double dev = x - (median);
		return (1 / (java.lang.Math.PI)) * ((scale) / ((dev * dev) + ((scale) * (scale))));
	}

	@java.lang.Override
	public double inverseCumulativeProbability(double p) {
		double ret;
		if ((p < 0.0) || (p > 1.0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", p, 0.0, 1.0);
		} else if (p == 0) {
			ret = java.lang.Double.NEGATIVE_INFINITY;
		} else if (p == 1) {
			ret = java.lang.Double.POSITIVE_INFINITY;
		} else {
			ret = (median) + ((scale) * (java.lang.Math.tan(((java.lang.Math.PI) * (p - 0.5)))));
		}
		return ret;
	}

	@java.lang.Deprecated
	public void setMedian(double median) {
		setMedianInternal(median);
	}

	private void setMedianInternal(double newMedian) {
		this.median = newMedian;
	}

	@java.lang.Deprecated
	public void setScale(double s) {
		setScaleInternal(s);
	}

	private void setScaleInternal(double s) {
		if (s <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("scale must be positive ({0})", s);
		} 
		scale = s;
	}

	@java.lang.Override
	protected double getDomainLowerBound(double p) {
		double ret;
		if (p < 0.5) {
			ret = -(java.lang.Double.MAX_VALUE);
		} else {
			ret = median;
		}
		return ret;
	}

	@java.lang.Override
	protected double getDomainUpperBound(double p) {
		double ret;
		if (p < 0.5) {
			ret = median;
		} else {
			ret = java.lang.Double.MAX_VALUE;
		}
		return ret;
	}

	@java.lang.Override
	protected double getInitialDomain(double p) {
		double ret;
		if (p < 0.5) {
			ret = (median) - (scale);
		} else if (p > 0.5) {
			ret = (median) + (scale);
		} else {
			ret = median;
		}
		return ret;
	}

	@java.lang.Override
	protected double getSolverAbsoluteAccuracy() {
		return solverAbsoluteAccuracy;
	}
}

