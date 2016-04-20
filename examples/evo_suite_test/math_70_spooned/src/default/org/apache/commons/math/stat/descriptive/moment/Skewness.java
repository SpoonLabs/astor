package org.apache.commons.math.stat.descriptive.moment;


public class Skewness extends org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic implements java.io.Serializable {
	private static final long serialVersionUID = 7101857578996691352L;

	protected org.apache.commons.math.stat.descriptive.moment.ThirdMoment moment = null;

	protected boolean incMoment;

	public Skewness() {
		incMoment = true;
		moment = new org.apache.commons.math.stat.descriptive.moment.ThirdMoment();
	}

	public Skewness(final org.apache.commons.math.stat.descriptive.moment.ThirdMoment m3) {
		incMoment = false;
		this.moment = m3;
	}

	public Skewness(org.apache.commons.math.stat.descriptive.moment.Skewness original) {
		org.apache.commons.math.stat.descriptive.moment.Skewness.copy(original, this);
	}

	@java.lang.Override
	public void increment(final double d) {
		if (incMoment) {
			moment.increment(d);
		} 
	}

	@java.lang.Override
	public double getResult() {
		if ((moment.n) < 3) {
			return java.lang.Double.NaN;
		} 
		double variance = (moment.m2) / ((moment.n) - 1);
		if (variance < 1.0E-19) {
			return 0.0;
		} else {
			double n0 = moment.getN();
			return (n0 * (moment.m3)) / ((((n0 - 1) * (n0 - 2)) * (java.lang.Math.sqrt(variance))) * variance);
		}
	}

	public long getN() {
		return moment.getN();
	}

	@java.lang.Override
	public void clear() {
		if (incMoment) {
			moment.clear();
		} 
	}

	@java.lang.Override
	public double evaluate(final double[] values, final int begin, final int length) {
		double skew = java.lang.Double.NaN;
		if ((test(values, begin, length)) && (length > 2)) {
			org.apache.commons.math.stat.descriptive.moment.Mean mean = new org.apache.commons.math.stat.descriptive.moment.Mean();
			double m = mean.evaluate(values, begin, length);
			double accum = 0.0;
			double accum2 = 0.0;
			for (int i = begin ; i < (begin + length) ; i++) {
				final double d = (values[i]) - m;
				accum += d * d;
				accum2 += d;
			}
			final double variance = (accum - ((accum2 * accum2) / length)) / (length - 1);
			double accum3 = 0.0;
			for (int i = begin ; i < (begin + length) ; i++) {
				final double d = (values[i]) - m;
				accum3 += (d * d) * d;
			}
			accum3 /= variance * (java.lang.Math.sqrt(variance));
			double n0 = length;
			skew = (n0 / ((n0 - 1) * (n0 - 2))) * accum3;
		} 
		return skew;
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.moment.Skewness copy() {
		org.apache.commons.math.stat.descriptive.moment.Skewness result = new org.apache.commons.math.stat.descriptive.moment.Skewness();
		org.apache.commons.math.stat.descriptive.moment.Skewness.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.moment.Skewness source, org.apache.commons.math.stat.descriptive.moment.Skewness dest) {
		dest.moment = new org.apache.commons.math.stat.descriptive.moment.ThirdMoment(source.moment.copy());
		dest.incMoment = source.incMoment;
	}
}

