package org.apache.commons.math.stat.descriptive.rank;


public class Percentile extends org.apache.commons.math.stat.descriptive.AbstractUnivariateStatistic implements java.io.Serializable {
	private static final long serialVersionUID = -8091216485095130416L;

	private double quantile = 0.0;

	public Percentile() {
		this(50.0);
	}

	public Percentile(final double p) {
		setQuantile(p);
	}

	public Percentile(org.apache.commons.math.stat.descriptive.rank.Percentile original) {
		org.apache.commons.math.stat.descriptive.rank.Percentile.copy(original, this);
	}

	public double evaluate(final double[] values, final double p) {
		test(values, 0, 0);
		return evaluate(values, 0, values.length, p);
	}

	@java.lang.Override
	public double evaluate(final double[] values, final int start, final int length) {
		return evaluate(values, start, length, quantile);
	}

	public double evaluate(final double[] values, final int begin, final int length, final double p) {
		test(values, begin, length);
		if ((p > 100) || (p <= 0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("out of bounds quantile value: {0}, must be in (0, 100]", p);
		} 
		if (length == 0) {
			return java.lang.Double.NaN;
		} 
		if (length == 1) {
			return values[begin];
		} 
		double n = length;
		double pos = (p * (n + 1)) / 100;
		double fpos = java.lang.Math.floor(pos);
		int intPos = ((int)(fpos));
		double dif = pos - fpos;
		double[] sorted = new double[length];
		java.lang.System.arraycopy(values, begin, sorted, 0, length);
		java.util.Arrays.sort(sorted);
		if (pos < 1) {
			return sorted[0];
		} 
		if (pos >= n) {
			return sorted[(length - 1)];
		} 
		double lower = sorted[(intPos - 1)];
		double upper = sorted[intPos];
		return lower + (dif * (upper - lower));
	}

	public double getQuantile() {
		return quantile;
	}

	public void setQuantile(final double p) {
		if ((p <= 0) || (p > 100)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("out of bounds quantile value: {0}, must be in (0, 100]", p);
		} 
		quantile = p;
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.rank.Percentile copy() {
		org.apache.commons.math.stat.descriptive.rank.Percentile result = new org.apache.commons.math.stat.descriptive.rank.Percentile();
		org.apache.commons.math.stat.descriptive.rank.Percentile.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.rank.Percentile source, org.apache.commons.math.stat.descriptive.rank.Percentile dest) {
		dest.quantile = source.quantile;
	}
}

