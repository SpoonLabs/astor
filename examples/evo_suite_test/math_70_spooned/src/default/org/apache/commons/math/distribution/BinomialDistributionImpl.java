package org.apache.commons.math.distribution;


public class BinomialDistributionImpl extends org.apache.commons.math.distribution.AbstractIntegerDistribution implements java.io.Serializable , org.apache.commons.math.distribution.BinomialDistribution {
	private static final long serialVersionUID = 6751309484392813623L;

	private int numberOfTrials;

	private double probabilityOfSuccess;

	public BinomialDistributionImpl(int trials ,double p) {
		super();
		setNumberOfTrialsInternal(trials);
		setProbabilityOfSuccessInternal(p);
	}

	public int getNumberOfTrials() {
		return numberOfTrials;
	}

	public double getProbabilityOfSuccess() {
		return probabilityOfSuccess;
	}

	@java.lang.Deprecated
	public void setNumberOfTrials(int trials) {
		setNumberOfTrialsInternal(trials);
	}

	private void setNumberOfTrialsInternal(int trials) {
		if (trials < 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("number of trials must be non-negative ({0})", trials);
		} 
		numberOfTrials = trials;
	}

	@java.lang.Deprecated
	public void setProbabilityOfSuccess(double p) {
		setProbabilityOfSuccessInternal(p);
	}

	private void setProbabilityOfSuccessInternal(double p) {
		if ((p < 0.0) || (p > 1.0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", p, 0.0, 1.0);
		} 
		probabilityOfSuccess = p;
	}

	@java.lang.Override
	protected int getDomainLowerBound(double p) {
		return -1;
	}

	@java.lang.Override
	protected int getDomainUpperBound(double p) {
		return numberOfTrials;
	}

	@java.lang.Override
	public double cumulativeProbability(int x) throws org.apache.commons.math.MathException {
		double ret;
		if (x < 0) {
			ret = 0.0;
		} else if (x >= (numberOfTrials)) {
			ret = 1.0;
		} else {
			ret = 1.0 - (org.apache.commons.math.special.Beta.regularizedBeta(getProbabilityOfSuccess(), (x + 1.0), ((numberOfTrials) - x)));
		}
		return ret;
	}

	public double probability(int x) {
		double ret;
		if ((x < 0) || (x > (numberOfTrials))) {
			ret = 0.0;
		} else {
			ret = java.lang.Math.exp(org.apache.commons.math.distribution.SaddlePointExpansion.logBinomialProbability(x, numberOfTrials, probabilityOfSuccess, (1.0 - (probabilityOfSuccess))));
		}
		return ret;
	}

	@java.lang.Override
	public int inverseCumulativeProbability(final double p) throws org.apache.commons.math.MathException {
		if (p == 0) {
			return -1;
		} 
		if (p == 1) {
			return java.lang.Integer.MAX_VALUE;
		} 
		return super.inverseCumulativeProbability(p);
	}
}

