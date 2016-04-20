package org.apache.commons.math.distribution;


public class WeibullDistributionImpl extends org.apache.commons.math.distribution.AbstractContinuousDistribution implements java.io.Serializable , org.apache.commons.math.distribution.WeibullDistribution {
	public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;

	private static final long serialVersionUID = 8589540077390120676L;

	private double shape;

	private double scale;

	private final double solverAbsoluteAccuracy;

	public WeibullDistributionImpl(double alpha ,double beta) {
		this(alpha, beta, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
	}

	public WeibullDistributionImpl(double alpha ,double beta ,double inverseCumAccuracy) {
		super();
		setShapeInternal(alpha);
		setScaleInternal(beta);
		solverAbsoluteAccuracy = inverseCumAccuracy;
	}

	public double cumulativeProbability(double x) {
		double ret;
		if (x <= 0.0) {
			ret = 0.0;
		} else {
			ret = 1.0 - (java.lang.Math.exp(-(java.lang.Math.pow((x / (scale)), shape))));
		}
		return ret;
	}

	public double getShape() {
		return shape;
	}

	public double getScale() {
		return scale;
	}

	@java.lang.Override
	public double density(double x) {
		if (x < 0) {
			return 0;
		} 
		final double xscale = x / (scale);
		final double xscalepow = java.lang.Math.pow(xscale, ((shape) - 1));
		final double xscalepowshape = xscalepow * xscale;
		return (((shape) / (scale)) * xscalepow) * (java.lang.Math.exp(-xscalepowshape));
	}

	@java.lang.Override
	public double inverseCumulativeProbability(double p) {
		double ret;
		if ((p < 0.0) || (p > 1.0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", p, 0.0, 1.0);
		} else if (p == 0) {
			ret = 0.0;
		} else if (p == 1) {
			ret = java.lang.Double.POSITIVE_INFINITY;
		} else {
			ret = (scale) * (java.lang.Math.pow(-(java.lang.Math.log((1.0 - p))), (1.0 / (shape))));
		}
		return ret;
	}

	@java.lang.Deprecated
	public void setShape(double alpha) {
		setShapeInternal(alpha);
	}

	private void setShapeInternal(double alpha) {
		if (alpha <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("shape must be positive ({0})", alpha);
		} 
		this.shape = alpha;
	}

	@java.lang.Deprecated
	public void setScale(double beta) {
		setScaleInternal(beta);
	}

	private void setScaleInternal(double beta) {
		if (beta <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("scale must be positive ({0})", beta);
		} 
		this.scale = beta;
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
		return java.lang.Math.pow(((scale) * (java.lang.Math.log(2.0))), (1.0 / (shape)));
	}

	@java.lang.Override
	protected double getSolverAbsoluteAccuracy() {
		return solverAbsoluteAccuracy;
	}
}

