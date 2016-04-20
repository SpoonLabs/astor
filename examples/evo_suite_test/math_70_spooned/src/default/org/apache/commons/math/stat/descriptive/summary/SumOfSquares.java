package org.apache.commons.math.stat.descriptive.summary;


public class SumOfSquares extends org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic implements java.io.Serializable {
	private static final long serialVersionUID = 1460986908574398008L;

	private long n;

	private double value;

	public SumOfSquares() {
		n = 0;
		value = java.lang.Double.NaN;
	}

	public SumOfSquares(org.apache.commons.math.stat.descriptive.summary.SumOfSquares original) {
		org.apache.commons.math.stat.descriptive.summary.SumOfSquares.copy(original, this);
	}

	@java.lang.Override
	public void increment(final double d) {
		if ((n) == 0) {
			value = d * d;
		} else {
			value += d * d;
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
		double sumSq = java.lang.Double.NaN;
		if (test(values, begin, length)) {
			sumSq = 0.0;
			for (int i = begin ; i < (begin + length) ; i++) {
				sumSq += (values[i]) * (values[i]);
			}
		} 
		return sumSq;
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.summary.SumOfSquares copy() {
		org.apache.commons.math.stat.descriptive.summary.SumOfSquares result = new org.apache.commons.math.stat.descriptive.summary.SumOfSquares();
		org.apache.commons.math.stat.descriptive.summary.SumOfSquares.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.summary.SumOfSquares source, org.apache.commons.math.stat.descriptive.summary.SumOfSquares dest) {
		dest.n = source.n;
		dest.value = source.value;
	}
}

