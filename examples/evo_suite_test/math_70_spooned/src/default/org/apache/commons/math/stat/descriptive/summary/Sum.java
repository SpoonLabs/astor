package org.apache.commons.math.stat.descriptive.summary;


public class Sum extends org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic implements java.io.Serializable {
	private static final long serialVersionUID = -8231831954703408316L;

	private long n;

	private double value;

	public Sum() {
		n = 0;
		value = java.lang.Double.NaN;
	}

	public Sum(org.apache.commons.math.stat.descriptive.summary.Sum original) {
		org.apache.commons.math.stat.descriptive.summary.Sum.copy(original, this);
	}

	@java.lang.Override
	public void increment(final double d) {
		if ((n) == 0) {
			value = d;
		} else {
			value += d;
		}
		(n)++;
	}

	@java.lang.Override
	public double getResult() {
		return value;
	}

	public long getN() {
		return n;
	}

	@java.lang.Override
	public void clear() {
		value = java.lang.Double.NaN;
		n = 0;
	}

	@java.lang.Override
	public double evaluate(final double[] values, final int begin, final int length) {
		double sum = java.lang.Double.NaN;
		if (test(values, begin, length)) {
			sum = 0.0;
			for (int i = begin ; i < (begin + length) ; i++) {
				sum += values[i];
			}
		} 
		return sum;
	}

	public double evaluate(final double[] values, final double[] weights, final int begin, final int length) {
		double sum = java.lang.Double.NaN;
		if (test(values, weights, begin, length)) {
			sum = 0.0;
			for (int i = begin ; i < (begin + length) ; i++) {
				sum += (values[i]) * (weights[i]);
			}
		} 
		return sum;
	}

	public double evaluate(final double[] values, final double[] weights) {
		return evaluate(values, weights, 0, values.length);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.summary.Sum copy() {
		org.apache.commons.math.stat.descriptive.summary.Sum result = new org.apache.commons.math.stat.descriptive.summary.Sum();
		org.apache.commons.math.stat.descriptive.summary.Sum.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.summary.Sum source, org.apache.commons.math.stat.descriptive.summary.Sum dest) {
		dest.n = source.n;
		dest.value = source.value;
	}
}

