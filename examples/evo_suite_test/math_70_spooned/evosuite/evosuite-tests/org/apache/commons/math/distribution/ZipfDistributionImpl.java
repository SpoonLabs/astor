package org.apache.commons.math.distribution;


public class ZipfDistributionImpl extends org.apache.commons.math.distribution.AbstractIntegerDistribution implements java.io.Serializable , org.apache.commons.math.distribution.ZipfDistribution {
	private static final long serialVersionUID = -140627372283420404L;

	private int numberOfElements;

	private double exponent;

	public ZipfDistributionImpl(final int numberOfElements ,final double exponent) throws java.lang.IllegalArgumentException {
		setNumberOfElementsInternal(numberOfElements);
		setExponentInternal(exponent);
	}

	public int getNumberOfElements() {
		return numberOfElements;
	}

	@java.lang.Deprecated
	public void setNumberOfElements(final int n) {
		setNumberOfElementsInternal(n);
	}

	private void setNumberOfElementsInternal(final int n) throws java.lang.IllegalArgumentException {
		if (n <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid number of elements {0} (must be positive)", n);
		} 
		this.numberOfElements = n;
	}

	public double getExponent() {
		return exponent;
	}

	@java.lang.Deprecated
	public void setExponent(final double s) {
		setExponentInternal(s);
	}

	private void setExponentInternal(final double s) throws java.lang.IllegalArgumentException {
		if (s <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid exponent {0} (must be positive)", s);
		} 
		this.exponent = s;
	}

	public double probability(final int x) {
		if ((x <= 0) || (x > (numberOfElements))) {
			return 0.0;
		} 
		return (1.0 / (java.lang.Math.pow(x, exponent))) / (generalizedHarmonic(numberOfElements, exponent));
	}

	@java.lang.Override
	public double cumulativeProbability(final int x) {
		if (x <= 0) {
			return 0.0;
		} else {
			if (x >= (numberOfElements)) {
				return 1.0;
			} 
		}
		return (generalizedHarmonic(x, exponent)) / (generalizedHarmonic(numberOfElements, exponent));
	}

	@java.lang.Override
	protected int getDomainLowerBound(final double p) {
		return 0;
	}

	@java.lang.Override
	protected int getDomainUpperBound(final double p) {
		return numberOfElements;
	}

	private double generalizedHarmonic(final int n, final double m) {
		double value = 0;
		for (int k = n ; k > 0 ; --k) {
			value += 1.0 / (java.lang.Math.pow(k, m));
		}
		return value;
	}
}

