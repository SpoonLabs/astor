package org.apache.commons.math.distribution;


public class BetaDistributionImpl extends org.apache.commons.math.distribution.AbstractContinuousDistribution implements org.apache.commons.math.distribution.BetaDistribution {
	public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;

	private static final long serialVersionUID = -1221965979403477668L;

	private double alpha;

	private double beta;

	private double z;

	private final double solverAbsoluteAccuracy;

	public BetaDistributionImpl(double alpha ,double beta ,double inverseCumAccuracy) {
		this.alpha = alpha;
		this.beta = beta;
		z = java.lang.Double.NaN;
		solverAbsoluteAccuracy = inverseCumAccuracy;
	}

	public BetaDistributionImpl(double alpha ,double beta) {
		this(alpha, beta, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
	}

	@java.lang.Deprecated
	public void setAlpha(double alpha) {
		this.alpha = alpha;
		z = java.lang.Double.NaN;
	}

	public double getAlpha() {
		return alpha;
	}

	@java.lang.Deprecated
	public void setBeta(double beta) {
		this.beta = beta;
		z = java.lang.Double.NaN;
	}

	public double getBeta() {
		return beta;
	}

	private void recomputeZ() {
		if (java.lang.Double.isNaN(z)) {
			z = ((org.apache.commons.math.special.Gamma.logGamma(alpha)) + (org.apache.commons.math.special.Gamma.logGamma(beta))) - (org.apache.commons.math.special.Gamma.logGamma(((alpha) + (beta))));
		} 
	}

	public double density(java.lang.Double x) {
		return density(x.doubleValue());
	}

	public double density(double x) {
		recomputeZ();
		if ((x < 0) || (x > 1)) {
			return 0;
		} else {
			if (x == 0) {
				if ((alpha) < 1) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("Cannot compute beta density at 0 when alpha = {0,number}", alpha);
				} 
				return 0;
			} else {
				if (x == 1) {
					if ((beta) < 1) {
						throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("Cannot compute beta density at 1 when beta = %.3g", beta);
					} 
					return 0;
				} else {
					double logX = java.lang.Math.log(x);
					double log1mX = java.lang.Math.log1p(-x);
					return java.lang.Math.exp((((((alpha) - 1) * logX) + (((beta) - 1) * log1mX)) - (z)));
				}
			}
		}
	}

	@java.lang.Override
	public double inverseCumulativeProbability(double p) throws org.apache.commons.math.MathException {
		if (p == 0) {
			return 0;
		} else {
			if (p == 1) {
				return 1;
			} else {
				return super.inverseCumulativeProbability(p);
			}
		}
	}

	@java.lang.Override
	protected double getInitialDomain(double p) {
		return p;
	}

	@java.lang.Override
	protected double getDomainLowerBound(double p) {
		return 0;
	}

	@java.lang.Override
	protected double getDomainUpperBound(double p) {
		return 1;
	}

	public double cumulativeProbability(double x) throws org.apache.commons.math.MathException {
		if (x <= 0) {
			return 0;
		} else {
			if (x >= 1) {
				return 1;
			} else {
				return org.apache.commons.math.special.Beta.regularizedBeta(x, alpha, beta);
			}
		}
	}

	@java.lang.Override
	public double cumulativeProbability(double x0, double x1) throws org.apache.commons.math.MathException {
		return (cumulativeProbability(x1)) - (cumulativeProbability(x0));
	}

	@java.lang.Override
	protected double getSolverAbsoluteAccuracy() {
		return solverAbsoluteAccuracy;
	}
}

