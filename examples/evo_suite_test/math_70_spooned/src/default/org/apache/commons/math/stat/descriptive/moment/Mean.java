package org.apache.commons.math.stat.descriptive.moment;


public class Mean extends org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic implements java.io.Serializable , org.apache.commons.math.stat.descriptive.WeightedEvaluation {
	private static final long serialVersionUID = -1296043746617791564L;

	protected org.apache.commons.math.stat.descriptive.moment.FirstMoment moment;

	protected boolean incMoment;

	public Mean() {
		incMoment = true;
		moment = new org.apache.commons.math.stat.descriptive.moment.FirstMoment();
	}

	public Mean(final org.apache.commons.math.stat.descriptive.moment.FirstMoment m1) {
		this.moment = m1;
		incMoment = false;
	}

	public Mean(org.apache.commons.math.stat.descriptive.moment.Mean original) {
		org.apache.commons.math.stat.descriptive.moment.Mean.copy(original, this);
	}

	@java.lang.Override
	public void increment(final double d) {
		if (incMoment) {
			moment.increment(d);
		} 
	}

	@java.lang.Override
	public void clear() {
		if (incMoment) {
			moment.clear();
		} 
	}

	@java.lang.Override
	public double getResult() {
		return moment.m1;
	}

	public long getN() {
		return moment.getN();
	}

	@java.lang.Override
	public double evaluate(final double[] values, final int begin, final int length) {
		if (test(values, begin, length)) {
			org.apache.commons.math.stat.descriptive.summary.Sum sum = new org.apache.commons.math.stat.descriptive.summary.Sum();
			double sampleSize = length;
			double xbar = (sum.evaluate(values, begin, length)) / sampleSize;
			double correction = 0;
			for (int i = begin ; i < (begin + length) ; i++) {
				correction += (values[i]) - xbar;
			}
			return xbar + (correction / sampleSize);
		} 
		return java.lang.Double.NaN;
	}

	public double evaluate(final double[] values, final double[] weights, final int begin, final int length) {
		if (test(values, weights, begin, length)) {
			org.apache.commons.math.stat.descriptive.summary.Sum sum = new org.apache.commons.math.stat.descriptive.summary.Sum();
			double sumw = sum.evaluate(weights, begin, length);
			double xbarw = (sum.evaluate(values, weights, begin, length)) / sumw;
			double correction = 0;
			for (int i = begin ; i < (begin + length) ; i++) {
				correction += (weights[i]) * ((values[i]) - xbarw);
			}
			return xbarw + (correction / sumw);
		} 
		return java.lang.Double.NaN;
	}

	public double evaluate(final double[] values, final double[] weights) {
		return evaluate(values, weights, 0, values.length);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.moment.Mean copy() {
		org.apache.commons.math.stat.descriptive.moment.Mean result = new org.apache.commons.math.stat.descriptive.moment.Mean();
		org.apache.commons.math.stat.descriptive.moment.Mean.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.moment.Mean source, org.apache.commons.math.stat.descriptive.moment.Mean dest) {
		dest.incMoment = source.incMoment;
		dest.moment = source.moment.copy();
	}
}

