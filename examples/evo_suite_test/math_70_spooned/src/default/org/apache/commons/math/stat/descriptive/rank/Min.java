package org.apache.commons.math.stat.descriptive.rank;


public class Min extends org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic implements java.io.Serializable {
	private static final long serialVersionUID = -2941995784909003131L;

	private long n;

	private double value;

	public Min() {
		n = 0;
		value = java.lang.Double.NaN;
	}

	public Min(org.apache.commons.math.stat.descriptive.rank.Min original) {
		org.apache.commons.math.stat.descriptive.rank.Min.copy(original, this);
	}

	@java.lang.Override
	public void increment(final double d) {
		if ((d < (value)) || (java.lang.Double.isNaN(value))) {
			value = d;
		} 
		(n)++;
	}

	@java.lang.Override
	public void clear() {
		value = java.lang.Double.NaN;
		n = 0;
	}

	@java.lang.Override
	public double getResult() {
		return value;
	}

	public long getN() {
		return n;
	}

	@java.lang.Override
	public double evaluate(final double[] values, final int begin, final int length) {
		double min = java.lang.Double.NaN;
		if (test(values, begin, length)) {
			min = values[begin];
			for (int i = begin ; i < (begin + length) ; i++) {
				if (!(java.lang.Double.isNaN(values[i]))) {
					min = min < (values[i]) ? min : values[i];
				} 
			}
		} 
		return min;
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.rank.Min copy() {
		org.apache.commons.math.stat.descriptive.rank.Min result = new org.apache.commons.math.stat.descriptive.rank.Min();
		org.apache.commons.math.stat.descriptive.rank.Min.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.rank.Min source, org.apache.commons.math.stat.descriptive.rank.Min dest) {
		dest.n = source.n;
		dest.value = source.value;
	}
}

