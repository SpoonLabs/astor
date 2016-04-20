package org.apache.commons.math.stat.descriptive.summary;


public class SumOfLogs extends org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic implements java.io.Serializable {
	private static final long serialVersionUID = -370076995648386763L;

	private int n;

	private double value;

	public SumOfLogs() {
		value = 0.0;
		n = 0;
	}

	public SumOfLogs(org.apache.commons.math.stat.descriptive.summary.SumOfLogs original) {
		org.apache.commons.math.stat.descriptive.summary.SumOfLogs.copy(original, this);
	}

	@java.lang.Override
	public void increment(final double d) {
		value += java.lang.Math.log(d);
		(n)++;
	}

	@java.lang.Override
	public double getResult() {
		if ((n) > 0) {
			return value;
		} else {
			return java.lang.Double.NaN;
		}
	}

	public long getN() {
		return n;
	}

	@java.lang.Override
	public void clear() {
		value = 0.0;
		n = 0;
	}

	@java.lang.Override
	public double evaluate(final double[] values, final int begin, final int length) {
		double sumLog = java.lang.Double.NaN;
		if (test(values, begin, length)) {
			sumLog = 0.0;
			for (int i = begin ; i < (begin + length) ; i++) {
				sumLog += java.lang.Math.log(values[i]);
			}
		} 
		return sumLog;
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.summary.SumOfLogs copy() {
		org.apache.commons.math.stat.descriptive.summary.SumOfLogs result = new org.apache.commons.math.stat.descriptive.summary.SumOfLogs();
		org.apache.commons.math.stat.descriptive.summary.SumOfLogs.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.summary.SumOfLogs source, org.apache.commons.math.stat.descriptive.summary.SumOfLogs dest) {
		dest.n = source.n;
		dest.value = source.value;
	}
}

